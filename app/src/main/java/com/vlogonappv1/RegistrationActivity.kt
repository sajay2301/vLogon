package com.vlogonappv1

import android.app.*

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AlertDialog

import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.AppApplication.Companion.mSessionHolder
import com.vlogonappv1.dataclass.ProgressDialogshow
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.google.firebase.firestore.FirebaseFirestore
import com.vlogonappv1.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import java.util.*
import com.vlogonappv1.spinnerdatepicker.DatePicker
import com.vlogonappv1.spinnerdatepicker.DatePickerDialog

class RegistrationActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener {
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




    private var mVerificationId: String? = null
    private var emaillogin: String? = null
    private var UserID: String? = null
    private var emailid: String? = null
    private var firstname: String? = null
    private var lastname: String? = null
    var profilepic: String? = null
    private var source: String? = null
    var termandcondition: Boolean = false
    var allowsendnewsoffer: Boolean = false
    private var pendingEmail: String = ""
    private var emailLink: String = ""
    private var location: String = ""
    private lateinit var auth: FirebaseAuth
    lateinit var dialog: Dialog
    internal var flag = 0
    lateinit var Firestoredb: FirebaseFirestore
    companion object {
        private const val TAG = "PasswordlessSignIn"
        private const val KEY_PENDING_EMAIL = "key_pending_email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)



        toolbar?.apply {

            tvToolbarTitle.text = "Sing Up"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
        auth = FirebaseAuth.getInstance()
        val i = intent.extras
        Firestoredb = FirebaseFirestore.getInstance()

        emaillogin = i!!.getString("emaillogin")
        source = i.getString("source")

        if (emaillogin.equals("true")) {


            personalemailverify.clicks().subscribe {

                val email = etpersonalemail.text.toString()
                if (TextUtils.isEmpty(email)) {

                    Toast.makeText(
                        this@RegistrationActivity,
                        "Email must not be empty.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    dialog = ProgressDialogshow.progressDialog(this)
                    dialog.show()

                    mSessionHolder.User_Firstname = etfisrtname.text.toString()
                    mSessionHolder.User_Lastname = etlastname.text.toString()
                    mSessionHolder.User_Countrycode = selectcountrycode.text.toString()
                    mSessionHolder.User_Mobilenumber = etmobilenumber.text.toString()
                    mSessionHolder.User_OfficeEmailId = etofficeemail.text.toString()
                    mSessionHolder.User_PersonalEmailId = etpersonalemail.text.toString()

                    mSessionHolder.User_EmailSource = emaillogin.toString()
                    mSessionHolder.User_LoginSource = source.toString()

                    mSessionHolder.User_name = etusername.text.toString()
                    mSessionHolder.User_Birthdate = etbirthdate.text.toString()
                    mSessionHolder.User_Location =location.toString()
                    mSessionHolder.User_AdditionalNumber = etadditionalnumber.text.toString()

                    mSessionHolder.User_termandcondition = termandcondition
                    mSessionHolder.User_allowoffer = allowsendnewsoffer

                    mSessionHolder.User_Password = etpassword.text.toString()
                    mSessionHolder.User_ConfirmPassword = etconfirmpassword.text.toString()
                    mSessionHolder.User_VerifyEmailId = ""
                    mSessionHolder.User_VerifyEmailId = "personal"
                    mSessionHolder.User_ActivityName="Registration"
                    sendSignInLink(etpersonalemail.text.toString())
                }
            }

        } else {
            try {
                emailid = i.getString("emailid")
                firstname = i.getString("firstname")
                lastname = i.getString("lastname")
                profilepic = i.getString("profilepic")
                etfisrtname.text = Editable.Factory.getInstance().newEditable(firstname)
                etlastname.text = Editable.Factory.getInstance().newEditable(lastname)
                etpersonalemail.text = Editable.Factory.getInstance().newEditable(emailid)

                if(source.equals("twitter"))
                {

                }else
                {
                    personalemailverify.setTextColor(Color.parseColor("#54B948"))
                    mSessionHolder.User_personalcolorcode = "#54B948"
                    personalemailverify.text="Verified"
                }



            } catch (e: NullPointerException) {

                etfisrtname.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_Firstname)
                etlastname.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_Lastname)
                selectcountrycode.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_Countrycode)
                etmobilenumber.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_Mobilenumber)
                etpersonalemail.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_PersonalEmailId)
                etofficeemail.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_OfficeEmailId)
                etpassword.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_Password)
                etconfirmpassword.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_ConfirmPassword)

                etbirthdate.text = mSessionHolder.User_Birthdate
                etusername.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_name)
                etadditionalnumber.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_AdditionalNumber)
                location=mSessionHolder.User_Location


                emaillogin = mSessionHolder.User_EmailSource
                source = mSessionHolder.User_LoginSource

                checkBoxtermandcondition.isChecked = mSessionHolder.User_termandcondition
                termandcondition=mSessionHolder.User_termandcondition
                checkBoxallowsendnewsoffer.isChecked = mSessionHolder.User_allowoffer
                try {

                    if(!mSessionHolder.User_personalcolorcode.equals("")) {
                        personalemailverify.setTextColor(Color.parseColor(mSessionHolder.User_personalcolorcode))
                        mSessionHolder.User_personalcolorcode = "#54B948"
                        personalemailverify.text="Verified"
                    }
                }catch (e :StringIndexOutOfBoundsException)
                {

                }



            }
        }

        checkBoxtermandcondition.setOnCheckedChangeListener { _, isChecked ->
            termandcondition = isChecked
        }

        checkBoxallowsendnewsoffer.setOnCheckedChangeListener { _, isChecked ->
            allowsendnewsoffer = isChecked
        }


        checkBoxbackupandrestore.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
            {
                mSessionHolder.backupsetupornot="true"
            }else
            {
                mSessionHolder.backupsetupornot="false"
            }
        }


        addmobilebutton.clicks().subscribe {
            enterAdditionalnumber()

        }


        officeemailverify.clicks().subscribe {
            val email = etofficeemail.text.toString()
            if (TextUtils.isEmpty(email)) {

                Toast.makeText(
                    this@RegistrationActivity,
                    "Email must not be empty.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {


                dialog = ProgressDialogshow.progressDialog(this)
                dialog.show()

                mSessionHolder.User_Firstname = etfisrtname.text.toString()
                mSessionHolder.User_Lastname = etlastname.text.toString()
                mSessionHolder.User_Countrycode = selectcountrycode.text.toString()
                mSessionHolder.User_Mobilenumber = etmobilenumber.text.toString()
                mSessionHolder.User_OfficeEmailId = etofficeemail.text.toString()
                mSessionHolder.User_PersonalEmailId = etpersonalemail.text.toString()


                mSessionHolder.User_Birthdate = etbirthdate.text.toString()
                mSessionHolder.User_Location =location.toString()
                mSessionHolder.User_AdditionalNumber = etadditionalnumber.text.toString()
                mSessionHolder.User_name = etusername.text.toString()

                mSessionHolder.User_EmailSource = emaillogin.toString()
                mSessionHolder.User_LoginSource = source.toString()

                mSessionHolder.User_termandcondition = termandcondition
                mSessionHolder.User_allowoffer = allowsendnewsoffer

                mSessionHolder.User_Password = etpassword.text.toString()
                mSessionHolder.User_ConfirmPassword = etconfirmpassword.text.toString()
                mSessionHolder.User_VerifyEmailId = ""
                mSessionHolder.User_VerifyEmailId = "office"
                mSessionHolder.User_ActivityName="Registration"
                sendSignInLink(etofficeemail.text.toString())
            }
        }


        btnSingup.clicks().subscribe {

            if (emaillogin.equals("true")) {

                if (etusername.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter User Name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (etfisrtname.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter First Name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (etlastname.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter Last Name",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (etmobilenumber.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter Phone Number",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if (etbirthdate.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Select Birth Date",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                else if (etpersonalemail.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter Personal Email",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (etpassword.text.toString().isEmpty() || etpassword.text.toString().length < 6 || !isValidPassword(
                        etpassword.text.toString()
                    )
                ) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Password Must be Six Character Long and Included special characters, besides letters and digits (A-Z, a-z, 0-9), are allowed in passwords",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (etconfirmpassword.text.toString().isEmpty() || etpassword.text.toString() != etconfirmpassword.text.toString()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter Valid Confirm Password",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (!termandcondition) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Agree with the terms and condition",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {


                    if (mSessionHolder.User_personalcolorcode == "") {

                        Toast.makeText(
                            this@RegistrationActivity,
                            "Please Verify Your Personal Email ID",
                            Toast.LENGTH_SHORT

                        ).show()

                    } else if (etofficeemail.text.toString() != "" && mSessionHolder.User_officecolorcode == "") {
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Please Verify Your Office Email ID",
                            Toast.LENGTH_SHORT

                        ).show()
                    } else {




                        CheckUserisExistornot()
                    }


                }

            } else {

                if (etusername.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter User Name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (etfisrtname.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter First Name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (etlastname.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter Last Name",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (etmobilenumber.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter Phone Number",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (etbirthdate.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Select Birth Date",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                else if (etpersonalemail.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter Personal Email",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (etpassword.text.toString().isEmpty() || etpassword.text.toString().length < 6 || !isValidPassword(
                        etpassword.text.toString()
                    )
                ) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Password Must be Six Character Long and Included special characters, besides letters and digits (A-Z, a-z, 0-9), are allowed in passwords",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (etconfirmpassword.text.toString().isEmpty() || etpassword.text.toString() != etconfirmpassword.text.toString()) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Please Enter Valid Confirm Password",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (!termandcondition) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Agree with the terms and condition",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {


                    if (etofficeemail.text.toString() != "" && mSessionHolder.User_officecolorcode == "") {
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Please Verify Your Office Email ID",
                            Toast.LENGTH_SHORT

                        ).show()
                    } else {






                        CheckUserisExistornot()




                    }
                }


            }


        }

        txtlogin.clicks().subscribe {

            val intent = Intent(this@RegistrationActivity, UserLoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()
        }

        etbirthdate.clicks().subscribe{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


        /*    val dpd = DatePickerDialog(this@RegistrationActivity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

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

        // Check if the Intent that started the Activity contains an email sign-in link.
      //  checkIntent(intent)

        val intent = Intent(this, SelectCountryActivity::class.java)

        selectcountrycode.clicks().subscribe {
            startActivityForResult(intent, 1)
        }
        if(mSessionHolder.User_ActivityName .equals("Registration"))
        {

            try {
                emailLink = i.getString("emaillink")


                // Log.e("emailLink",emailLink)
                dialog = ProgressDialogshow.progressDialog(this)
                dialog.show()
                if (auth.isSignInWithEmailLink(emailLink)) {
                    // Retrieve this from wherever you stored it

                    var email: String = ""
                    email = if (mSessionHolder.User_VerifyEmailId == "personal") {
                        mSessionHolder.User_PersonalEmailId
                    } else {
                        mSessionHolder.User_OfficeEmailId
                    }


                    // The client SDK will parse the code from the link for you.
                    auth.signInWithEmailLink(email, emailLink)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                dialog.dismiss()


                                if (mSessionHolder.User_VerifyEmailId == "personal") {
                                    personalemailverify.setTextColor(Color.parseColor("#54B948"))
                                    mSessionHolder.User_personalcolorcode = "#54B948"
                                    personalemailverify.text="Verified"

                                } else {
                                    officeemailverify.setTextColor(Color.parseColor("#54B948"))
                                    mSessionHolder.User_officecolorcode = "#54B948"
                                    officeemailverify.text="Verified"
                                }


                            } else {
                                dialog.dismiss()
                                Log.e(TAG, "Error signing in with email link", task.exception)
                            }
                        }
                }
            }catch (e : IllegalStateException)
            {

            }


        } else {

        }
    }

    @VisibleForTesting
    internal fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        SpinnerDatePickerDialogBuilder()
            .context(this@RegistrationActivity)
            .callback(this)
            .spinnerTheme(spinnerTheme)
            .defaultDate(year, monthOfYear, dayOfMonth)
            .build()
            .show()
    }
  /*  private fun saveTask() {

        class SaveTask : AsyncTask<String, Int, String>() {

            override fun doInBackground(vararg params: String): String? {

                //creating a task

                //adding to database


                val database = AppDatabase.getInstance(this@RegistrationActivity)

                if (database.userregistrationDao().all.isEmpty()) {

                    val checkdata =
                        database.userregistrationDao().checkdata(etpersonalemail.text.toString(), source.toString())
                    if (checkdata.isEmpty()) {
                        val userdata = UserRegistration(
                            etfisrtname.text.toString(),
                            etlastname.text.toString(),
                            etmobilenumber.text.toString(),
                            etpersonalemail.text.toString(),
                            etofficeemail.text.toString(),
                            etpassword.text.toString(),
                            profilepic.toString(),
                            source.toString(),
                            gender = "",
                            address = "",
                            city = "",
                            country = "",
                            countrycode = selectcountrycode.text.toString()
                        )
                        AppDatabase.getInstance(this@RegistrationActivity).userregistrationDao().insert(userdata)
                        // showErrormessgae("Saved Successfully","1");

                    } else {


                    }
                } else {
                    val checkdata =
                        database.userregistrationDao().checkdata(etpersonalemail.text.toString(), source.toString())
                    if (checkdata.isEmpty()) {
                        val userdata = UserRegistration(
                            etfisrtname.text.toString(),
                            etlastname.text.toString(),
                            etmobilenumber.text.toString(),
                            etpersonalemail.text.toString(),
                            etofficeemail.text.toString(),
                            etpassword.text.toString(),
                            profilepic.toString(),
                            source.toString(),

                            gender = "",
                            address = "",
                            city = "",
                            country = "",
                            countrycode = selectcountrycode.text.toString()

                        )
                      //  AppDatabase.getInstance(this@RegistrationActivity).userregistrationDao().insert(userdata)

                        // showErrormessgae("Saved Successfully","1");

                    } else {

                    }
                }
                return "string"
            }


            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                Log.e("Response", "" + response)

                mSessionHolder.User_Login = etpersonalemail.text.toString()
                mSessionHolder.Source_login = source.toString()

                startActivity<MainActivity>()
                finish()
                Toast.makeText(applicationContext, "Successfully Register", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }*/
  fun enterAdditionalnumber() {


      val builder = AlertDialog.Builder(this@RegistrationActivity)
      builder.setTitle("Enter Additional Number")
      val input = EditText(this@RegistrationActivity)
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

  }
    fun isValidPassword(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Za-z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)

        return matcher.matches()



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
                    Toast.makeText(applicationContext, "Verification Link send your email address please verify your email address.", Toast.LENGTH_LONG).show()

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









    override fun onBackPressed() {

        mSessionHolder.User_Firstname = ""
        mSessionHolder.User_Lastname = ""
        mSessionHolder.User_Countrycode = ""
        mSessionHolder.User_Mobilenumber = ""
        mSessionHolder.User_OfficeEmailId = ""
        mSessionHolder.User_PersonalEmailId = ""

        mSessionHolder.User_EmailSource = ""
        mSessionHolder.User_LoginSource = ""

        mSessionHolder.User_termandcondition = false
        mSessionHolder.User_allowoffer = false

        mSessionHolder.User_Password = ""
        mSessionHolder.User_ConfirmPassword = ""
        mSessionHolder.User_VerifyEmailId = ""
        mSessionHolder.User_personalcolorcode = ""
        mSessionHolder.User_officecolorcode = ""
        mSessionHolder.User_ActivityName=""

        mSessionHolder.User_Birthdate = ""
        mSessionHolder.User_Location =""
        mSessionHolder.User_AdditionalNumber =""
        mSessionHolder.User_name =""
        finish()


    }



    private fun CheckUserisExistornot() {
        flag=0
        dialog = ProgressDialogshow.progressDialog(this@RegistrationActivity)
        dialog.show()


        Firestoredb.collection("RegisterUser").whereEqualTo("UserName", etusername.text.toString())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        //  Log.e("data", document.getId() + " => " + document.get("name"));

                        flag = 1


                    }
                    if (flag == 0) {


                        if (emaillogin.equals("true")) {

                            mSessionHolder.User_Firstname = ""
                            mSessionHolder.User_Lastname = ""
                            mSessionHolder.User_Countrycode = ""
                            mSessionHolder.User_Mobilenumber = ""
                            mSessionHolder.User_OfficeEmailId = ""
                            mSessionHolder.User_PersonalEmailId = ""

                            mSessionHolder.User_EmailSource = ""
                            mSessionHolder.User_LoginSource = ""

                            mSessionHolder.User_termandcondition = false
                            mSessionHolder.User_allowoffer = false

                            mSessionHolder.User_Password = ""
                            mSessionHolder.User_ConfirmPassword = ""
                            mSessionHolder.User_VerifyEmailId = ""
                            mSessionHolder.User_personalcolorcode = ""
                            mSessionHolder.User_officecolorcode = ""
                            mSessionHolder.User_ActivityName = ""

                            mSessionHolder.User_Birthdate = ""
                            mSessionHolder.User_Location = ""
                            mSessionHolder.User_name = ""
                            mSessionHolder.User_AdditionalNumber = ""

                            val intent = Intent(this@RegistrationActivity, OtpVerificationActivity::class.java)
                            intent.putExtra("username", etusername.text.toString())
                            intent.putExtra("birthdate", etbirthdate.text.toString())
                            intent.putExtra("firstname", etfisrtname.text.toString())
                            intent.putExtra("lastname", etlastname.text.toString())
                            intent.putExtra("mobilenumber", etmobilenumber.text.toString())
                            intent.putExtra("additionalnumber", etadditionalnumber.text.toString())


                            intent.putExtra("countrycode", selectcountrycode.text.toString())
                            intent.putExtra("personalemail", etpersonalemail.text.toString())
                            intent.putExtra("officeemail", etofficeemail.text.toString())
                            intent.putExtra("password", etpassword.text.toString())

                            intent.putExtra("location", location.toString())
                            intent.putExtra("profilepic", "")
                            intent.putExtra("source", "email")
                            startActivity(intent)
                            overridePendingTransition(R.anim.enter, R.anim.exit)
                            finish()
                        }else {


                            mSessionHolder.User_Firstname = ""
                            mSessionHolder.User_Lastname = ""
                            mSessionHolder.User_Countrycode = ""
                            mSessionHolder.User_Mobilenumber = ""
                            mSessionHolder.User_OfficeEmailId = ""
                            mSessionHolder.User_PersonalEmailId = ""

                            mSessionHolder.User_EmailSource = ""
                            mSessionHolder.User_LoginSource = ""

                            mSessionHolder.User_termandcondition = false
                            mSessionHolder.User_allowoffer = false

                            mSessionHolder.User_Password = ""
                            mSessionHolder.User_ConfirmPassword = ""
                            mSessionHolder.User_VerifyEmailId = ""
                            mSessionHolder.User_personalcolorcode = ""
                            mSessionHolder.User_officecolorcode = ""
                            mSessionHolder.User_ActivityName = ""

                            mSessionHolder.User_Birthdate = ""
                            mSessionHolder.User_Location = ""
                            mSessionHolder.User_AdditionalNumber = ""
                            mSessionHolder.User_name = ""

                            val intent = Intent(this@RegistrationActivity, OtpVerificationActivity::class.java)
                            intent.putExtra("username", etusername.text.toString())
                            intent.putExtra("birthdate", etbirthdate.text.toString())
                            intent.putExtra("firstname", etfisrtname.text.toString())
                            intent.putExtra("lastname", etlastname.text.toString())
                            intent.putExtra("mobilenumber", etmobilenumber.text.toString())
                            intent.putExtra("additionalnumber", etadditionalnumber.text.toString())

                            intent.putExtra("countrycode", selectcountrycode.text.toString())
                            intent.putExtra("personalemail", etpersonalemail.text.toString())
                            intent.putExtra("officeemail", etofficeemail.text.toString())
                            intent.putExtra("password", etpassword.text.toString())
                            intent.putExtra("profilepic", profilepic.toString())
                            intent.putExtra("location", location.toString())
                            intent.putExtra("source", source.toString())
                            startActivity(intent)
                            overridePendingTransition(R.anim.enter, R.anim.exit)
                            finish()
                        }

                    } else {
                        Toast.makeText(this@RegistrationActivity, "Username Allready Exist.", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()

                    }
                } else {
                    dialog.dismiss()
                    Log.e("dasd", "Error getting documents.", task.exception)
                }
            }



    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val countryCode = data!!.getStringExtra(SelectCountryActivity.RESULT_CONTRYCODE)
            location = data.getStringExtra(SelectCountryActivity.RESULT_CONTRYNAME)
            //Toast.makeText(this, "You selected countrycode: $countryCode", Toast.LENGTH_LONG).show()
            selectcountrycode.text = countryCode
        }
    }


}
