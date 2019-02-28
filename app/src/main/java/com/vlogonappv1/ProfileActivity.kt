package com.vlogonappv1

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.db.DBHelper
import com.vlogonappv1.AppApplication.Companion.mSessionHolder
import com.vlogonappv1.dataclass.ImagePickerHelper
import com.vlogonappv1.dataclass.ProgressDialogshow
import com.vlogonappv1.dataclass.UserRegistrationClass
import com.vlogonappv1.dataclass.getPath
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import java.io.File
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.vlogonappv1.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import java.util.*
import com.vlogonappv1.spinnerdatepicker.DatePicker
import com.vlogonappv1.spinnerdatepicker.DatePickerDialog

class ProfileActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener {

    override fun onDateSet(
        view: DatePicker?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) {
        var fm = "" + (monthOfYear +1)
        var fd = "" + dayOfMonth
        if((monthOfYear+1)<10){
            fm = "0"+(monthOfYear +1)
        }
        if (dayOfMonth<10){
            fd="0"+dayOfMonth
        }
        etbirthdate.text = "$fd/$fm/$year"
    }

    private var imagePickerHelper: ImagePickerHelper? = null
    private var adImage: File? = null
    var profilepic: String? = null
    var gender: String? = null
    private var fileRequest = AD_IMAGE
    lateinit var dialog: Dialog
    private var emailLink: String = ""
    private lateinit var auth: FirebaseAuth
    private var db: DBHelper? = null


    private var filenameqrcode: String? = null
    lateinit var storage: FirebaseStorage
    lateinit var storageRef: StorageReference
    internal var downloadUrl = ""


    internal var flag = 0
    internal var checkdata = 0
    lateinit var Firestoredb: FirebaseFirestore
    var passwordencodedKey: String=""

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
        db = DBHelper(applicationContext)
        Firestoredb = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
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
               // UploadImageFileToFirebaseStorage()
                updateprofile()
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
        etbirthdate.clicks().subscribe{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


          /*  val dpd = DatePickerDialog(this@ProfileActivity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                // Display Selected date in textbox

                var fm = "" + (month +1)
                var fd = "" + dayOfMonth
                if((month+1)<10){
                    fm = "0"+(month +1)
                }
                if (dayOfMonth<10){
                    fd="0"+dayOfMonth
                }
                etbirthdate.text = "$fd/$fm/$year"

            }, year, month, day)

            dpd.show()*/

            showDate(year, month, day, R.style.DatePickerSpinner)
        }

