package com.vlogon.Database.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.vlogon.Database.entities.EducationData

@Dao
interface VideoDao {

    @get:Query("SELECT * FROM videodata")
    val all: List<EducationData>

    @Query("SELECT * FROM videodata WHERE userid IN (:userid)")
    fun loadvideodatabyid(userid: Int): List<EducationData>

    @Query("SELECT * FROM videodata WHERE userid IN (:userid) and postid IN (:postid)")
    fun checkvideodata(userid: Int,postid:Int): List<EducationData>


    @Query("SELECT * FROM videodata WHERE userid IN (:userid) and postid IN (:postid)")
    fun getdatadata(userid: Int,postid:Int): EducationData

    @Insert
    fun insertAll(providers: List<EducationData>)

    @Insert
    fun insert(postData: EducationData)

    @Delete
    fun delete(provider: EducationData)

}