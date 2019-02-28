package com.vlogonappv1.database.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.vlogonappv1.database.entities.ExperienceData

@Dao
interface ExperienceDao {

    @get:Query("SELECT * FROM experiencedata")
    val all: List<ExperienceData>

    @Query("SELECT * FROM experiencedata WHERE personalemail IN (:personalemail)")
    fun loadAllByIds(personalemail: String): List<ExperienceData>

    @Query("SELECT * FROM experiencedata WHERE personalemail IN (:personalemail) and source IN (:source) order by enddate")
    fun checkdata(personalemail: String,source:String): List<ExperienceData>

    @Query("SELECT * FROM experiencedata WHERE personalemail IN (:personalemail)")
    fun loaddatabypostid(personalemail:String): List<ExperienceData>


    @Query("SELECT * FROM experiencedata WHERE personalemail IN (:personalemail) and source IN (:source)")
    fun getdatadata(personalemail: String,source:String): ExperienceData

    @Query("UPDATE experiencedata SET company = :company, title= :title,location= :location,startdate= :startdate, enddate= :enddate,description= :description,currentlyworkingornot= :currentlyworkingornot WHERE personalemail =:id and source =:source and uid =:uid")
    fun update(company: String, title: String,location: String, startdate: String,enddate: String, description: String,currentlyworkingornot: Boolean, id: String,source: String,uid: Int)


    @Insert
    fun insertAll(providers: List<ExperienceData>)

    @Insert
    fun insert(userData: ExperienceData)

    @Delete
    fun delete(provider: ExperienceData)
}