        //   checkIntent(intent)
        UserLogin()
    }
    @VisibleForTesting
    internal fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        SpinnerDatePickerDialogBuilder()
            .context(this@ProfileActivity)
            .callback(this)
            .spinnerTheme(spinnerTheme)
            .defaultDate(year, monthOfYear, dayOfMonth)
            .build()
            .show()
    }
    override fun onBackPressed() {

        mSessionHolder.User_officecolorcode = ""
        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()


    }

   /* fun enterAdditionalnumber() {


        val builder = AlertDialog.Builder(this@ProfileActivity)
        builder.setTitle("Enter Additional Number")
        val input = EditText(this@ProfileActivity)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)
        builder.setPositiveButton("Ok") { dialog, which ->
            val m_Text = input.text.toString()

            if(etadditionalnumber.text.toString() == "") {
                etadditionalnumber.text = Editable.Factory.getInstance().newEditable(m_Text)
            }else
            {
                var getnumber=etadditionalnumber.text.toString()
                etadditionalnumber.text = Editable.Factory.getInstance().newEditable("$getnumber,$m_Text")
            }



        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()

    }*/
    private fun UserLogin() {

        flag=0
        dialog = ProgressDialogshow.progressDialog(this@ProfileActivity)
        dialog.show()
        Firestoredb.collection("RegisterUser").whereEqualTo("Primary Email", mSessionHolder.User_Login)
            .whereEqualTo("Source", mSessionHolder.Source_login)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        //  Log.e("data", document.getId() + " => " + document.get("name"));
                        flag = 1
                        dialog.dismiss()
                        mSessionHolder.USER_ID = document.id
                        etfisrtname.text = Editable.Factory.getInstance().newEditable(document.get("Firstname").toString())
                        etlastname.text = Editable.Factory.getInstance().newEditable(document.get("Lastname").toString())
                        etmobilenumber.text = Editable.Factory.getInstance().newEditable(document.get("Mobile Number").toString())
                        etpersonalemail.text = Editable.Factory.getInstance().newEditable(document.get("Primary Email").toString())
                        etofficeemail.text = Editable.Factory.getInstance().newEditable(document.get("OfficeEmail").toString())
                        txtcountrycode.text = document.get("Countrycode").toString()

                        etbirthdate.text = document.get("BirthDate").toString()
                        etadditionalnumber.text = Editable.Factory.getInstance().newEditable(document.get("AdditionalNumber").toString())
                        etusername.text = document.get("UserName").toString()


                        profilepic = document.get("ProfilePic").toString()
                        Glide.with(applicationContext).load(profilepic)
                            .apply(
                                RequestOptions()
                                    .placeholder(R.mipmap.ic_launcher_round)
                            )
                            .into(userImageProfile)

                        when {
                            document.get("Gender").toString().equals("Female") -> radiofontsize.check(R.id.radiofemale)
                            document.get("Gender").toString().equals("Male") -> radiofontsize.check(R.id.radiomale)
                            document.get("Gender").toString().equals("Other") -> radiofontsize.check(R.id.radioother)

                        }

                        etaddress.text = Editable.Factory.getInstance().newEditable(document.get("Address").toString())
                        etcity.text = Editable.Factory.getInstance().newEditable(document.get("City").toString())
                        etcountry.text = Editable.Factory.getInstance().newEditable(document.get("Country").toString())

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

                                                mSessionHolder.User_ActivityName=""
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


                        }
                    }
                    if (flag == 0) {
                        Toast.makeText(this@ProfileActivity, "Something Went Wrong", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()


                    } else {

                        dialog.dismiss()
                    }
                } else {
                    dialog.dismiss()
                    Log.e("dasd", "Error getting documents.", task.exception)
                }
            }


    }
     private fun getTasks() {
        class GetTasks : AsyncTask<Void, Void, List<UserRegistrationClass>>() {

            override fun doInBackground(vararg voids: Void): List<UserRegistrationClass> {
                return db!!.allStudent(mSessionHolder.User_Login, mSessionHolder.Source_login)
            }

            override fun onPostExecute(tasks: List<UserRegistrationClass>) {
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
    fun UploadImageFileToFirebaseStorage() {

        dialog = ProgressDialogshow.progressDialog(this@ProfileActivity)
        dialog.show()

        val picurl = Uri.fromFile(adImage)
        val photoRef = storageRef.child("vlogonapp")
            .child(picurl.lastPathSegment)
        // [END get_child_ref]


        photoRef.putFile(picurl).addOnProgressListener { taskSnapshot ->

        }.continueWithTask { task ->
            // Forward any exceptions
            if (!task.isSuccessful) {
                throw task.exception!!
            }

            Log.e("uploadFromUri", "uploadFromUri: upload success")

            // Request the public download URL
            photoRef.downloadUrl
        }.addOnSuccessListener { downloadUri ->
            // Upload succeeded
            Log.e("downloadUri", downloadUri.toString())
            dialog.dismiss()


        }.addOnFailureListener { exception ->
            // Upload failed
            Log.e("exception", exception.message)
            dialog.dismiss()
        }


    }
    private fun updateprofile() {

        dialog = ProgressDialogshow.progressDialog(this@ProfileActivity)
        dialog.show()


        val setuserinfo = HashMap<String, Any>()

        setuserinfo["Firstname"] = etfisrtname.text.toString()
        setuserinfo["Lastname"] =etlastname.text.toString()
        setuserinfo["Mobile Number"] =etmobilenumber.text.toString()
        setuserinfo["Primary Email"] = mSessionHolder.User_Login
        setuserinfo["OfficeEmail"] = etofficeemail.text.toString()
        setuserinfo["ProfilePic"] = profilepic.toString()
        setuserinfo["Source"] = mSessionHolder.Source_login
        setuserinfo["Countrycode"] = txtcountrycode.text.toString()

        setuserinfo["Gender"] = gender.toString()
        setuserinfo["City"] =etcity.text.toString()
        setuserinfo["Country"] = etcountry.text.toString()
        setuserinfo["Address"] =etaddress.text.toString()
        setuserinfo["AdditionalNumber"] =etadditionalnumber.text.toString()
        setuserinfo["BirthDate"] =etbirthdate.text.toString()

        Firestoredb.collection("RegisterUser")
            .document(mSessionHolder.USER_ID)
            .update(setuserinfo)
            .addOnSuccessListener {


                try {
                    db = DBHelper(applicationContext)


                    val userdata = UserRegistrationClass()
                    userdata.firstname = etfisrtname.text.toString()
                    userdata.lastname = etlastname.text.toString()
                    userdata.mobilenumber = etmobilenumber.text.toString()
                    userdata.personalemail = mSessionHolder.User_Login
                    userdata.source = mSessionHolder.Source_login

                    userdata.officeemail = etofficeemail.text.toString()
                    userdata.profilepic = profilepic.toString()
                    userdata.gender = gender.toString()
                    userdata.address = etaddress.text.toString()
                    userdata.city = etcity.text.toString()
                    userdata.country = etcountry.text.toString()
                    userdata.countrycode = txtcountrycode.text.toString()

                    userdata.additionalnumber = etadditionalnumber.text.toString()
                    userdata.birthdate = etbirthdate.text.toString()

                    val id_db = db!!.updateUserRegistration(userdata)
                    db!!.closeDB()
                }catch (e:KotlinNullPointerException)
                {

                }

                val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finish()

                Toast.makeText(applicationContext, "Profile Update Successfully", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener { e ->
                Log.e("addded", "Error adding Register", e)
                Toast.makeText(
                    this@ProfileActivity,
                    "Register User could not be added",
                    Toast.LENGTH_SHORT
                ).show()
            }






    }

    private fun saveTask() {

        class SaveTask : AsyncTask<String, Int, String>() {

            override fun doInBackground(vararg params: String): String? {

                //creating a task

                //adding to database
                db = DBHelper(applicationContext)



                val userdata = UserRegistrationClass()
                userdata.firstname = etfisrtname.text.toString()
                userdata.lastname =  etlastname.text.toString()
                userdata.mobilenumber =  etmobilenumber.text.toString()
                userdata.personalemail =  mSessionHolder.User_Login
                userdata.source =  mSessionHolder.Source_login

                userdata.officeemail =  etofficeemail.text.toString()
                userdata.profilepic = profilepic.toString()
                userdata.gender = gender.toString()
                userdata.address = etaddress.text.toString()
                userdata.city = etcity.text.toString()
                userdata.country = etcountry.text.toString()
                userdata.countrycode =txtcountrycode.text.toString()

                val id_db = db!!.updateUserRegistration(userdata)


               /* val database = AppDatabase.getInstance(this@ProfileActivity)


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

                }*/

                return "string"
            }

            override fun onPostExecute(response: String) {
                super.onPostExecute(response)

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
