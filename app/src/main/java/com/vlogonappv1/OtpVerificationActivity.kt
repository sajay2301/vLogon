package com.vlogonappv1

import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.jakewharton.rxbinding2.view.clicks

import com.vlogonappv1.AppApplication.Companion.mSessionHolder
import com.vlogonappv1.Class.ProgressDialogshow
import com.vlogonappv1.Class.UserRegistrationClass
import com.vlogonappv1.db.DBHelper
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import org.jetbrains.anko.startActivity


import java.util.concurrent.TimeUnit


class OtpVerificationActivity : AppCompatActivity() {

    //It is the verification id that will be sent to the user
    private var mVerificationId: String? = null

    private var UserID: String? = null
    //The edittext to input the code
    private var editTextCode: EditText? = null
    //firebase auth object
    private var mAuth: FirebaseAuth? = null
    lateinit var setmobilenumber: TextView
    lateinit var  btnVerify: Button
    lateinit var dialog: Dialog

    private var firstname: String? = null
    private var lastname: String? = null
    private var MobileNumber: String? = null
    var personalemail: String? = null
    private var officeemail: String? = null
    private var password: String? = null
    var profilepic: String? = null
    private var source: String? = null
    private var countrycode: String? = null


    private var db: DBHelper? = null


    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            //Getting the code sent by SMS
            val code = phoneAuthCredential.smsCode

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextCode!!.setText(code)
                //verifying the code
                //verifyVerificationCode(code);
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@OtpVerificationActivity, e.message, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(s: String?, forceResendingToken: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(s, forceResendingToken)

            //storing the verification id that is sent to the user
            mVerificationId = s
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        mAuth = FirebaseAuth.getInstance()

        val i = intent.extras


        firstname = i!!.getString("firstname")
        lastname = i!!.getString("lastname")
        MobileNumber = i!!.getString("mobilenumber")
        personalemail = i!!.getString("personalemail")
        officeemail = i!!.getString("officeemail")
        password = i!!.getString("password")
        profilepic = i!!.getString("profilepic")
        source = i!!.getString("source")
        countrycode = i!!.getString("countrycode")



        editTextCode = findViewById<View>(R.id.enterotp) as EditText
        btnVerify = findViewById<View>(R.id.btnVerify) as Button
        setmobilenumber = findViewById<View>(R.id.setmobilenumber) as TextView
        setmobilenumber.text = MobileNumber


        sendVerificationCode(countrycode + "" + MobileNumber)
        btnVerify.setOnClickListener(View.OnClickListener {
            val code = editTextCode!!.text.toString().trim { it <= ' ' }
            if (code.isEmpty() || code.length < 6) {
                editTextCode!!.error = "Enter valid code"
                editTextCode!!.requestFocus()
                return@OnClickListener
            }
            //verifying the code entered manually
            dialog = ProgressDialogshow.progressDialog(this)
            dialog.show()
            verifyVerificationCode(code)
        })


        txtresendotp.clicks().subscribe{
            Toast.makeText(this@OtpVerificationActivity, "OTP Send", Toast.LENGTH_LONG).show()
            sendVerificationCode(countrycode + "" + MobileNumber)
        }
        toolbar?.apply {

            tvToolbarTitle.text = "Otp Verification"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility= View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


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
        }catch (e:KotlinNullPointerException)
        {
            dialog.dismiss()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this@OtpVerificationActivity) { task ->
                if (task.isSuccessful) {
                    //verification successful we will start the profile activity
                    saveTask()
                    dialog.dismiss()


                } else {

                    //verification unsuccessful.. display an error message
                    dialog.dismiss()
                    val message = "Somthing is wrong, we will fix it soon..."
                    Toast.makeText(this@OtpVerificationActivity, message, Toast.LENGTH_LONG).show()

                }
            }
    }


    private fun saveTask() {

        class SaveTask : AsyncTask<String, Int, String>() {

            override fun doInBackground(vararg params: String): String? {

                //creating a task

                //adding to database
                 db = DBHelper(applicationContext)



                val userdata = UserRegistrationClass()
                userdata.firstname = firstname.toString()
                userdata.lastname = lastname.toString()
                userdata.mobilenumber = MobileNumber.toString()
                userdata.personalemail = personalemail.toString()
                userdata.officeemail = officeemail.toString()
                userdata.password = password.toString()
                userdata.profilepic = profilepic.toString()
                userdata.source = source.toString()
                userdata.gender = ""
                userdata.address = ""
                userdata.city = ""
                userdata.country = ""
                userdata.countrycode =countrycode.toString()

                val id_db = db!!.addUserRegistration(userdata)

               /* val database = AppDatabase.getInstance(this@OtpVerificationActivity)

                if (database.userregistrationDao().all.isEmpty()) {

                    val checkdata = database.userregistrationDao().checkdata(personalemail.toString(),source.toString())
                    if (checkdata.isEmpty()) {
                        val userdata = UserRegistration(firstname.toString(), lastname.toString(), MobileNumber.toString(), personalemail.toString(),officeemail.toString(), password.toString(),profilepic.toString(),source.toString(),gender = "",address = "",city = "",country = "", countrycode = countrycode.toString())
                        AppDatabase.getInstance(this@OtpVerificationActivity).userregistrationDao().insert(userdata)
                        // showErrormessgae("Saved Successfully","1");

                    } else {


                    }
                } else {
                    val checkdata = database.userregistrationDao().checkdata(personalemail.toString(),source.toString())
                    if (checkdata.isEmpty()) {
                        val userdata = UserRegistration(firstname.toString(), lastname.toString(), MobileNumber.toString(), personalemail.toString(),officeemail.toString(), password.toString(),profilepic.toString(),source.toString(),gender = "",address = "",city = "",country = "", countrycode = countrycode.toString())
                        AppDatabase.getInstance(this@OtpVerificationActivity).userregistrationDao().insert(userdata)

                        // showErrormessgae("Saved Successfully","1");

                    } else {

                    }
                }*/
                return "string"
            }


            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                Log.e("Response", "" + response)

                mSessionHolder.User_Login=personalemail.toString()
                mSessionHolder.Source_login=source.toString()
                mSessionHolder.User_Mobilenumber=MobileNumber.toString()
                mSessionHolder.User_Countrycode=countrycode.toString()

                if(mSessionHolder.Source_login.equals("google"))
                {

                    AppApplication.mSessionHolder.accountname= mSessionHolder.User_Login
                    AppApplication.mSessionHolder.setonnotset= "set"
                }

                db!!.closeDB()
                startActivity<MainActivity>()
                finish();
                Toast.makeText(applicationContext, "Successfully Register", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }
    override fun onBackPressed() {


        finish()


    }

}
