package com.vlogon

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.vlogon.AppApplication.Companion.mSessionHolder
import com.vlogon.Database.AppDatabase
import com.vlogon.ResumeActivity.AdvanceResumeActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.doAsync
import java.net.URL
import android.os.AsyncTask
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vlogon.Database.entities.UserRegistration


class MainActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    var profilepic: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar?.apply {

            tvToolbarTitle.text = "Home"
            icBack.visibility = View.GONE
            ivusermenuicon.clicks().subscribe {
                val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finish()
            }

            ivlogout.clicks().subscribe {
                mSessionHolder.User_Login=""
                mSessionHolder.Source_login=""
                Toast.makeText(applicationContext, "Logout", Toast.LENGTH_LONG).show()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finish()
            }
            setSupportActionBar(this)


        }

        btnadvanceresume.clicks().subscribe {
            val intent = Intent(this@MainActivity, AdvanceResumeActivity::class.java)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)
        }
        getTasks()
       /* doAsync {
            val database = AppDatabase.getInstance(this@MainActivity)


            val checkdata = database.userregistrationDao().checkdata(mSessionHolder.User_Login)







        }
*/

       // Glide.with(applicationContext).load(profilepic).into(userImageProfile)
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        val toast = Toast.makeText(
            this,
            "Press back again to exit the app", Toast.LENGTH_SHORT
        )
        toast.show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)

    }

    private fun getTasks() {
        class GetTasks : AsyncTask<Void, Void, List<UserRegistration>>() {

            override fun doInBackground(vararg voids: Void): List<UserRegistration> {
                return AppDatabase
                    .getInstance(applicationContext)
                    .userregistrationDao()
                    .checkdata(mSessionHolder.User_Login,mSessionHolder.Source_login)
            }

            override fun onPostExecute(tasks: List<UserRegistration>) {
                super.onPostExecute(tasks)

                displayname.text=tasks[0].firstname +" " + tasks[0].lastname
                displayemailid.text=tasks[0].personalemail
                profilepic=tasks[0].profilepic
                Log.e("picture", profilepic.toString())
                Glide.with(applicationContext).load(profilepic)
                    .apply(
                        RequestOptions()
                            .placeholder(R.mipmap.ic_launcher_round)
                    )
                    .into(userImageProfile)

            }
        }

        val gt = GetTasks()
        gt.execute()
    }

}
