package com.vlogonapp.ResumeActivity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonapp.R
import kotlinx.android.synthetic.main.activity_advance_resume.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import com.vlogonapp.Adapter.AchievementAdapter
import com.vlogonapp.Adapter.EducationAdapter
import com.vlogonapp.Adapter.ExperienceAdapter
import com.vlogonapp.Adapter.SkillsAdapter
import com.vlogonapp.AppApplication
import com.vlogonapp.AppApplication.Companion.mSessionHolder
import com.vlogonapp.Database.AppDatabase
import com.vlogonapp.Database.entities.AchievementsData
import com.vlogonapp.Database.entities.EducationData
import com.vlogonapp.Database.entities.ExperienceData
import com.vlogonapp.Database.entities.SkillsData


class AdvanceResumeActivity : AppCompatActivity() {


    var isFromSwipe: Boolean = false
    val itemListEducation = ArrayList<EducationData>()
    val itemListExperience = ArrayList<ExperienceData>()

    val itemListAchievement = ArrayList<AchievementsData>()
    val itemListSkills = ArrayList<SkillsData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advance_resume)


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
                    intent.putExtra("id", itemListEducation[position].uid)
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
                    intent.putExtra("id", itemListExperience[position].uid)
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
                    intent.putExtra("id", itemListAchievement[position].uid)
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
                    intent.putExtra("id", itemListSkills[position].uid)
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
        class GetTasks : AsyncTask<Void, Void, List<EducationData>>() {

            override fun doInBackground(vararg voids: Void): List<EducationData> {
                return AppDatabase
                    .getInstance(applicationContext)
                    .educationDao()
                    .checkdata(AppApplication.mSessionHolder.User_Login, AppApplication.mSessionHolder.Source_login)
            }

            override fun onPostExecute(tasks: List<EducationData>) {
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

                }

            }
        }

        val gt = GetTasks()
        gt.execute()
    }

    private fun getExperienceData() {
        itemListExperience.clear()
        class GetTasks : AsyncTask<Void, Void, List<ExperienceData>>() {

            override fun doInBackground(vararg voids: Void): List<ExperienceData> {
                return AppDatabase
                    .getInstance(applicationContext)
                    .experienceDao()
                    .checkdata(mSessionHolder.User_Login, mSessionHolder.Source_login)
            }

            override fun onPostExecute(tasks: List<ExperienceData>) {
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
        class GetTasks : AsyncTask<Void, Void, List<AchievementsData>>() {

            override fun doInBackground(vararg voids: Void): List<AchievementsData> {
                return AppDatabase
                    .getInstance(applicationContext)
                    .achievementDao()
                    .checkdata(mSessionHolder.User_Login, mSessionHolder.Source_login)
            }

            override fun onPostExecute(tasks: List<AchievementsData>) {
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
        class GetTasks : AsyncTask<Void, Void, List<SkillsData>>() {

            override fun doInBackground(vararg voids: Void): List<SkillsData> {
                return AppDatabase
                    .getInstance(applicationContext)
                    .skillsDao()
                    .checkdata(mSessionHolder.User_Login, mSessionHolder.Source_login)
            }

            override fun onPostExecute(tasks: List<SkillsData>) {
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
