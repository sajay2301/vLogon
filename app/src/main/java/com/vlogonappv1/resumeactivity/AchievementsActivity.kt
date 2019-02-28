package com.vlogonappv1.resumeactivity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.db.DBHelper
import com.vlogonappv1.AppApplication.Companion.mSessionHolder
import com.vlogonappv1.dataclass.AchievementsDataClass
import com.vlogonappv1.R
import kotlinx.android.synthetic.main.activity_achievements.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*

class AchievementsActivity : AppCompatActivity() {


    private var company: String? = null
    private var title: String? = null
    private var link: String? = null
    private var discription: String? = null

    private var id: Int = 0
    private var edit: String? = null

    private var db: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        toolbar?.apply {

            tvToolbarTitle.text = resources.getString(R.string.achievements)
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
        btnsave.clicks().subscribe {


            if(btnsave.text.equals("Save")) {
               saveTask()
            }else
            {
                updateTasks()
            }
        }
        val i = intent.extras

        edit = i.getString("edit")
        if(edit.equals("true")) {




            company = i!!.getString("company")
            title = i.getString("title")
            link = i.getString("link")
            discription = i.getString("discription")
            id = i.getInt("id")
            btnsave.text = "Update"

            etcompany.text = Editable.Factory.getInstance().newEditable(company)
            ettitle.text = Editable.Factory.getInstance().newEditable(title)
            etlink.text = Editable.Factory.getInstance().newEditable(link)
            etdiscription.text = Editable.Factory.getInstance().newEditable(discription)



        }else
        {
            btnsave.text = "Save"
        }

      //  getTasks()
    }

    private fun saveTask() {

        class SaveTask : AsyncTask<String, Int, String>() {

            override fun doInBackground(vararg params: String): String? {


                //creating a task

                //adding to database

                db = DBHelper(applicationContext)



                val achievementsdata = AchievementsDataClass()
                achievementsdata.personalemail = mSessionHolder.User_Login
                achievementsdata.source =mSessionHolder.Source_login
                achievementsdata.company = etcompany.text.toString()
                achievementsdata.title = ettitle.text.toString()
                achievementsdata.link =  etlink.text.toString()
                achievementsdata.discription = etdiscription.text.toString()

                val id_db = db!!.addAchievementDetails(achievementsdata)

/*
                val database = AppDatabase.getInstance(this@AchievementsActivity)

                if (database.achievementDao().all.isEmpty()) {


                        val achievementsdata = AchievementsData(
                            mSessionHolder.User_Login,
                            mSessionHolder.Source_login,
                            etcompany.text.toString(),
                            ettitle.text.toString(),
                            etlink.text.toString(),
                            etdiscription.text.toString()
                        )
                        AppDatabase.getInstance(this@AchievementsActivity).achievementDao().insert(achievementsdata)
                        // showErrormessgae("Saved Successfully","1");




                } else {


                        val achievementsdata = AchievementsData(
                            mSessionHolder.User_Login,
                            mSessionHolder.Source_login,
                            etcompany.text.toString(),
                            ettitle.text.toString(),
                            etlink.text.toString(),
                            etdiscription.text.toString()
                        )
                        AppDatabase.getInstance(this@AchievementsActivity).achievementDao().insert(achievementsdata)

                        // showErrormessgae("Saved Successfully","1");


                }*/
                return "string"
            }

            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                Log.e("Response", "" + response)
                val intent = Intent(this@AchievementsActivity, AdvanceResumeActivity::class.java)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finish()
                db!!.closeDB()
                Toast.makeText(applicationContext, "Saved", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }

        private fun updateTasks() {
            class SaveTask : AsyncTask<String, Int, String>() {

                override fun doInBackground(vararg params: String): String? {

                    db = DBHelper(applicationContext)



                    val achievementsdata = AchievementsDataClass()
                    achievementsdata.personalemail = mSessionHolder.User_Login
                    achievementsdata.source =mSessionHolder.Source_login
                    achievementsdata.company = etcompany.text.toString()
                    achievementsdata.title = ettitle.text.toString()
                    achievementsdata.link =  etlink.text.toString()
                    achievementsdata.discription = etdiscription.text.toString()
                    achievementsdata.aid = id

                    val id_db = db!!.updateAchievementDetails(achievementsdata)

                    //AppDatabase.getInstance(this@AchievementsActivity).achievementDao().update(etcompany.text.toString(), ettitle.text.toString(),etlink.text.toString(), etdiscription.text.toString(),mSessionHolder.User_Login,mSessionHolder.Source_login,id)

                    return "string"
                }

                override fun onPostExecute(response: String) {
                    super.onPostExecute(response)
                    Log.e("Response", "" + response)
                    val intent = Intent(this@AchievementsActivity, AdvanceResumeActivity::class.java)
                    overridePendingTransition(R.anim.enter, R.anim.exit)
                    startActivity(intent)
                    finish()
                    Toast.makeText(applicationContext, "Update", Toast.LENGTH_LONG).show()
                }
            }

            val st = SaveTask()
            st.execute()


    }

    override fun onBackPressed() {


        val intent = Intent(this@AchievementsActivity, AdvanceResumeActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()



    }
}
