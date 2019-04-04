package com.vlogonappv1.forgetpassword

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.AppApplication.Companion.mSessionHolder
import com.vlogonappv1.dataclass.ProgressDialogshow
import com.vlogonappv1.R
import com.vlogonappv1.activity.SelectCountryActivity
import com.vlogonappv1.activity.UserLoginActivity
import com.vlogonappv1.spinnerdatepicker.DatePicker
import com.vlogonappv1.spinnerdatepicker.DatePickerDialog
import com.vlogonappv1.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_step_verification_process.*
import kotlinx.android.synthetic.main.forgetpasswordsetpassworddialog.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class ForgetPasswordActivity : AppCompatActivity() , DatePickerDialog.OnDateSetListener {
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
        etselectbirthdate.text = "$fd/$fm/$year"
    }
    private lateinit var mAuth: FirebaseAuth
    lateinit var dialog: Dialog
    private var emailLink: String = ""
    private var mVerificationId: String? = null

    lateinit var Firestoredb: FirebaseFirestore
    internal var flag = 0
    internal var checkflag = 0
    var passwordencodedKey: String = ""
    var documentid: String = ""
    companion object {

        var selectbirtdatedialog: Dialog? = null

        var passwordesetdialog: Dialog? = null

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_verification_process)
        mAuth = FirebaseAuth.getInstance()
        Firestoredb = FirebaseFirestore.getInstance()
        toolbar?.apply {

            tvToolbarTitle.text = "Step Verification"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility= View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
        btnverifymobile.clicks().subscribe{


              if (etmobilenumber.text.toString() == "") {
                    Toast.makeText(
                        this@ForgetPasswordActivity,
                        "Please Enter Your Mobile Number",
                        Toast.LENGTH_SHORT

                    ).show()
                } else {

                  sendVerificationCode(selectcountrycode.text.toString() + "" + etmobilenumber.text.toString())
                  btnverifymobile.visibility=View.GONE
                  layoutmobilenumber.visibility=View.GONE
                  selectcountrycode.visibility=View.GONE
                  btnotpverifymobile.visibility=View.VISIBLE
                  etenterotp.visibility=View.VISIBLE
                  Toast.makeText(this@ForgetPasswordActivity, "OTP Send", Toast.LENGTH_LONG).show()
              }




        }

        btnotpverifymobile.setOnClickListener(View.OnClickListener {
            val code = etenterotp!!.text.toString().trim { it <= ' ' }
            if (code.isEmpty() || code.length < 6) {
                etenterotp!!.error = "Enter valid code"
                etenterotp!!.requestFocus()
                return@OnClickListener
            }
            //verifying the code entered manually
            dialog = ProgressDialogshow.progressDialog(this)
            dialog.show()
            verifyVerificationCode(code)
        })

        selectcountrycode.clicks().subscribe {
            val intent = Intent(this, SelectCountryActivity::class.java)
            startActivityForResult(intent, 1)
        }


        btnemailverifysubmit.clicks().subscribe {


            if (etemailaddresss.text.toString() == "") {
                Toast.makeText(
                    this@ForgetPasswordActivity,
                    "Please Enter Your Email Address",
                    Toast.LENGTH_SHORT

                ).show()
            } else {
                mSessionHolder.User_PersonalEmailId = etemailaddresss.text.toString()
                mSessionHolder.User_ActivityName = "forgetpasswordemailscreen"
                mSessionHolder.User_Mobilenumber = etmobilenumber.text.toString()
                mSessionHolder.User_Countrycode = selectcountrycode.text.toString()

                dialog = ProgressDialogshow.progressDialog(this)
                dialog.show()
                sendSignInLink(etemailaddresss.text.toString())
            }
        }


            if(mSessionHolder.User_ActivityName.equals("forgetpasswordemailscreen"))
            {
                val i = intent.extras
                try {
                    emailLink = i!!.getString("emaillink")

                    etemailaddresss.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_PersonalEmailId)
                    selectcountrycode.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_Countrycode)
                    etmobilenumber.text = Editable.Factory.getInstance().newEditable(mSessionHolder.User_Mobilenumber)

                    // Log.e("emailLink",emailLink)
                    dialog = ProgressDialogshow.progressDialog(this)
                    dialog.show()
                    if (mAuth.isSignInWithEmailLink(emailLink)) {
                        // Retrieve this from wherever you stored it

                        var email= mSessionHolder.User_PersonalEmailId



                        // The client SDK will parse the code from the link for you.
                        mAuth.signInWithEmailLink(email, emailLink)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    dialog.dismiss()

                                    mSessionHolder.User_ActivityName=""



                                    emailverifytrue.visibility=View.VISIBLE

                                    etemailaddresss.isEnabled=true
                                    btnemailverifysubmit.isEnabled=true
                                    etselectbirthdate.isEnabled=true
                                    btnsubmitdateverify.isEnabled=true


                                } else {
                                    dialog.dismiss()
                                    Log.e("Error", "Error signing in with email link", task.exception)
                                }
                            }
                    }
                }catch (e : IllegalStateException)
                {

                }
                catch (e : KotlinNullPointerException)
                {

                }


            } else {

            }



        etselectbirthdate.clicks().subscribe{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            showDate(year, month, day, R.style.DatePickerSpinner)

        }


        btnsubmitdateverify.clicks().subscribe {
            if (etselectbirthdate.text.toString().isEmpty()) {
                Toast.makeText(this@ForgetPasswordActivity, "Select Your Birthdate", Toast.LENGTH_LONG).show()
            }else{
                CheckUserisExistornot(etselectbirthdate.text.toString())
            }
        }


    }
    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            //Getting the code sent by SMS
            val code = phoneAuthCredential.smsCode



            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                etenterotp!!.setText(code)
                //verifying the code
                //verifyVerificationCode(code);
            }


        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@ForgetPasswordActivity, e.message, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(s: String?, forceResendingToken: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(s, forceResendingToken)

            //storing the verification id that is sent to the user
            mVerificationId = s
        }
    }


    private fun sendVerificationCode(mobile: String?) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mobile!!,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallbacks
        )
    }


    private fun verifyVerificationCode(code: String) {
        //creating the credential
        try {
            val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)

            //signing the user
            signInWithPhoneAuthCredential(credential)
        } catch (e: KotlinNullPointerException) {
            dialog.dismiss()
        }
    }




    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this@ForgetPasswordActivity) { task ->
                if (task.isSuccessful) {
                    //verification successful we will start the profile activity
                    //saveTask()

                    dialog.dismiss()
                    btnverifymobile.visibility=View.VISIBLE
                    layoutmobilenumber.visibility=View.VISIBLE
                    selectcountrycode.visibility=View.VISIBLE
                    btnotpverifymobile.visibility=View.GONE
                    etenterotp.visibility=View.GONE
                    mobileverifytrue.visibility = View.VISIBLE

                    layoutmobilenumber.isEnabled=false


                    etemailaddresss.isEnabled=true
                    btnemailverifysubmit.isEnabled=true

                    etselectbirthdate.isEnabled=true
                    btnsubmitdateverify.isEnabled=true





                } else {

                    //verification unsuccessful.. display an error message
                    dialog.dismiss()
                    val message = "Somthing is wrong, we will fix it soon..."
                    Toast.makeText(this@ForgetPasswordActivity, message, Toast.LENGTH_LONG).show()

                }
            }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val countryCode = data!!.getStringExtra(SelectCountryActivity.RESULT_CONTRYCODE)
            //Toast.makeText(this, "You selected countrycode: $countryCode", Toast.LENGTH_LONG).show()
            selectcountrycode.text = countryCode
        }
    }

    @VisibleForTesting
    internal fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        SpinnerDatePickerDialogBuilder()
            .context(this@ForgetPasswordActivity)
            .callback(this)
            .spinnerTheme(spinnerTheme)
            .defaultDate(year, monthOfYear, dayOfMonth)
            .build()
            .show()
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


        mAuth.sendSignInLinkToEmail(email, settings)
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

    private fun CheckUserisExistornot(birthdate: String) {
        checkflag=0
        dialog = ProgressDialogshow.progressDialog(this@ForgetPasswordActivity)
        dialog.show()


        if(mSessionHolder.User_PersonalEmailId == "")
        {
            Firestoredb.collection("RegisterUser").whereEqualTo("Mobile Number", etmobilenumber.text.toString())
                .whereEqualTo("BirthDate", birthdate)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            //  Log.e("data", document.getId() + " => " + document.get("name"));

                            documentid = document.id.toString()
                            checkflag = 1


                        }
                        if (checkflag == 0) {

                            dialog.dismiss()
                            Toast.makeText(
                                this@ForgetPasswordActivity,
                                "Please Select Correct BirthDate",
                                Toast.LENGTH_SHORT
                            ).show()


                        } else {


                            birthdateverifytrue.visibility = View.VISIBLE

                            etemailaddresss.isEnabled = true
                            btnemailverifysubmit.isEnabled = true
                            etselectbirthdate.isEnabled = true
                            btnsubmitdateverify.isEnabled = true

                            dialog.dismiss()
                            passwordesetdialog = PasswordResetDialog(this@ForgetPasswordActivity, "restore")
                            passwordesetdialog!!.show()

                        }
                    } else {
                        dialog.dismiss()
                        Log.e("dasd", "Error getting documents.", task.exception)
                    }
                }



        }else {

            Firestoredb.collection("RegisterUser").whereEqualTo("Primary Email", mSessionHolder.User_PersonalEmailId)
                .whereEqualTo("BirthDate", birthdate)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            //  Log.e("data", document.getId() + " => " + document.get("name"));

                            documentid = document.id.toString()
                            checkflag = 1


                        }
                        if (checkflag == 0) {

                            dialog.dismiss()
                            Toast.makeText(
                                this@ForgetPasswordActivity,
                                "Please Select Correct BirthDate",
                                Toast.LENGTH_SHORT
                            ).show()


                        } else {


                            birthdateverifytrue.visibility = View.VISIBLE

                            etemailaddresss.isEnabled = true
                            btnemailverifysubmit.isEnabled = true
                            etselectbirthdate.isEnabled = true
                            btnsubmitdateverify.isEnabled = true

                            dialog.dismiss()
                            passwordesetdialog = PasswordResetDialog(this@ForgetPasswordActivity, "restore")
                            passwordesetdialog!!.show()

                        }
                    } else {
                        dialog.dismiss()
                        Log.e("dasd", "Error getting documents.", task.exception)
                    }
                }


        }
    }


    fun PasswordResetDialog(context: Context, backupname: String): Dialog {


        val inflate = LayoutInflater.from(context).inflate(R.layout.forgetpasswordsetpassworddialog, null)
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
                Toast.makeText(this@ForgetPasswordActivity, "Password can't be blank", Toast.LENGTH_LONG).show()
            } else if (resetdialog.etnewconfrimpassword.text.toString().isEmpty()) {
                Toast.makeText(this@ForgetPasswordActivity, "Confrim Password can't be blank", Toast.LENGTH_LONG).show()
            }else if (resetdialog.etnewconfrimpassword.text.toString() != resetdialog.etnewpassword.text.toString()) {
                Toast.makeText(this@ForgetPasswordActivity, "Please Enter Valid Confirm Password", Toast.LENGTH_LONG).show()
            } else {
                resetdialog.dismiss()
                passwordencodedKey = String(Base64.encode(resetdialog.etnewpassword.text.toString().toByteArray(), 0))
                updatepassword(passwordencodedKey,resetdialog.etnewpassword.text.toString())
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


    private fun updatepassword(password: String,storepassword: String) {

        dialog = ProgressDialogshow.progressDialog(this@ForgetPasswordActivity)
        dialog.show()
        val setpassinfo = HashMap<String, Any>()

        setpassinfo["Password"] = password

        Firestoredb.collection("RegisterUser")
            .document(documentid!!)
            .update(setpassinfo)
            .addOnSuccessListener {


                mSessionHolder.Login_Password=storepassword
                val intent = Intent(this@ForgetPasswordActivity, UserLoginActivity::class.java)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finish()
                finishAffinity()

                Toast.makeText(
                    this@ForgetPasswordActivity,
                    "Password Reset Successfully",
                    Toast.LENGTH_SHORT
                ).show()

            }
            .addOnFailureListener { e ->
                Log.e("addded", "Error adding Register", e)
                Toast.makeText(
                    this@ForgetPasswordActivity,
                    "Register User could not be added",
                    Toast.LENGTH_SHORT
                ).show()
            }






    }
    override fun onBackPressed() {



        finish()



    }
}
