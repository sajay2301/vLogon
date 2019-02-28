package com.vlogonappv1.resumeactivity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.R
import kotlinx.android.synthetic.main.activity_advance_resume.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.vlogonappv1.db.DBHelper
import com.vlogonappv1.adapter.AchievementAdapter
import com.vlogonappv1.adapter.EducationAdapter
import com.vlogonappv1.adapter.ExperienceAdapter
import com.vlogonappv1.adapter.SkillsAdapter
import com.vlogonappv1.AppApplication.Companion.mSessionHolder
import com.vlogonappv1.dataclass.AchievementsDataClass
import com.vlogonappv1.dataclass.EducationDataClass
import com.vlogonappv1.dataclass.ExperienceDataClass
import com.vlogonappv1.dataclass.SkillsDataClass


class AdvanceResumeActivity : AppCompatActivity() {


    var isFromSwipe: Boolean = false
    val itemListEducation = ArrayList<EducationDataClass>()
    val itemListExperience = ArrayList<ExperienceDataClass>()

    val itemListAchievement = ArrayList<AchievementsDataClass>()
    val itemListSkills = ArrayList<SkillsDataClass>()
    private var db: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advance_resume)

        db = DBHelper(applicationContext)
        toolbar?.apply {

            tvToolbarTitle.text = "Advance Resume"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility=View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
       
        layouteducation.clicks().subscribe{
            val intent = Intent(this@AdvanceResumeActivity, EducationActivity::class.java)
            intent.putExtra("edit", "false")
            overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)
            finish()
        }


        layoutexperience.clicks().subscribe{
            val intent = Intent(this@AdvanceResumeActivity, ExperienceActivity::class.java)
            intent.putExtra("edit", "false")
            overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)
            finish()
        }

        layoutskills.clicks().subscribe{
            val intent = Intent(this@AdvanceResumeActivity, SkillsActivity::class.java)
            intent.putExtra("edit", "false")
            overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)
            finish()
        }

        layoutachivement.clicks().subscribe{
            val intent = Intent(this@AdvanceResumeActivity, AchievementsActivity::class.java)
            intent.putExtra("edit", "false")
            overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)
            finish()
        }


        rveducation?.apply {
            layoutManager = LinearLayoutManager(this@AdvanceResumeActivity)
            adapter = EducationAdapter(itemListEducation,
                onItemClick = { position ->

                    val intent = Intent(this@AdvanceResumeActivity, EducationActivity::class.java)

                    intent.putExtra("degree",itemListEducation[position].degree)
                    intent.putExtra("feildofstudy",itemListEducation[position].fieldtostudy)
                    intent.putExtra("college",itemListEducation[position].college)
                    intent.putExtra("yearofcompletion",itemListEducation[position].Yearofcomletion)
                    intent.putExtra("id", itemListEducation[position].eid)
                    intent.putExtra("edit", "true")
                    startActivity(intent)
                    overridePendingTransition(R.anim.enter, R.anim.exit)
                    finish()
                }, activity = this@AdvanceResumeActivity
            )


        }
        rvexperience?.apply {
            layoutManager = LinearLayoutManager(this@AdvanceResumeActivity)
            adapter = ExperienceAdapter(itemListExperience,
                onItemClick = { position ->

                    val intent = Intent(this@AdvanceResumeActivity, ExperienceActivity::class.java)

                    intent.putExtra("company",itemListExperience[position].company)
                    intent.putExtra("title",itemListExperience[position].title)
                    intent.putExtra("location",itemListExperience[position].location)
                    intent.putExtra("startdate",itemListExperience[position].startdate)
                    intent.putExtra("enddate",itemListExperience[position].enddate)
                    intent.putExtra("description",itemListExperience[position].description)
                    intent.putExtra("currentlyworkingornot",itemListExperience[position].currentlyworkingornot)
                    intent.putExtra("id", itemListExperience[position].eid)
                    intent.putExtra("edit", "true")
                    startActivity(intent)
                    overridePendingTransition(R.anim.enter, R.anim.exit)
                    finish()

                }, activity = this@AdvanceResumeActivity
            )


        }

        rvachievements?.apply {
            layoutManager = LinearLayoutManager(this@AdvanceResumeActivity)
            adapter = AchievementAdapter(itemListAchievement,
                onItemClick = { position ->

                    val intent = Intent(this@AdvanceResumeActivity, AchievementsActivity::class.java)

                    intent.putExtra("company",itemListAchievement[position].company)
                    intent.putExtra("title",itemListAchievement[position].title)
                    intent.putExtra("link",itemListAchievement[position].link)
                    intent.putExtra("discription",itemListAchievement[position].discription)
                    intent.putExtra("id", itemListAchievement[position].aid)
                    intent.putExtra("edit", "true")
                    startActivity(intent)
                    overridePendingTransition(R.anim.enter, R.anim.exit)
                    finish()
                }, activity = this@AdvanceResumeActivity
            )


        }
        rvskilss?.apply {
            layoutManager = LinearLayoutManager(this@AdvanceResumeActivity)
            adapter = SkillsAdapter(itemListSkills,
                onItemClick = { position ->

                    val intent = Intent(this@AdvanceResumeActivity, SkillsActivity::class.java)

                    intent.putExtra("skill",itemListSkills[position].skills)
                    intent.putExtra("id", itemListSkills[position].sid)
                    intent.putExtra("edit", "true")
                    startActivity(intent)
                    overridePendingTransition(R.anim.enter, R.anim.exit)
                    finish()

                }, activity = this@AdvanceResumeActivity
            )


        }
        getEducation()
        getExperienceData()
        getAchievement()
        getSkilldata()

    }

    private fun getEducation() {
        itemListEducation.clear()
        class GetTasks : AsyncTask<Void, Void, List<EducationDataClass>>() {

            override fun doInBackground(vararg voids: Void): List<EducationDataClass> {
                return db!!.allEducationDetails(mSessionHolder.User_Login, mSessionHolder.Source_login)
            }

            override fun onPostExecute(tasks: List<EducationDataClass>) {
                super.onPostExecute(tasks)


                try {

                 /*   etdegreename.text = Editable.Factory.getInstance().newEditable(tasks[0].degree)
                    etfieldofstudy.text = Editable.Factory.getInstance().newEditable(tasks[0].fieldtostudy)
                    etcollege.text = Editable.Factory.getInstance().newEditable(tasks[0].college)
                    etyearofcompletion.text = Editable.Factory.getInstance().newEditable(tasks[0].Yearofcomletion)*/

                    itemListEducation.addAll(tasks)
                    rveducation.adapter!!.notifyDataSetChanged()

                }catch (e:IndexOutOfBoundsException)
                {
                        Log.e("dasda",e.message)
                }

            }
        }

        val gt = GetTasks()
        gt.execute()
    }

    private fun getExperienceData() {
        itemListExperience.clear()
        class GetTasks : AsyncTask<Void, Void, List<ExperienceDataClass>>() {

            override fun doInBackground(vararg voids: Void): List<ExperienceDataClass> {
                return db!!.allExperienceDetails(mSessionHolder.User_Login, mSessionHolder.Source_login)
            }

            override fun onPostExecute(tasks: List<ExperienceDataClass>) {
                super.onPostExecute(tasks)


                try {

                    itemListExperience.addAll(tasks)
                    rvexperience.adapter!!.notifyDataSetChanged()

                } catch (e: IndexOutOfBoundsException) {

                }
            }
        }

        val gt = GetTasks()
        gt.execute()
    }
    private fun getAchievement() {
        itemListAchievement.clear()
        class GetTasks : AsyncTask<Void, Void, List<AchievementsDataClass>>() {

            override fun doInBackground(vararg voids: Void): List<AchievementsDataClass> {
                return db!!.allAchievmentDetails(mSessionHolder.User_Login, mSessionHolder.Source_login)
            }

            override fun onPostExecute(tasks: List<AchievementsDataClass>) {
                super.onPostExecute(tasks)

                try {

                    itemListAchievement.addAll(tasks)
                    rvachievements.adapter!!.notifyDataSetChanged()

                } catch (e: IndexOutOfBoundsException) {

                }

            }
        }

        val gt = GetTasks()
        gt.execute()
    }


    private fun getSkilldata() {
        itemListSkills.clear()
        class GetTasks : AsyncTask<Void, Void, List<SkillsDataClass>>() {

            override fun doInBackground(vararg voids: Void): List<SkillsDataClass> {
                return db!!.allSkillDetails(mSessionHolder.User_Login, mSessionHolder.Source_login)
            }

            override fun onPostExecute(tasks: List<SkillsDataClass>) {
                super.onPostExecute(tasks)



                try {
                    itemListSkills.addAll(tasks)
                    rvskilss.adapter!!.notifyDataSetChanged()

                } catch (e: IndexOutOfBoundsException) {

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
