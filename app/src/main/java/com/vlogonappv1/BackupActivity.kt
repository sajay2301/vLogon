package com.vlogonappv1

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.EditText
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
import com.vlogonappv1.dataclass.ProgressDialogshow
import com.vlogonappv1.service.Alarm
import com.vlogonappv1.twofactorverification.FirstStepMobileVerificationActivity
import com.vlogonappv1.backup.LocalBackup
import com.vlogonappv1.backup.RemoteBackup
import com.vlogonappv1.db.DBHelper
import kotlinx.android.synthetic.main.backup_drive_activity.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import kotlinx.android.synthetic.main.newpasswordsetdialog.*
import kotlinx.android.synthetic.main.password_dialoglayout.*
import kotlinx.android.synthetic.main.restoreforgetpasswordoptiondialog.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class BackupActivity : RemoteBackup() {


    private var doubleBackToExitPressedOnce = false
    var profilepic: String? = null
    private var localBackup: LocalBackup? = null
    private var remoteBackup: RemoteBackup? = null
    private var db: DBHelper? = null
    private lateinit var auth: FirebaseAuth
    private var isBackup: String = ""


    override lateinit var dialog: Dialog
    internal var flag = 0
    internal var checkdata = 0
    private val REQUEST_CODE_PICKER = 2
    private val REQUEST_CODE_PICKER_FOLDER = 4
    lateinit var Firestoredb: FirebaseFirestore
    private val TAG = "vlogon_drive_backup"
    private val BACKUP_FOLDER_KEY = "backup_folder"
    var passwordencodedKey: String = ""
    var alarm: Alarm? = null

    companion object {


        val REQUEST_CODE_PERMISSIONS = 2
        val REQUEST_CODE_SIGN_IN = 0
        val REQUEST_CODE_OPENING = 1
        val REQUEST_CODE_CREATION = 2
        var passworddialog: Dialog? = null
        val RC_SIGN_IN = 9001
        var forgetoptiondialog: Dialog? = null
        var passwordesetdialog: Dialog? = null


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.backup_drive_activity)



        auth = FirebaseAuth.getInstance()
        db = DBHelper(applicationContext)
        Firestoredb = FirebaseFirestore.getInstance()



        alarm = Alarm(this)

        toolbar?.apply {

            tvToolbarTitle.text = "Backup And Restore"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }

        if (AppApplication.mSessionHolder.twostepverify == "true") {

            if (AppApplication.mSessionHolder.Backupname.equals("google")) {

                isBackup = "google"
                signIn(isBackup)
            } else if (AppApplication.mSessionHolder.Backupname.equals("restorebackup")) {
                AppApplication.mSessionHolder.Backupname = ""
                passworddialog = PasswordDialog(this@BackupActivity, "restore")
                passworddialog!!.show()
            } else {

                passwordesetdialog = PasswordResetDialog(this@BackupActivity, AppApplication.mSessionHolder.Backupname)
                passwordesetdialog!!.show()

            }

            AppApplication.mSessionHolder.twostepverify = "false"
        }

        button_backup.clicks().subscribe {

            if (AppApplication.mSessionHolder.accountname.equals("")) {
                Toast.makeText(this@BackupActivity, "Setup Google Drive", Toast.LENGTH_SHORT).show()
            } else {
                GetSearchInRelsult("backup_Drive")
            }
        }
        button_restore.clicks().subscribe {
            if (AppApplication.mSessionHolder.accountname.equals("")) {
                Toast.makeText(this@BackupActivity, "Setup Google Drive", Toast.LENGTH_SHORT).show()
            } else {
                GetSearchInRelsult("import_Drive")
            }

        }
        activity_backup_drive_button_folder.clicks().subscribe {
            if (AppApplication.mSessionHolder.accountname.equals("")) {

                AppApplication.mSessionHolder.Backupname = "google"
                forgetoptiondialog =
                        ForgetPasswordOptionDialog(this@BackupActivity, AppApplication.mSessionHolder.Backupname)
                forgetoptiondialog!!.show()
            } else {

            }

        }


        activity_backup_drive_textview_folder.text = AppApplication.mSessionHolder.setonnotset
        txtaccountname.text = AppApplication.mSessionHolder.accountname

        if (AppApplication.mSessionHolder.accountname.equals("")) {
            activity_backup_drive_textview_folder.text = "Not Set"

        }


        if (AppApplication.mSessionHolder.setpaswordornot.equals("false")) {

            txtpasswordset.text = "Set Password"
        } else {
            txtpasswordset.text = "Reset Password"
        }



        txtpasswordset.clicks().subscribe {


            if (txtpasswordset.text.equals("Set Password")) {

                setpasswordDialog("none")
            } else {
                AppApplication.mSessionHolder.Backupname = "none"
                forgetoptiondialog =
                        ForgetPasswordOptionDialog(this@BackupActivity, AppApplication.mSessionHolder.Backupname)
                forgetoptiondialog!!.show()
            }
        }

        try {
            if (AppApplication.mSessionHolder.alarmsetornot) {
                checkBoxdailybackup.isChecked = AppApplication.mSessionHolder.alarmsetornot
            }
        } catch (e: KotlinNullPointerException) {

        }

        checkBoxdailybackup.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppApplication.mSessionHolder.alarmsetornot = isChecked
                alarm!!.setAlarm(this@BackupActivity)
            } else {
                AppApplication.mSessionHolder.alarmsetornot = isChecked
                alarm!!.cancleAlarm(this@BackupActivity)
            }
        }

    }


    override fun onBackPressed() {
        val intent = Intent(this@BackupActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        finish()
        AppApplication.mSessionHolder.backupsetupornot = "false"

    }


    override fun onDriveClientReady() {

        if (isBackup.equals("backup")) {

            activity_backup_drive_textview_folder.text = AppApplication.mSessionHolder.setonnotset
            txtaccountname.text = AppApplication.mSessionHolder.accountname
            startDriveBackup()
        } else if (isBackup.equals("restore")) {
            startDriveRestore()


            activity_backup_drive_textview_folder.text = AppApplication.mSessionHolder.setonnotset
            txtaccountname.text = AppApplication.mSessionHolder.accountname
            AppApplication.mSessionHolder.Backupname = ""
            AppApplication.mSessionHolder.twostepverify = "false"
        } else if (isBackup.equals("google")) {
            AppApplication.mSessionHolder.Backupname = ""
            AppApplication.mSessionHolder.twostepverify = "false"
            activity_backup_drive_textview_folder.text = AppApplication.mSessionHolder.setonnotset
            txtaccountname.text = AppApplication.mSessionHolder.accountname
        }


    }


    private fun startDriveBackup() {

        val inFileName = this@BackupActivity.getDatabasePath(DBHelper.DATABASE_NAME).toString()

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
        packupprogressbar.visibility = View.VISIBLE
        backuptext.visibility = View.VISIBLE
        val rootFolderTask = driveResourceClient!!.rootFolder
        //  val appFolderTask = driveResourceClient!!.appFolder
        val createContentsTask = driveResourceClient!!.createContents()
        Tasks.whenAll(rootFolderTask, createContentsTask)
            .continueWithTask { task ->

                val parent = rootFolderTask.result
                val contents = createContentsTask.result

                val inFileName = this@BackupActivity.getDatabasePath(DBHelper.DATABASE_NAME).toString()

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
                                packupprogressbar.progress = dl_progress
                                backuptext.text = "" + ((total * 100) / lenghtOfFile).toInt() + "/" +
                                        packupprogressbar.max
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
                packupprogressbar.visibility = View.GONE
                backuptext.visibility = View.GONE
                Toast.makeText(this@BackupActivity, "Backup completed", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener(this) { e ->
                Log.e(TAG, "Unable to create file", e)


            }
    }

    private fun startDriveRestore() {

        /* dialog = ProgressDialogshow.progressDialog(this)
         dialog.show()*/
        val appFolderTask = driveResourceClient!!.appFolder
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


                Log.e("fail", "Unable to read contents", e)
                //   Toast.makeText(activity, "Error on import", Toast.LENGTH_SHORT).show()

            }


    }

    private fun retrieveContents(file: DriveFile) {
        restoreprogressbar.visibility = View.VISIBLE
        restoretext.visibility = View.VISIBLE
        //DB Path
        val inFileName = this@BackupActivity.getDatabasePath(DBHelper.DATABASE_NAME).toString()

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
                    var total: Long = 0
                    val lenghtOfFile = contents.parcelFileDescriptor.statSize

                    fileInputStream.use { input ->
                        output.use { fileOut ->

                            while (true) {
                                val length = input.read(buffer)
                                total += length;
                                if (length <= 0)
                                    break

                                val dl_progress = ((total * 100) / lenghtOfFile).toInt()
                                restoreprogressbar.progress = dl_progress
                                restoretext.text = "" + ((total * 100) / lenghtOfFile).toInt() + "/" +
                                        packupprogressbar.max
                                fileOut.write(buffer, 0, length)
                            }

                        }
                    }

                    // Close the streams
                    output.flush()
                    output.close()
                    fileInputStream.close()
                    restoreprogressbar.visibility = View.GONE
                    restoretext.visibility = View.GONE
                    dialog.dismiss()

                    Toast.makeText(this@BackupActivity, "Restore completed", Toast.LENGTH_SHORT).show()

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


    fun setpasswordDialog(backupname: String) {


        val builder = AlertDialog.Builder(this@BackupActivity)
        builder.setTitle("Set Password")
        val input = EditText(this@BackupActivity)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)
        builder.setPositiveButton("Ok") { dialog, which ->
            val m_Text = input.text.toString()
            passwordencodedKey = String(Base64.encode(m_Text.toByteArray(), 0))

            Setpassword(passwordencodedKey, backupname)


        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()

    }

    private fun Setpassword(password: String, backupname: String) {

        dialog = ProgressDialogshow.progressDialog(this@BackupActivity)
        dialog.show()
        val setpassinfo = HashMap<String, Any>()
        setpassinfo["EmailId"] = AppApplication.mSessionHolder.User_Login
        setpassinfo["Source"] = AppApplication.mSessionHolder.Source_login
        setpassinfo["Phonenumber"] = AppApplication.mSessionHolder.User_Mobilenumber
        setpassinfo["Password"] = password

        Firestoredb.collection("PasswordSet")
            .add(setpassinfo)
            .addOnSuccessListener {
                dialog.dismiss()
                when {
                    backupname.equals("localBackup") -> {
                        val outFileName =
                            Environment.getExternalStorageDirectory().toString() + File.separator + resources.getString(
                                R.string.app_name
                            ) + File.separator
                        localBackup!!.performBackup(db!!, outFileName)
                    }
                    backupname.equals("localRestore") -> localBackup!!.performRestore(db!!)
                    backupname.equals("backup_Drive") -> {
                        isBackup = "backup"

                        signIn(isBackup)
                    }
                    backupname.equals("import_Drive") -> {
                        isBackup = "restore"
                        signIn(isBackup)
                    }
                    backupname.equals("none") -> {

                        Toast.makeText(
                            this@BackupActivity,
                            "Password Set Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        AppApplication.mSessionHolder.setpaswordornot = "true"
                        txtpasswordset.text = "Reset Password"
                    }
                }

            }
            .addOnFailureListener { e ->
                Log.e("addded", "Error adding Register", e)
                Toast.makeText(
                    this@BackupActivity,
                    "Register User could not be added",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun updatepassword(password: String, backupname: String) {

        dialog = ProgressDialogshow.progressDialog(this@BackupActivity)
        dialog.show()
        val setpassinfo = HashMap<String, Any>()
        setpassinfo["EmailId"] = AppApplication.mSessionHolder.User_Login
        setpassinfo["Source"] = AppApplication.mSessionHolder.Source_login
        setpassinfo["Phonenumber"] = AppApplication.mSessionHolder.User_Mobilenumber
        setpassinfo["Password"] = password

        var documentid: String? = null

        Firestoredb.collection("PasswordSet").whereEqualTo("EmailId", AppApplication.mSessionHolder.User_Login)
            .whereEqualTo("Source", AppApplication.mSessionHolder.Source_login)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        //  Log.e("data", document.getId() + " => " + document.get("name"));
                        flag = 1
                        documentid = document.id
                    }
                    if (flag == 0) {
                        Toast.makeText(this@BackupActivity, "Username or Password Is Incorrect", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()


                    } else {
                        Firestoredb.collection("PasswordSet")
                            .document(documentid!!)
                            .update(setpassinfo)
                            .addOnSuccessListener {
                                AppApplication.mSessionHolder.twostepverify = "false"
                                AppApplication.mSessionHolder.Backupname = ""

                                dialog.dismiss()
                                when {
                                    backupname.equals("localBackup") -> {
                                        val outFileName =
                                            Environment.getExternalStorageDirectory().toString() + File.separator + resources.getString(
                                                R.string.app_name
                                            ) + File.separator
                                        localBackup!!.performBackup(db!!, outFileName)
                                    }
                                    backupname.equals("localRestore") -> localBackup!!.performRestore(db!!)
                                    backupname.equals("backup_Drive") -> {
                                        isBackup = "backup"
                                        signIn(isBackup)
                                    }
                                    backupname.equals("restore") -> {
                                        isBackup = "restore"
                                        signIn(isBackup)
                                    }
                                    backupname.equals("none") -> {
                                        Toast.makeText(
                                            this@BackupActivity,
                                            "Password Reset Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        AppApplication.mSessionHolder.setpaswordornot = "true"
                                        txtpasswordset.text = "Reset Password"
                                    }
                                }

                            }
                            .addOnFailureListener { e ->
                                Log.e("addded", "Error adding Register", e)
                                Toast.makeText(
                                    this@BackupActivity,
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

    private fun GetSearchInRelsult(backupname: String) {
        checkdata = 0
        dialog = ProgressDialogshow.progressDialog(this@BackupActivity)
        dialog.show()
        Firestoredb.collection("PasswordSet").whereEqualTo("EmailId", AppApplication.mSessionHolder.User_Login)
            .whereEqualTo("Source", AppApplication.mSessionHolder.Source_login)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dialog.dismiss()

                    for (document in task.result!!) {
                        //  Log.e("data", document.getId() + " => " + document.get("name"));

                        checkdata = 1


                    }
                    if (checkdata == 0) {
                        setpasswordDialog(backupname)
                    } else {
                        //EnterpasswordDialog(backupname)

                        when {

                            backupname.equals("backup_Drive") -> {
                                isBackup = "backup"

                                signIn(isBackup)
                            }
                            backupname.equals("import_Drive") -> {
                                /* isBackup = "restore"
                                 signIn(isBackup)
 */
                                AppApplication.mSessionHolder.Backupname = "restorebackup"
                                forgetoptiondialog = ForgetPasswordOptionDialog(
                                    this@BackupActivity,
                                    AppApplication.mSessionHolder.Backupname
                                )
                                forgetoptiondialog!!.show()
                            }
                        }

                    }


                } else {

                    setpasswordDialog(backupname)
                    Log.w("dasd", "Error getting documents.", task.exception)
                }
            }


    }

    fun EnterNewpasswordDialog(backupname: String) {


        val builder = AlertDialog.Builder(this@BackupActivity)
        builder.setTitle("Set New Password")
        val input = EditText(this@BackupActivity)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)
        builder.setPositiveButton("Ok") { dialog, which ->
            val m_Text = input.text.toString()
            passwordencodedKey = String(Base64.encode(m_Text.toByteArray(), 0))

            updatepassword(passwordencodedKey, backupname)


        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()

    }

    private fun Enterpassword(password: String, backupname: String) {
        flag = 0
        dialog = ProgressDialogshow.progressDialog(this@BackupActivity)
        dialog.show()


        Firestoredb.collection("PasswordSet").whereEqualTo("EmailId", AppApplication.mSessionHolder.User_Login)
            .whereEqualTo("Password", password)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        //  Log.e("data", document.getId() + " => " + document.get("name"));

                        flag = 1


                    }
                    if (flag == 0) {
                        Toast.makeText(this@BackupActivity, "Username or Password Is Incorrect", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()
                        passworddialog = PasswordDialog(this@BackupActivity, "restore")
                        passworddialog!!.show()
                    } else {
                        dialog.dismiss()
                        if (backupname == "localBackup") {
                            val outFileName =
                                Environment.getExternalStorageDirectory().toString() + File.separator + resources.getString(
                                    R.string.app_name
                                ) + File.separator
                            localBackup!!.performBackup(db!!, outFileName)
                        } else if (backupname == "localRestore") localBackup!!.performRestore(db!!)
                        else if (backupname == "backup_Drive") {
                            isBackup = "backup"
                            signIn(isBackup)
                        } else if (backupname == "import_Drive") {
                            isBackup = "restore"
                            signIn(isBackup)
                        } else if (backupname == "restorebackup") {
                            isBackup = "restore"
                            signIn(isBackup)
                        } else if (backupname == "restore") {
                            isBackup = "restore"
                            signIn(isBackup)
                        }


                    }
                } else {
                    dialog.dismiss()
                    Log.e("dasd", "Error getting documents.", task.exception)
                }
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
                Toast.makeText(this@BackupActivity, "Password can't be blank", Toast.LENGTH_LONG).show()
            } else if (resetdialog.etnewconfrimpassword.text.toString().isEmpty()) {
                Toast.makeText(this@BackupActivity, "Confrim Password can't be blank", Toast.LENGTH_LONG).show()
            }else if (resetdialog.etnewconfrimpassword.text.toString() != resetdialog.etnewpassword.text.toString()) {
                Toast.makeText(this@BackupActivity, "Please Enter Valid Confirm Password", Toast.LENGTH_LONG).show()
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
                Toast.makeText(this@BackupActivity, "Password can't be blank", Toast.LENGTH_LONG).show()
            } else {
                passworddialog.dismiss()
                passwordencodedKey = String(Base64.encode(passworddialog.etpassword.text.toString().toByteArray(), 0))
                Enterpassword(passwordencodedKey, backupname)
            }
        }

        passworddialog.txtforgetpassword.clicks().subscribe {
            passworddialog.dismiss()
            passwordesetdialog = PasswordResetDialog(this@BackupActivity, "restore")
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
                val intent = Intent(this@BackupActivity, FirstStepMobileVerificationActivity::class.java)


                intent.putExtra("mobilenumber", AppApplication.mSessionHolder.User_Mobilenumber)
                intent.putExtra("countrycode", AppApplication.mSessionHolder.User_Countrycode)

                startActivity(intent)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                finish()

            } else {
                AppApplication.mSessionHolder.User_PersonalEmailId = AppApplication.mSessionHolder.User_Login
                AppApplication.mSessionHolder.User_ActivityName = "emailaddressverification"

                sendSignInLink(AppApplication.mSessionHolder.User_Login)
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
}