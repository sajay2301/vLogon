package com.vlogonappv1.ResumeActivity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.R
import kotlinx.android.synthetic.main.activity_education_.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import android.widget.Toast
import android.content.Intent
import android.os.AsyncTask
import android.text.Editable
import android.util.Log
import com.vlogonappv1.db.DBHelper
import com.vlogonappv1.AppApplication.Companion.mSessionHolder
import com.vlogonappv1.Class.EducationDataClass


class EducationActivity : AppCompatActivity() {


    private var degree: String? = null
    private var feildofstudy: String? = null
    private var college: String? = null
    private var yearofcompletion: String? = null
    private var id: Int = 0
    private var edit: String? = null
    private var db: DBHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_education_)

        toolbar?.apply {

            tvToolbarTitle.text = resources.getString(R.string.education)
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility= View.GONE
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

            degree = i!!.getString("degree")
            feildofstudy = i.getString("feildofstudy")
            college = i.getString("college")
            yearofcompletion = i.getString("yearofcompletion")
            id = i.getInt("id")
            btnsave.text = "Update"

            etdegreename.text = Editable.Factory.getInstance().newEditable(degree)
            etfieldofstudy.text = Editable.Factory.getInstance().newEditable(feildofstudy)
            etcollege.text = Editable.Factory.getInstance().newEditable(college)
            etyearofcompletion.text = Editable.Factory.getInstance().newEditable(yearofcompletion)


        }else
        {
            btnsave.text = "Save"
        }

        //getTasks()

    }
    override fun onBackPressed() {


        val intent = Intent(this@EducationActivity, AdvanceResumeActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()


    }

    private fun saveTask() {

        class SaveTask : AsyncTask<String, Int, String>() {

            override fun doInBackground(vararg params: String): String? {


                //creating a task

                //adding to database

                db = DBHelper(applicationContext)



                val educationdata = EducationDataClass()
                educationdata.personalemail = mSessionHolder.User_Login
                educationdata.source =mSessionHolder.Source_login
                educationdata.degree = etdegreename.text.toString()
                educationdata.fieldtostudy =  etfieldofstudy.text.toString()
                educationdata.college = etcollege.text.toString()
                educationdata.Yearofcomletion = etyearofcompletion.text.toString()

                val id_db = db!!.addEducationDetails(educationdata)


               /* val database = AppDatabase.getInstance(this@EducationActivity)

                if (database.experienceDao().all.isEmpty()) {


                        val educationdata = EducationData(
                            mSessionHolder.User_Login,
                            mSessionHolder.Source_login,
                            etdegreename.text.toString(),
                            etfieldofstudy.text.toString(),
                            etcollege.text.toString(),
                            etyearofcompletion.text.toString()
                        )
                        AppDatabase.getInstance(this@EducationActivity).educationDao().insert(educationdata)
                        // showErrormessgae("Saved Successfully","1");

                } else {

                        val educationdata = EducationData(
                            mSessionHolder.User_Login,
                            mSessionHolder.Source_login,
                            etdegreename.text.toString(),
                            etfieldofstudy.text.toString(),
                            etcollege.text.toString(),
                            etyearofcompletion.text.toString()
                        )
                        AppDatabase.getInstance(this@EducationActivity).educationDao().insert(educationdata)

                        // showErrormessgae("Saved Successfully","1");



                        //  val educationdata = EducationData(mSessionHolder.User_Login,mSessionHolder.Source_login, etdegreename.text.toString(), etfieldofstudy.text.toString(),etcollege.text.toString(), etyearofcompletion.text.toString())
                       *//* AppDatabase.getInstance(this@EducationActivity).educationDao().update(
                            etdegreename.text.toString(),
                            etfieldofstudy.text.toString(),
                            etcollege.text.toString(),
                            etyearofcompletion.text.toString(),
                            mSessionHolder.User_Login,
                            mSessionHolder.Source_login,id
                        )*//*



                }*/

                return "string"
            }

            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                Log.e("Response", "" + response)
                val intent = Intent(this@EducationActivity, AdvanceResumeActivity::class.java)
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


                //creating a task

                //adding to database

                db = DBHelper(applicationContext)

                val educationdata = EducationDataClass()
                educationdata.personalemail = mSessionHolder.User_Login
                educationdata.source =mSessionHolder.Source_login
                educationdata.degree = etdegreename.text.toString()
                educationdata.fieldtostudy =  etfieldofstudy.text.toString()
                educationdata.college = etcollege.text.toString()
                educationdata.Yearofcomletion = etyearofcompletion.text.toString()
                educationdata.eid = id


             val id_db = db!!.updateEducationDetails(educationdata)

                /*

                                        AppDatabase.getInstance(this@EducationActivity).educationDao().update(
                                           etdegreename.text.toString(),
                                           etfieldofstudy.text.toString(),
                                           etcollege.text.toString(),
                                           etyearofcompletion.text.toString(),
                                           mSessionHolder.User_Login,
                                           mSessionHolder.Source_login,id
                                       )
              */




                return "string"
            }

            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                Log.e("Response", "" + response)
                val intent = Intent(this@EducationActivity, AdvanceResumeActivity::class.java)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finish()
                db!!.closeDB()
                Toast.makeText(applicationContext, "Update", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }

}
