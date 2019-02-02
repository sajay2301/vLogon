package com.vlogonappv1.Twofactorverification


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
import com.vlogonappv1.BackupActivity
import com.vlogonappv1.Class.ProgressDialogshow
import com.vlogonappv1.MainActivity
import com.vlogonappv1.R
import com.vlogonappv1.db.DBHelper
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import java.util.concurrent.TimeUnit


class FirstStepMobileVerificationActivity : AppCompatActivity() {

    //It is the verification id that will be sent to the user
    private var mVerificationId: String? = null

    private var UserID: String? = null
    //The edittext to input the code
    private var editTextCode: EditText? = null
    //firebase auth object
    private var mAuth: FirebaseAuth? = null
    lateinit var setmobilenumber: TextView
    lateinit var btnVerify: Button
    lateinit var dialog: Dialog


    private var MobileNumber: String? = null
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
            Toast.makeText(this@FirstStepMobileVerificationActivity, e.message, Toast.LENGTH_LONG).show()
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



        MobileNumber = i!!.getString("mobilenumber")
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


        txtresendotp.clicks().subscribe {
            Toast.makeText(this@FirstStepMobileVerificationActivity, "OTP Send", Toast.LENGTH_LONG).show()
            sendVerificationCode(countrycode + "" + MobileNumber)
        }
        toolbar?.apply {

            tvToolbarTitle.text = "Otp Verification"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
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
        } catch (e: KotlinNullPointerException) {
            dialog.dismiss()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this@FirstStepMobileVerificationActivity) { task ->
                if (task.isSuccessful) {
                    //verification successful we will start the profile activity
                    //saveTask()

                    dialog.dismiss()
                    if (mSessionHolder.Backupname.equals("restore")) {
                        mSessionHolder.User_ActivityName = ""
                        mSessionHolder.User_PersonalEmailId = ""

                        mSessionHolder.twostepverify = "true"
                        val intent = Intent(this@FirstStepMobileVerificationActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.enter, R.anim.exit)
                        finish()
                    } else if (mSessionHolder.Backupname.equals("none")) {
                        mSessionHolder.User_ActivityName = ""
                        mSessionHolder.User_PersonalEmailId = ""

                        mSessionHolder.twostepverify = "true"
                        val intent = Intent(this@FirstStepMobileVerificationActivity, BackupActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.enter, R.anim.exit)
                        finish()
                    } else if (mSessionHolder.Backupname.equals("restorebackup")) {
                        mSessionHolder.User_ActivityName = ""
                        mSessionHolder.User_PersonalEmailId = ""

                        mSessionHolder.twostepverify = "true"
                        val intent = Intent(this@FirstStepMobileVerificationActivity, BackupActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.enter, R.anim.exit)
                        finish()
                    }else if (mSessionHolder.Backupname.equals("google")) {
                        mSessionHolder.User_ActivityName = ""
                        mSessionHolder.User_PersonalEmailId = ""

                        mSessionHolder.twostepverify = "true"
                        val intent = Intent(this@FirstStepMobileVerificationActivity, BackupActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.enter, R.anim.exit)
                        finish()
                    } else

                    {
                        val intent = Intent(
                            this@FirstStepMobileVerificationActivity,
                            EmailAddressVerificationActivity::class.java
                        )
                        startActivity(intent)
                        overridePendingTransition(R.anim.enter, R.anim.exit)
                        finish()

                    }


                } else {

                    //verification unsuccessful.. display an error message
                    dialog.dismiss()
                    val message = "Somthing is wrong, we will fix it soon..."
                    Toast.makeText(this@FirstStepMobileVerificationActivity, message, Toast.LENGTH_LONG).show()

                }
            }
    }


    override fun onBackPressed() {


        val intent = Intent(this@FirstStepMobileVerificationActivity, MainActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()


    }
}
