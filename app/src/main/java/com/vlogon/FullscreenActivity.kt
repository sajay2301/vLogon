package com.vlogon

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.vlogon.ResumeActivity.ExperienceActivity
import kotlinx.android.synthetic.main.activity_fullscreen.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {
    private val mHideHandler = Handler()

    private var emailLink: String = ""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)

        auth = FirebaseAuth.getInstance()
        checkIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        checkIntent(intent)
    }

    private fun checkIntent(intent: Intent?) {
        if (intentHasEmailLink(intent)) {
            emailLink = intent!!.data!!.toString()
            if(AppApplication.mSessionHolder.User_ActivityName.equals("Profile"))
            {
                val intent = Intent(this@FullscreenActivity, ProfileActivity::class.java)
                intent.putExtra("emaillink", emailLink)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finishAffinity()
                finish()
            }else
            {
                val intent = Intent(this@FullscreenActivity, RegistrationActivity::class.java)
                intent.putExtra("emaillink", emailLink)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finishAffinity()
                finish()
            }


        }
    }

    private fun intentHasEmailLink(intent: Intent?): Boolean {
        if (intent != null && intent.data != null) {
            val intentData = intent.data!!.toString()
            if (auth.isSignInWithEmailLink(intentData)) {
                return true
            }
        }

        return false
    }

}
