package com.vlogonapp

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonapp.AppApplication.Companion.mSessionHolder
import com.vlogonapp.Database.AppDatabase
import com.vlogonapp.Database.entities.UserRegistration
import kotlinx.android.synthetic.main.activity_user_login.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*



class UserLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)


        toolbar?.apply {

            tvToolbarTitle.text = "Log In"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
        if (!mSessionHolder.Login_id.isEmpty()) {
            checkBoxremeber.isChecked=true
            etemailid.text = Editable.Factory.getInstance().newEditable(mSessionHolder.Login_id)
            etpassword.text = Editable.Factory.getInstance().newEditable(mSessionHolder.Login_Password)
        }
        btnlogin.clicks().subscribe{

            if (etemailid.getText().toString().isEmpty()) {
                Toast.makeText(
                    this@UserLoginActivity,
                    "Please Enter Email ID",
                    Toast.LENGTH_SHORT
                ).show()

            }
            else if (etpassword.getText().toString().isEmpty()) {
                Toast.makeText(
                    this@UserLoginActivity,
                    "Please Enter Password",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                getTasks()
            }
            }

        txtsignup.clicks().subscribe{

            val intent = Intent(this@UserLoginActivity, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()
        }



        checkBoxremeber.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
            {
                mSessionHolder.Login_id = etemailid.text.toString()
                mSessionHolder.Login_Password = etpassword.text.toString()
            }else
            {
                mSessionHolder.Login_id = ""
                mSessionHolder.Login_Password = ""
            }
        }
    }
    private fun getTasks() {
        class GetTasks : AsyncTask<Void, Void, List<UserRegistration>>() {

            override fun doInBackground(vararg voids: Void): List<UserRegistration> {
                return AppDatabase
                    .getInstance(applicationContext)
                    .userregistrationDao()
                    .getdatadata(etemailid.text.toString(),etpassword.text.toString())
            }

            override fun onPostExecute(tasks: List<UserRegistration>) {
                super.onPostExecute(tasks)


                if(tasks.isEmpty())
                {
                    Toast.makeText(applicationContext, "Email Id Or Password Is Incorrect", Toast.LENGTH_LONG).show()

                }else {
                    try {

                        mSessionHolder.User_Login = tasks[0].personalemail
                        mSessionHolder.Source_login = tasks[0].source
                        Toast.makeText(applicationContext, "Login Successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@UserLoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.enter, R.anim.exit)
                        finish()
                    } catch (e: IndexOutOfBoundsException) {

                    }
                }

            }
        }

        val gt = GetTasks()
        gt.execute()
    }
    override fun onBackPressed() {


        finish()


    }



}
