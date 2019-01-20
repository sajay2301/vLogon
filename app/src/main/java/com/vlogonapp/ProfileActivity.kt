package com.vlogonapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonapp.AppApplication.Companion.mSessionHolder
import com.vlogonapp.Class.ImagePickerHelper
import com.vlogonapp.Class.ProgressDialogshow
import com.vlogonapp.Class.getPath
import com.vlogonapp.Database.AppDatabase
import com.vlogonapp.Database.entities.UserRegistration
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class ProfileActivity : AppCompatActivity() {

    private var imagePickerHelper: ImagePickerHelper? = null
    private var adImage: File? = null
    var profilepic: String? = null
    var gender: String? = null
    private var fileRequest = AD_IMAGE
    lateinit var dialog: Dialog
    private var emailLink: String = ""
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val POST_IMAGE = 0
        private const val AD_IMAGE = 1

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        toolbar?.apply {

            tvToolbarTitle.text = "Advance Profile"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
        auth = FirebaseAuth.getInstance()

        btnupdate.clicks().subscribe {

            if (etofficeemail.text.toString() != "" && mSessionHolder.User_officecolorcode == "") {
                Toast.makeText(
                    this@ProfileActivity,
                    "Please Verify Your Office Email ID",
                    Toast.LENGTH_SHORT

                ).show()
            } else {
                mSessionHolder.User_OfficeEmailId = ""
                mSessionHolder.User_ActivityName = ""

                mSessionHolder.User_officecolorcode = ""
                saveTask()
                //BackUpTask()
            }
        }


        txtcountrycode.clicks().subscribe {
            val intent = Intent(this, SelectCountryActivity::class.java)

            startActivityForResult(intent, 1)
        }

        radiofontsize?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radiofemale -> gender = "Female"
                R.id.radiomale -> gender = "Male"
                R.id.radioother -> gender = "Other"
            }
        }

        imagePickerHelper = ImagePickerHelper(this)
        userImageProfile.clicks().subscribe {

            imagePickerHelper?.selectOptionToLoadImage(AD_IMAGE)
        }

        officeemailverify.clicks().subscribe {
            val email = etofficeemail.text.toString()
            if (TextUtils.isEmpty(email)) {

                Toast.makeText(
                    this@ProfileActivity,
                    "Email must not be empty.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {


                dialog = ProgressDialogshow.progressDialog(this)
                dialog.show()
                mSessionHolder.User_OfficeEmailId = etofficeemail.text.toString()
                mSessionHolder.User_ActivityName = "Profile"

                sendSignInLink(etofficeemail.text.toString())
            }
        }


        //   checkIntent(intent)
        getTasks()
    }

    override fun onBackPressed() {

        mSessionHolder.User_officecolorcode = ""
        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()


    }

    private fun getTasks() {
        class GetTasks : AsyncTask<Void, Void, List<UserRegistration>>() {

            override fun doInBackground(vararg voids: Void): List<UserRegistration> {
                return AppDatabase
                    .getInstance(applicationContext)
                    .userregistrationDao()
                    .checkdata(mSessionHolder.User_Login, mSessionHolder.Source_login)
            }

            override fun onPostExecute(tasks: List<UserRegistration>) {
                super.onPostExecute(tasks)


                try {

                    etfisrtname.text = Editable.Factory.getInstance().newEditable(tasks[0].firstname)
                    etlastname.text = Editable.Factory.getInstance().newEditable(tasks[0].lastname)
                    etmobilenumber.text = Editable.Factory.getInstance().newEditable(tasks[0].mobilenumber)
                    etpersonalemail.text = Editable.Factory.getInstance().newEditable(tasks[0].personalemail)
                    etofficeemail.text = Editable.Factory.getInstance().newEditable(tasks[0].officeemail)
                    txtcountrycode.text = tasks[0].countrycode

                    profilepic = tasks[0].profilepic
                    Glide.with(applicationContext).load(profilepic)
                        .apply(
                            RequestOptions()
                                .placeholder(R.mipmap.ic_launcher_round)
                        )
                        .into(userImageProfile)

                    when {
                        tasks[0].gender.equals("Female") -> radiofontsize.check(R.id.radiofemale)
                        tasks[0].gender.equals("Male") -> radiofontsize.check(R.id.radiomale)
                        tasks[0].gender.equals("Other") -> radiofontsize.check(R.id.radioother)

                    }

                    etaddress.text = Editable.Factory.getInstance().newEditable(tasks[0].address)
                    etcity.text = Editable.Factory.getInstance().newEditable(tasks[0].city)
                    etcountry.text = Editable.Factory.getInstance().newEditable(tasks[0].country)

                    if (etpersonalemail.text.toString() != "") {
                        personalemailverify.setTextColor(Color.parseColor("#54B948"))
                        personalemailverify.text = "Verified"
                    }
                    if (etofficeemail.text.toString() != "") {
                        mSessionHolder.User_officecolorcode = "#54B948"
                        officeemailverify.setTextColor(Color.parseColor("#54B948"))
                        officeemailverify.text = "Verified"
                    }

                    if (mSessionHolder.User_ActivityName.equals("Profile")) {
                        val i = intent.extras
                        try {
                            emailLink = i!!.getString("emaillink")


                            dialog = ProgressDialogshow.progressDialog(this@ProfileActivity)
                            dialog.show()
                            if (auth.isSignInWithEmailLink(emailLink)) {
                                // Retrieve this from wherever you stored it


                                //mSessionHolder.User_OfficeEmailId

                                // The client SDK will parse the code from the link for you.
                                auth.signInWithEmailLink(mSessionHolder.User_OfficeEmailId, emailLink)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {

                                            dialog.dismiss()


                                            etofficeemail.text = Editable.Factory.getInstance()
                                                .newEditable(mSessionHolder.User_OfficeEmailId)
                                            officeemailverify.setTextColor(Color.parseColor("#54B948"))
                                            mSessionHolder.User_officecolorcode = "#54B948"
                                            officeemailverify.text = "Verified"


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

                    } else {

                    }

                } catch (e: IndexOutOfBoundsException) {

                }

            }
        }

        val gt = GetTasks()
        gt.execute()
    }


    private fun saveTask() {

        class SaveTask : AsyncTask<String, Int, String>() {

            override fun doInBackground(vararg params: String): String? {

                //creating a task

                //adding to database


                val database = AppDatabase.getInstance(this@ProfileActivity)


                val checkdata =
                    database.userregistrationDao().checkdata(mSessionHolder.User_Login, mSessionHolder.Source_login)
                if (checkdata.isEmpty()) {

                } else {

                    AppDatabase.getInstance(this@ProfileActivity).userregistrationDao().update(
                        etfisrtname.text.toString(),
                        etlastname.text.toString(),
                        txtcountrycode.text.toString(),
                        etmobilenumber.text.toString(),
                        etofficeemail.text.toString(),
                        profilepic.toString(),
                        gender.toString(),
                        etaddress.text.toString(),
                        etcity.text.toString(),
                        etcountry.text.toString(),
                        mSessionHolder.User_Login,
                        mSessionHolder.Source_login
                    )

                }

                return "string"
            }

            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finish()
                Toast.makeText(applicationContext, "Profile Update Successfully", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {


            Activity.RESULT_OK -> {
                when (requestCode) {
                    ImagePickerHelper.REQUEST_CAMERA -> {
                        when (imagePickerHelper?.mFile?.fileRequest) {

                            AD_IMAGE -> {
                                adImage = imagePickerHelper?.mFile?.mFile
                                loadImage(adImage, AD_IMAGE, Uri.fromFile(adImage))
                            }
                        }
                    }
                    ImagePickerHelper.SELECT_FILE -> {
                        when (imagePickerHelper?.fileRequest) {
                            AD_IMAGE -> {
                                contentResolver.notifyChange(data?.data, null)
                                data?.data?.let {
                                    adImage = File(getPath(it))
                                    loadImage(adImage, AD_IMAGE, it)
                                }
                            }
                        }
                    }
                }
            }


        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val countryCode = data!!.getStringExtra(SelectCountryActivity.RESULT_CONTRYCODE)
            //Toast.makeText(this, "You selected countrycode: $countryCode", Toast.LENGTH_LONG).show()
            txtcountrycode.text = countryCode
        }
    }

    private fun loadImage(imageFile: File?, request: Int, uri: Uri) {

        profilepic = ""
        profilepic = imageFile.toString()
        Glide.with(applicationContext).load(imageFile)
            .apply(
                RequestOptions()
                    .placeholder(R.mipmap.ic_launcher_round)
            )
            .into(userImageProfile)


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ImagePickerHelper.PERMISSIONS_PICK_IMAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePickerHelper?.actionPickImage(fileRequest)
            }
        } else if (requestCode == ImagePickerHelper.PERMISSIONS_TAKE_IMAGE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                imagePickerHelper?.actionTakeImage(fileRequest)
            }
        }
    }


    private fun sendSignInLink(email: String) {
        val settings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName(
                packageName,
                false, null/* minimum app version */
            )/* install if not available? */
            .setHandleCodeInApp(true)
            .setUrl("https://vlogin-0001.firebaseapp.com/emailSignInLink")
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


    private fun BackUpTask() {

        dialog = ProgressDialogshow.progressDialog(this)
        dialog.show()

        class SaveTask : AsyncTask<String, Int, String>() {

            override fun doInBackground(vararg params: String): String? {

                //creating a task

                //adding to database


                val dbfile = this@ProfileActivity.getDatabasePath(AppDatabase.DATABASE_NAME)
                val sdir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "DBsaves")
                val sfpath = sdir.path + File.separator + "DBsave" + System.currentTimeMillis().toString()
                if (!sdir.exists()) {
                    sdir.mkdirs()
                }
                val savefile = File(sfpath)
                try {
                    savefile.createNewFile()
                    val buffersize = 8 * 1024
                    val buffer = ByteArray(buffersize)
                    var bytes_read = buffersize
                    val savedb = FileOutputStream(sfpath)
                    val indb = FileInputStream(dbfile)
                    while ((bytes_read == indb.read(buffer, 0, buffersize)) !=null) {
                        savedb.write(buffer, 0, bytes_read)
                    }
                    savedb.flush()
                    indb.close()
                    savedb.close()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return "string"
            }

            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                dialog.dismiss()
                Toast.makeText(applicationContext, "Backup Successfully", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }




}
