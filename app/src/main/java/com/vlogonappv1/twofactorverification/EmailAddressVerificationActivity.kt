package com.vlogonappv1.twofactorverification

import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.AppApplication.Companion.mSessionHolder
import com.vlogonappv1.dataclass.ProgressDialogshow
import com.vlogonappv1.MainActivity
import com.vlogonappv1.R
import kotlinx.android.synthetic.main.activity_email_address_verification.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*

class EmailAddressVerificationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var dialog: Dialog
    private var emailLink: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_address_verification)

        toolbar?.apply {

            tvToolbarTitle.text = "Two Step Verification"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility= View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
        auth = FirebaseAuth.getInstance()

        btnsubmit.clicks().subscribe{


            if (etemailaddresss.text.toString() == "") {
                Toast.makeText(
                    this@EmailAddressVerificationActivity,
                    "Please Enter Your Email Address",
                    Toast.LENGTH_SHORT

                ).show()
            } else {
                mSessionHolder.User_PersonalEmailId=etemailaddresss.text.toString()
                mSessionHolder.User_ActivityName="emailaddressverification"
                dialog = ProgressDialogshow.progressDialog(this)
                dialog.show()
                sendSignInLink(etemailaddresss.text.toString())
            }




        }

        if(mSessionHolder.User_ActivityName.equals("emailaddressverification"))
        {
            val i = intent.extras
            try {
                emailLink = i!!.getString("emaillink")


                // Log.e("emailLink",emailLink)
                dialog = ProgressDialogshow.progressDialog(this)
                dialog.show()
                if (auth.isSignInWithEmailLink(emailLink)) {
                    // Retrieve this from wherever you stored it

                    var email= mSessionHolder.User_PersonalEmailId



                    // The client SDK will parse the code from the link for you.
                    auth.signInWithEmailLink(email, emailLink)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                dialog.dismiss()

                                mSessionHolder.User_ActivityName=""
                                mSessionHolder.User_PersonalEmailId=""

                                mSessionHolder.twostepverify="true"
                                val intent = Intent(this@EmailAddressVerificationActivity, MainActivity::class.java)
                                startActivity(intent)
                                overridePendingTransition(R.anim.enter, R.anim.exit)
                                finish()

                            } else {
                                dialog.dismiss()
                                Log.e("Error", "Error signing in with email link", task.exception)
                            }
                        }
                }
            }catch (e : IllegalStateException)
            {

            }


        } else {

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


        val intent = Intent(this@EmailAddressVerificationActivity, MainActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()



    }
}
