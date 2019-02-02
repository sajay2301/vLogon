package com.vlogonappv1.Database.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.vlogonappv1.Database.entities.AchievementsData

@Dao
interface AchievementDao {



    @get:Query("SELECT * FROM achievementsdata")
    val all: List<AchievementsData>

    @Query("SELECT * FROM achievementsdata WHERE personalemail IN (:personalemail)")
    fun loadAllByIds(personalemail: String): List<AchievementsData>

    @Query("SELECT * FROM achievementsdata WHERE personalemail IN (:personalemail) and source IN (:source)")
    fun checkdata(personalemail: String,source:String): List<AchievementsData>

    @Query("SELECT * FROM achievementsdata WHERE personalemail IN (:personalemail)")
    fun loaddatabypostid(personalemail:String): List<AchievementsData>


    @Query("SELECT * FROM achievementsdata WHERE personalemail IN (:personalemail) and source IN (:source)")
    fun getdatadata(personalemail: String,source:String): AchievementsData

    @Query("UPDATE achievementsdata SET company = :company, title= :title,link= :link,discription= :description WHERE personalemail =:id and source =:source and uid =:uid")
    fun update(company: String, title: String,link: String, description: String, id: String,source: String,uid: Int)


    @Insert
    fun insertAll(providers: List<AchievementsData>)

    @Insert
    fun insert(userData: AchievementsData)

    @Delete
    fun delete(provider: AchievementsData)


}