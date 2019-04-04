package com.vlogonappv1.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.android.gms.drive.DriveFile
import com.google.android.gms.drive.MetadataChangeSet
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.AppApplication
import com.vlogonappv1.AppApplication.Companion.mSessionHolder
import com.vlogonappv1.R
import com.vlogonappv1.backup.RemoteBackup
import com.vlogonappv1.dataclass.ProgressDialogshow
import com.vlogonappv1.db.DBHelper
import com.vlogonappv1.twofactorverification.FirstStepMobileVerificationActivity
import kotlinx.android.synthetic.main.activity_restore_found.*
import kotlinx.android.synthetic.main.dialog_backup_restore.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.newpasswordsetdialog.*
import kotlinx.android.synthetic.main.password_dialoglayout.*
import kotlinx.android.synthetic.main.restoreforgetpasswordoptiondialog.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class RestoreFoundActivity : RemoteBackup() {

    override fun onDriveClientReady() {
        if (isBackup.equals("backup")) {


            startDriveBackup()
        } else if (isBackup.equals("restore")) {
            startDriveRestore()
        } else {

        }
    }

    private var db: DBHelper? = null
    private lateinit var auth: FirebaseAuth
    private var isBackup: String? = null
    private var emailLink: String = ""
    override lateinit var dialog: Dialog
    internal var flag = 0
    internal var checkdata = 0
    lateinit var Firestoredb: FirebaseFirestore
    var passwordencodedKey: String = ""
    private val TAG = "restorescreeen"

    companion object {

        var passworddialog: Dialog? = null
        var backupdialog: Dialog? = null
        var forgetoptiondialog: Dialog? = null
        var passwordesetdialog: Dialog? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore_found)

        auth = FirebaseAuth.getInstance()
        db = DBHelper(applicationContext)
        Firestoredb = FirebaseFirestore.getInstance()



        button_restore.clicks().subscribe {
            mSessionHolder.Backupname = "restore"
            forgetoptiondialog = ForgetPasswordOptionDialog(this@RestoreFoundActivity, mSessionHolder.Backupname)
            forgetoptiondialog!!.show()
        }
        button_skip.clicks().subscribe {

            val intent = Intent(this@RestoreFoundActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()
        }

        if (mSessionHolder.twostepverify == "true") {


            if (mSessionHolder.Backupname.equals("backup_Drive") || mSessionHolder.Backupname.equals("import_Drive") || mSessionHolder.Backupname.equals(
                    "none"
                ) || mSessionHolder.Backupname.equals("google") || mSessionHolder.Backupname.equals("restorebackup")
            ) {
                val intent = Intent(this@RestoreFoundActivity, BackupActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                finish()
            } else {
                passworddialog = PasswordDialog(this@RestoreFoundActivity, "restore")
                passworddialog!!.show()

            }
        }
        toolbar?.apply {

            tvToolbarTitle.text = "Restore Backup"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility=View.GONE

            setSupportActionBar(this)


        }


        if (mSessionHolder.User_ActivityName.equals("restoreactivity")) {
            val i = intent.extras
            try {
                emailLink = i!!.getString("emaillink")


                dialog = ProgressDialogshow.progressDialog(this@RestoreFoundActivity)
                dialog.show()
                if (auth.isSignInWithEmailLink(emailLink)) {
                    // Retrieve this from wherever you stored it


                    //mSessionHolder.User_OfficeEmailId

                    // The client SDK will parse the code from the link for you.
                    auth.signInWithEmailLink(mSessionHolder.User_PersonalEmailId, emailLink)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                dialog.dismiss()

                                mSessionHolder.User_ActivityName=""

                                isBackup = "restore"
                                signIn(isBackup!!)

                            } else {
                                dialog.dismiss()
                                Toast.makeText(
                                    applicationContext,
                                    "Error signing in with email link",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                        }
                }
            } catch (e: IllegalStateException) {

            }


        }else {
            GetSearchInRelsult()
        }
    }

    private fun GetSearchInRelsult() {

        main_layout.visibility=View.GONE
        secondlayout.visibility=View.GONE

        dialog = ProgressDialogshow.progressDialog(this)
        dialog.show()
        checkdata = 0
        Firestoredb.collection("PasswordSet").whereEqualTo("EmailId", AppApplication.mSessionHolder.User_Login)
            .whereEqualTo("Source", AppApplication.mSessionHolder.Source_login)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    for (document in task.result!!) {
                        //  Log.e("data", document.getId() + " => " + document.get("name"));
                        checkdata = 1
                    }
                    if (checkdata == 0) {

                        AppApplication.mSessionHolder.setpaswordornot = "false"
                        main_layout.visibility=View.VISIBLE
                        secondlayout.visibility=View.VISIBLE
                        textrestore.text="Restore Not Found"
                        button_restore.visibility=View.GONE

                    } else {
                        secondlayout.visibility=View.VISIBLE
                        main_layout.visibility=View.VISIBLE
                        //EnterpasswordDialog(backupname)
                        if (AppApplication.mSessionHolder.firsttimeload == "true") {


                            button_restore.visibility=View.VISIBLE
                            AppApplication.mSessionHolder.firsttimeload = "false"

                        } else {

                            AppApplication.mSessionHolder.firsttimeload = "false"

                        }

                        AppApplication.mSessionHolder.setpaswordornot = "true"

                    }
                    dialog.dismiss()


                } else {

                    dialog.dismiss()
                    Log.w("dasd", "Error getting documents.", task.exception)
                }
            }


    }

    fun ForgetPasswordOptionDialog(context: Context, backupname: String): Dialog {

        var option: String = ""

        val inflate = LayoutInflater.from(context).inflate(R.layout.restoreforgetpasswordoptiondialog, null)
        val optiondialog = Dialog(context)
        optiondialog.setContentView(inflate)
        optiondialog.setCancelable(false)
        optiondialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val window = optiondialog.window
        val wlp = window.attributes
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        optiondialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)


        optiondialog.forgetbuttoncancel.clicks().subscribe {
            optiondialog.dismiss()
        }
        optiondialog.radioforgetoption?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radiomobile -> option = "mobile"
                R.id.radioemail -> option = "email"

            }
        }

        optiondialog.buttonoksubmit.clicks().subscribe {

            optiondialog.dismiss()

            if (option.equals("mobile")) {
                mSessionHolder.User_ActivityName = "restoreactivity"
                val intent = Intent(this@RestoreFoundActivity, FirstStepMobileVerificationActivity::class.java)


                intent.putExtra("mobilenumber", mSessionHolder.User_Mobilenumber)
                intent.putExtra("countrycode", mSessionHolder.User_Countrycode)

                startActivity(intent)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                finish()

            } else {
                mSessionHolder.User_PersonalEmailId = mSessionHolder.User_Login
                mSessionHolder.User_ActivityName = "restoreactivity"

                sendSignInLink(mSessionHolder.User_Login)
            }

        }



        optiondialog.setOnKeyListener { dialog, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                optiondialog.dismiss()
                true
            } else {
                false
            }
        }


        return optiondialog
    }

    private fun sendSignInLink(email: String) {


        dialog = ProgressDialogshow.progressDialog(this)
        dialog.show()

        val settings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName(
                packageName,
                false, null/* minimum app version */
            )/* install if not available? */
            .setHandleCodeInApp(true)
            .setUrl("https://vlogonappv1.firebaseapp.com/emailSignInLink")
            .build()


        auth.sendSignInLinkToEmail(email, settings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Verification Link send your email address please verify your email address.",
                        Toast.LENGTH_LONG
                    ).show()

                    dialog.dismiss()

                } else {
                    val e = task.exception
                    Toast.makeText(applicationContext, "Could not send link", Toast.LENGTH_LONG).show()
                    if (e is FirebaseAuthInvalidCredentialsException) {

                        Toast.makeText(applicationContext, "Invalid email address.", Toast.LENGTH_LONG).show()
                    }
                    dialog.dismiss()
                }
            }


    }

    fun PasswordDialog(context: Context, backupname: String): Dialog {


        val inflate = LayoutInflater.from(context).inflate(R.layout.password_dialoglayout, null)
        val passworddialog = Dialog(context)
        passworddialog.setContentView(inflate)
        passworddialog.setCancelable(false)
        passworddialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val window = passworddialog.window
        val wlp = window.attributes
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        passworddialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )


        passworddialog.buttoncancel.clicks().subscribe {
            passworddialog.dismiss()
        }

        passworddialog.buttonok.clicks().subscribe {

            if (passworddialog.etpassword.text.toString().isEmpty()) {
                Toast.makeText(this@RestoreFoundActivity, "Password can't be blank", Toast.LENGTH_LONG).show()
            } else {
                passworddialog.dismiss()
                passwordencodedKey = String(Base64.encode(passworddialog.etpassword.text.toString().toByteArray(), 0))
                Enterpassword(passwordencodedKey, backupname)
            }

        }

        passworddialog.txtforgetpassword.clicks().subscribe {


            mSessionHolder.Backupname = backupname
            /*  val intent = Intent(this@MainActivity, EnterMobileNumberActivity::class.java)
              startActivity(intent)
              overridePendingTransition(R.anim.enter, R.anim.exit)
              finish()
              passworddialog.dismiss()*/
            passworddialog.dismiss()
            passwordesetdialog = PasswordResetDialog(this@RestoreFoundActivity, backupname)
            passwordesetdialog!!.show()
        }

        passworddialog.setOnKeyListener { dialog, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                passworddialog.dismiss()
                true
            } else {
                false
            }
        }


        return passworddialog
    }

    fun RestoreDialog(context: Context): Dialog {


        val inflate = LayoutInflater.from(context).inflate(R.layout.dialog_backup_restore, null)
        val restoredialog = Dialog(context)
        restoredialog.setContentView(inflate)
        restoredialog.setCancelable(false)
        restoredialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val window = restoredialog.window
        val wlp = window.attributes
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        restoredialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)


        restoredialog.dialog_backup_restore_button_cancel.clicks().subscribe {
            restoredialog.dismiss()
        }

        restoredialog.dialog_backup_restore_button_restore.clicks().subscribe {

            restoredialog.dismiss()
            mSessionHolder.Backupname = "restore"
            forgetoptiondialog =
                ForgetPasswordOptionDialog(this@RestoreFoundActivity, mSessionHolder.Backupname)
            forgetoptiondialog!!.show()


        }



        restoredialog.setOnKeyListener { dialog, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                restoredialog.dismiss()
                true
            } else {
                false
            }
        }


        return restoredialog
    }

    private fun Enterpassword(password: String, backupname: String) {
        flag = 0
        dialog = ProgressDialogshow.progressDialog(this@RestoreFoundActivity)
        dialog.show()


        Firestoredb.collection("PasswordSet").whereEqualTo("EmailId", mSessionHolder.User_Login)
            .whereEqualTo("Password", password)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        //  Log.e("data", document.getId() + " => " + document.get("name"));

                        flag = 1


                    }
                    if (flag == 0) {
                        Toast.makeText(
                            this@RestoreFoundActivity,
                            "Username or Password Is Incorrect",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        dialog.dismiss()

                        passworddialog = PasswordDialog(this@RestoreFoundActivity, "restore")
                        passworddialog!!.show()

                    } else {
                        dialog.dismiss()
                        mSessionHolder.twostepverify = "false"
                        mSessionHolder.Backupname = ""
                        /* if (backupname == "localBackup") {
                             val outFileName =
                                 Environment.getExternalStorageDirectory().toString() + File.separator + resources.getString(
                                     R.string.app_name
                                 ) + File.separator
                             localBackup!!.performBackup(db!!, outFileName)
                         } else if (backupname == "localRestore") {
                             localBackup!!.performRestore(db!!)
                         }
 */
                        //googleDriveService.connectToDrive(false)
                        isBackup = "restore"
                        signIn(isBackup!!)

                    }
                } else {
                    dialog.dismiss()
                    Log.e("dasd", "Error getting documents.", task.exception)
                }
            }


    }

    private fun startDriveRestore() {

        dialog = ProgressDialogshow.progressDialog(this)
        dialog.show()
        val query = Query.Builder()
            .addFilter(
                Filters.eq(
                    SearchableField.TITLE,
                    AppApplication.mSessionHolder.User_Login + AppApplication.mSessionHolder.User_Mobilenumber + ".db"
                )
            )
            .build()
        // [END drive_android_query_title]
        val queryTask = driveResourceClient!!
            .query(query)
            .addOnSuccessListener { meta ->
                if (meta.count > 0) {
                    val fileMeta = meta.get(0)

                } else {

                    // viewModel.toastMessage.value = R.string.message_no_backup_found
                }
            }.continueWithTask { task ->
                val meta = task.result
                if (meta!!.count == 0) {
                    return@continueWithTask null
                }

                val fileMeta = meta.get(0)
                val file = fileMeta.driveId.asDriveFile()
                driveResourceClient!!.openFile(file, DriveFile.MODE_READ_ONLY)
            }.addOnSuccessListener { driveContents ->

                Log.e("restore", driveContents.driveId.asDriveFile().toString())
                retrieveContents(driveContents.driveId.asDriveFile())

                driveResourceClient!!.discardContents(driveContents)
            }.addOnFailureListener { e ->

                dialog.dismiss()
                Log.e("fail", "Unable to read contents", e)
                //   Toast.makeText(activity, "Error on import", Toast.LENGTH_SHORT).show()

            }


    }

    private fun retrieveContents(file: DriveFile) {

        //DB Path
        val inFileName = this@RestoreFoundActivity.getDatabasePath(DBHelper.DATABASE_NAME).toString()

        val openFileTask = driveResourceClient!!.openFile(file, DriveFile.MODE_READ_ONLY)

        openFileTask
            .continueWithTask { task ->
                val contents = task.result
                try {
                    val parcelFileDescriptor = contents!!.parcelFileDescriptor
                    val fileInputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)

                    // Open the empty db as the output stream
                    val output = FileOutputStream(inFileName)

                    // Transfer bytes from the inputfile to the outputfile
                    val buffer = ByteArray(1024)
                    fileInputStream.use { input ->
                        output.use { fileOut ->

                            while (true) {
                                val length = input.read(buffer)
                                if (length <= 0)
                                    break
                                fileOut.write(buffer, 0, length)
                            }

                        }
                    }

                    // Close the streams
                    output.flush()
                    output.close()
                    fileInputStream.close()

                    dialog.dismiss()


                    button_skip.text="Next"
                    Toast.makeText(this@RestoreFoundActivity, "Restore completed", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error on import", Toast.LENGTH_SHORT).show()

                }

                driveResourceClient!!.discardContents(contents!!)

            }
            .addOnFailureListener { e ->

                Toast.makeText(this, "Error on import", Toast.LENGTH_SHORT).show()

            }
    }

    private fun startDriveBackup() {
        dialog = ProgressDialogshow.progressDialog(this@RestoreFoundActivity)
        dialog.show()

        val inFileName = this@RestoreFoundActivity.getDatabasePath(DBHelper.DATABASE_NAME).toString()

        val query = Query.Builder()
            .addFilter(
                Filters.eq(
                    SearchableField.TITLE,
                    AppApplication.mSessionHolder.User_Login + AppApplication.mSessionHolder.User_Mobilenumber + ".db"
                )
            )
            .build()
        // [END drive_android_query_title]
        val queryTask = driveResourceClient!!
            .query(query)
            .addOnSuccessListener { meta ->
                if (meta.count > 0) {
                    val fileMeta = meta.get(0)

                } else {

                    // viewModel.toastMessage.value = R.string.message_no_backup_found
                }
            }.continueWithTask { task ->
                val meta = task.result
                if (meta!!.count == 0) {
                    return@continueWithTask null
                }

                val fileMeta = meta.get(0)
                val file = fileMeta.driveId.asDriveFile()
                driveResourceClient!!.openFile(file, DriveFile.MODE_READ_ONLY)
            }.addOnSuccessListener { driveContents ->


                driveResourceClient!!
                    .delete(driveContents.driveId.asDriveFile())
                    .addOnSuccessListener(
                        this
                    ) { aVoid ->
                        startbackupnew()
                    }
                    .addOnFailureListener(this) { e ->
                        Log.e(TAG, "Unable to delete file", e)


                    }

                driveResourceClient!!.discardContents(driveContents)
            }.addOnFailureListener { e ->


                startbackupnew()
                //   Toast.makeText(activity, "Error on import", Toast.LENGTH_SHORT).show()

            }


    }

    fun startbackupnew() {

        val rootFolderTask = driveResourceClient!!.rootFolder
        //  val appFolderTask = driveResourceClient!!.appFolder
        val createContentsTask = driveResourceClient!!.createContents()
        Tasks.whenAll(rootFolderTask, createContentsTask)
            .continueWithTask { task ->

                val parent = rootFolderTask.result
                val contents = createContentsTask.result

                val inFileName = this@RestoreFoundActivity.getDatabasePath(DBHelper.DATABASE_NAME).toString()

                try {
                    val dbFile = File(inFileName)
                    val fis = FileInputStream(dbFile)
                    val outputStream = contents!!.outputStream
                    val lenghtOfFile = dbFile.length()
                    // Transfer bytes from the inputfile to the outputfile
                    val buffer = ByteArray(1024)
                    var total: Long = 0

                    fis.use { input ->
                        outputStream.use { fileOut ->

                            while (true) {

                                val length = input.read(buffer)
                                total += length;
                                if (length <= 0) {
                                    break
                                }
                                val dl_progress = ((total * 100) / lenghtOfFile).toInt()
                                fileOut.write(buffer, 0, length)
                            }

                        }
                    }


                    // Close the streams
                    outputStream.flush()
                    outputStream.close()
                    fis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val changeSet = MetadataChangeSet.Builder()
                    .setTitle(AppApplication.mSessionHolder.User_Login + AppApplication.mSessionHolder.User_Mobilenumber + ".db")
                    .setMimeType("application/db")
                    .setStarred(true)
                    .build()

                driveResourceClient!!.createFile(parent!!, changeSet, contents)
            }
            .addOnSuccessListener(
                this
            ) { driveFile ->
                Log.e("success", "GoogleDrive create file")

                Toast.makeText(this@RestoreFoundActivity, "Backup completed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                mSessionHolder.backuptimeset = "false"

            }
            .addOnFailureListener(this) { e ->
                Log.e(TAG, "Unable to create file", e)


            }
    }

    fun PasswordResetDialog(context: Context, backupname: String): Dialog {


        val inflate = LayoutInflater.from(context).inflate(R.layout.newpasswordsetdialog, null)
        val resetdialog = Dialog(context)
        resetdialog.setContentView(inflate)
        resetdialog.setCancelable(false)
        resetdialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val window = resetdialog.window
        val wlp = window.attributes
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        resetdialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )


        resetdialog.newpasswordcancel.clicks().subscribe {
            resetdialog.dismiss()
        }

        resetdialog.newpasswordsubmit.clicks().subscribe {


            if (resetdialog.etnewpassword.text.toString().isEmpty()) {
                Toast.makeText(this@RestoreFoundActivity, "Password can't be blank", Toast.LENGTH_LONG).show()
            } else if (resetdialog.etnewconfrimpassword.text.toString().isEmpty()) {
                Toast.makeText(this@RestoreFoundActivity, "Confrim Password can't be blank", Toast.LENGTH_LONG).show()
            } else if (resetdialog.etnewconfrimpassword.text.toString() != resetdialog.etnewpassword.text.toString()) {
                Toast.makeText(this@RestoreFoundActivity, "Please Enter Valid Confirm Password", Toast.LENGTH_LONG)
                    .show()
            } else {
                resetdialog.dismiss()
                passwordencodedKey = String(Base64.encode(resetdialog.etnewpassword.text.toString().toByteArray(), 0))

                updatepassword(passwordencodedKey, backupname)
            }
        }


        resetdialog.setOnKeyListener { dialog, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                resetdialog.dismiss()
                true
            } else {
                false
            }
        }


        return resetdialog
    }

    private fun updatepassword(password: String, backupname: String) {

        dialog = ProgressDialogshow.progressDialog(this@RestoreFoundActivity)
        dialog.show()
        val setpassinfo = HashMap<String, Any>()
        setpassinfo["EmailId"] = mSessionHolder.User_Login
        setpassinfo["Source"] = mSessionHolder.Source_login
        setpassinfo["Phonenumber"] = mSessionHolder.User_Mobilenumber
        setpassinfo["Password"] = password

        var documentid: String? = null

        Firestoredb.collection("PasswordSet").whereEqualTo("EmailId", mSessionHolder.User_Login)
            .whereEqualTo("Source", mSessionHolder.Source_login)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        //  Log.e("data", document.getId() + " => " + document.get("name"));
                        flag = 1
                        documentid = document.id
                    }
                    if (flag == 0) {
                        Toast.makeText(
                            this@RestoreFoundActivity,
                            "Username or Password Is Incorrect",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        dialog.dismiss()
                    } else {
                        Firestoredb.collection("PasswordSet")
                            .document(documentid!!)
                            .update(setpassinfo)
                            .addOnSuccessListener {
                                mSessionHolder.twostepverify = "false"
                                mSessionHolder.Backupname = ""

                                dialog.dismiss()
                                isBackup = "restore"
                                signIn(isBackup!!)

                            }
                            .addOnFailureListener { e ->
                                Log.e("addded", "Error adding Register", e)
                                Toast.makeText(
                                    this@RestoreFoundActivity,
                                    "Register User could not be added",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    }
                } else {
                    dialog.dismiss()
                    Log.e("dasd", "Error getting documents.", task.exception)
                }
            }


    }

}
