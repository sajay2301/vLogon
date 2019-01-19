package com.vlogon.ResumeActivity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.vlogon.AppApplication.Companion.mSessionHolder
import com.vlogon.Database.AppDatabase
import com.vlogon.Database.entities.SkillsData
import com.vlogon.R
import kotlinx.android.synthetic.main.activity_skills.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*

class SkillsActivity : AppCompatActivity() {

    private var skill: String? = null
    private var id: Int = 0
    private var edit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skills)


        toolbar?.apply {

            tvToolbarTitle.text = resources.getString(R.string.skill)
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }

        btnsave.clicks().subscribe {


            if (btnsave.text.equals("Save")) {
                saveTask()
            } else {
                updateTasks()
            }
        }
        val i = intent.extras

        edit = i.getString("edit")
        if (edit.equals("true")) {

            skill = i!!.getString("skill")
            id = i.getInt("id")
            btnsave.text = "Update"

            etskill.text = Editable.Factory.getInstance().newEditable(skill)


        } else {
            btnsave.text = "Save"
        }
        //   getTasks()


    }

    override fun onBackPressed() {


        val intent = Intent(this@SkillsActivity, AdvanceResumeActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()


    }

    private fun saveTask() {

        class SaveTask : AsyncTask<String, Int, String>() {

            override fun doInBackground(vararg params: String): String? {

                //creating a task

                //adding to database


                val database = AppDatabase.getInstance(this@SkillsActivity)

                if (database.skillsDao().all.isEmpty()) {

                    /*  val checkdata =
                          database.skillsDao().checkdata(mSessionHolder.User_Login, mSessionHolder.Source_login)
                      if (checkdata.isEmpty()) {*/
                    val skillsdata =
                        SkillsData(mSessionHolder.User_Login, mSessionHolder.Source_login, etskill.text.toString())
                    AppDatabase.getInstance(this@SkillsActivity).skillsDao().insert(skillsdata)
                    // showErrormessgae("Saved Successfully","1");

                    /* } else {


                     }*/
                } else {
                    /*  val checkdata =
                          database.skillsDao().checkdata(mSessionHolder.User_Login, mSessionHolder.Source_login)
                      if (checkdata.isEmpty()) {*/
                    val skillsdata =
                        SkillsData(mSessionHolder.User_Login, mSessionHolder.Source_login, etskill.text.toString())
                    AppDatabase.getInstance(this@SkillsActivity).skillsDao().insert(skillsdata)

                    // showErrormessgae("Saved Successfully","1");

                    /*      } else {

                              AppDatabase.getInstance(this@SkillsActivity).skillsDao().update(etskill.text.toString(),mSessionHolder.User_Login,mSessionHolder.Source_login)

                          }*/
                }
                return "string"
            }

            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                Log.e("Response", "" + response)
                val intent = Intent(this@SkillsActivity, AdvanceResumeActivity::class.java)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finish()
                Toast.makeText(applicationContext, "Saved", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }

    private fun updateTasks() {
        class SaveTask : AsyncTask<String, Int, String>() {

            override fun doInBackground(vararg params: String): String? {


                AppDatabase.getInstance(this@SkillsActivity).skillsDao()
                    .update(etskill.text.toString(), mSessionHolder.User_Login, mSessionHolder.Source_login, id)






                return "string"
            }

            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                Log.e("Response", "" + response)
                val intent = Intent(this@SkillsActivity, AdvanceResumeActivity::class.java)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finish()
                Toast.makeText(applicationContext, "Update", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }
}
