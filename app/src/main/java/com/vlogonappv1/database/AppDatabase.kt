package com.vlogonappv1.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.vlogonappv1.database.daos.*
import com.vlogonappv1.database.entities.*


/**
 * Created by axier on 7/2/18.
 */

@Database(entities = [(ExperienceData::class), (UserRegistration::class),(EducationData::class),(SkillsData::class),(AchievementsData::class)], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun educationDao(): EducationDao

    abstract fun userregistrationDao(): UserRegistrationDao

    abstract fun experienceDao(): ExperienceDao

    abstract fun skillsDao(): SkillsDao

    abstract fun achievementDao(): AchievementDao

    companion object {

        /**
         * The only instance
         */
        private var sInstance: AppDatabase? = null
        val DATABASE_NAME = "vlogonappnew.db"

        /**
         * Gets the singleton instance of SampleDatabase.
         *
         * @param context The context.
         * @return The singleton instance of SampleDatabase.
         */
       /* @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                sInstance = Room
                        .databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return sInstance!!
        }*/
    }


}

