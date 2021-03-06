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
import com.vlogonappv1.dataclass.ExperienceDataClass
import com.vlogonappv1.R
import kotlinx.android.synthetic.main.activity_experience.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*


class ExperienceActivity : AppCompatActivity() {

    var currentyworkornot: Boolean = false

    private var company: String? = null
    private var title: String? = null
    private var location: String? = null
    private var startdate: String? = null
    private var enddate: String? = null
    private var description: String? = null

    private var currentlyworkingornot:  Boolean = false
    private var workingornotvalue:  String? = null
    private var id: Int = 0
    private var edit: String? = null
    private var db: DBHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_experience)

        toolbar?.apply {

            tvToolbarTitle.text = resources.getString(R.string.experience)
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }

        checkBoxalowsendnewsoffer.setOnCheckedChangeListener { _, isChecked ->
            currentyworkornot = isChecked
            if(currentyworkornot)
            {
                workingornotvalue="1"
            }else
            {
                workingornotvalue="0"
            }
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
            location = i.getString("location")
            startdate = i.getString("startdate")
            enddate = i.getString("enddate")
            description = i.getString("description")
            workingornotvalue = i.getString("currentlyworkingornot")
            id = i.getInt("id")
            btnsave.text = "Update"

            etcompany.text = Editable.Factory.getInstance().newEditable(company)
            ettitle.text = Editable.Factory.getInstance().newEditable(title)
            etlocation.text = Editable.Factory.getInstance().newEditable(location)
            etstartdate.text = Editable.Factory.getInstance().newEditable(startdate)
            etenddate.text = Editable.Factory.getInstance().newEditable(enddate)
            etdescroption.text = Editable.Factory.getInstance().newEditable(description)
            checkBoxalowsendnewsoffer.isChecked = workingornotvalue.equals("1")



        }else
        {
            btnsave.text = "Save"
        }

    }


    private fun saveTask() {

        class SaveTask : AsyncTask<String, Int, String>() {

            override fun doInBackground(vararg params: String): String? {

                //creating a task

                //adding to database
                db = DBHelper(applicationContext)



                val experiencesdata = ExperienceDataClass()
                experiencesdata.personalemail = mSessionHolder.User_Login
                experiencesdata.source =mSessionHolder.Source_login
                experiencesdata.company = etcompany.text.toString()
                experiencesdata.title = ettitle.text.toString()
                experiencesdata.location =  etlocation.text.toString()
                experiencesdata.startdate = etstartdate.text.toString()
                experiencesdata.enddate = etenddate.text.toString()
                experiencesdata.description = etdescroption.text.toString()
                experiencesdata.currentlyworkingornot = workingornotvalue

                val id_db = db!!.addExperienceDetails(experiencesdata)


                /*val database = AppDatabase.getInstance(this@ExperienceActivity)

                if (database.experienceDao().all.isEmpty()) {


                    val experiencedata = ExperienceData(
                        mSessionHolder.User_Login,
                        mSessionHolder.Source_login,
                        etcompany.text.toString(),
                        ettitle.text.toString(),
                        etlocation.text.toString(),
                        etstartdate.text.toString(),
                        etenddate.text.toString(),
                        etdescroption.text.toString(),
                        currentyworkornot
                    )
                    AppDatabase.getInstance(this@ExperienceActivity).experienceDao().insert(experiencedata)
                    // showErrormessgae("Saved Successfully","1");


                } else {

                    val experiencedata = ExperienceData(
                        mSessionHolder.User_Login,
                        mSessionHolder.Source_login,
                        etcompany.text.toString(),
                        ettitle.text.toString(),
                        etlocation.text.toString(),
                        etstartdate.text.toString(),
                        etenddate.text.toString(),
                        etdescroption.text.toString(),
                        currentyworkornot
                    )
                    AppDatabase.getInstance(this@ExperienceActivity).experienceDao().insert(experiencedata)

                    // showErrormessgae("Saved Successfully","1");

                }

                    AppDatabase.getInstance(this@ExperienceActivity).experienceDao().update(
                        etcompany.text.toString(),
                        ettitle.text.toString(),
                        etlocation.text.toString(),
                        etstartdate.text.toString(),
                        etenddate.text.toString(),
                        etdescroption.text.toString(),
                        currentyworkornot,
                        mSessionHolder.User_Login,
                        mSessionHolder.Source_login
                    )*/



                return "string"
            }

            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                Log.e("Response", "" + response)
                val intent = Intent(this@ExperienceActivity, AdvanceResumeActivity::class.java)
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



                val experiencesdata = ExperienceDataClass()
                experiencesdata.personalemail = mSessionHolder.User_Login
                experiencesdata.source =mSessionHolder.Source_login
                experiencesdata.company = etcompany.text.toString()
                experiencesdata.title = ettitle.text.toString()
                experiencesdata.location =  etlocation.text.toString()
                experiencesdata.startdate = etstartdate.text.toString()
                experiencesdata.enddate = etenddate.text.toString()
                experiencesdata.description = etdescroption.text.toString()
                experiencesdata.currentlyworkingornot = workingornotvalue
                experiencesdata.eid = id

                val id_db = db!!.updateExperienceDetails(experiencesdata)

               // AppDatabase.getInstance(this@ExperienceActivity).experienceDao().update(etcompany.text.toString(), ettitle.text.toString(),etlocation.text.toString(), etstartdate.text.toString(),etenddate.text.toString(), etdescroption.text.toString(),currentyworkornot,mSessionHolder.User_Login,mSessionHolder.Source_login,id)

                return "string"
            }

            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                Log.e("Response", "" + response)
                val intent = Intent(this@ExperienceActivity, AdvanceResumeActivity::class.java)
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

    override fun onBackPressed() {


        val intent = Intent(this@ExperienceActivity, AdvanceResumeActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()



    }
}
