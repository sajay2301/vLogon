package com.vlogon.Database.daos

import android.arch.persistence.room.*
import com.vlogon.Database.entities.EducationData


/**
 * Created by axier on 7/2/18.
 */

@Dao
interface EducationDao {


    @get:Query("SELECT * FROM educationdata")
    val all: List<EducationData>

    @Query("SELECT * FROM educationdata WHERE personalemail IN (:personalemail)")
    fun loadAllByIds(personalemail: String): List<EducationData>

    @Query("SELECT * FROM educationdata WHERE personalemail IN (:personalemail) and source IN (:source) order by Yearofcomletion")
    fun checkdata(personalemail: String,source:String): List<EducationData>

    @Query("SELECT * FROM educationdata WHERE personalemail IN (:personalemail)")
    fun loaddatabypostid(personalemail:String): List<EducationData>


    @Query("SELECT * FROM educationdata WHERE personalemail IN (:personalemail) and source IN (:source)")
    fun getdatadata(personalemail: String,source:String): EducationData

    @Insert
    fun insertAll(providers: List<EducationData>)

    @Query("UPDATE educationdata SET degree = :degree, fieldtostudy= :fieldtostudy,college= :college,Yearofcomletion= :Yearofcomletion WHERE personalemail =:personalemail and source =:source and uid =:uid")
    fun update(degree: String, fieldtostudy: String,college: String, Yearofcomletion: String, personalemail: String,source: String,uid: Int)

    @Insert
    fun insert(userData: EducationData)

    @Update
    fun update(userData: EducationData)

    @Delete
    fun delete(provider: EducationData)

}
