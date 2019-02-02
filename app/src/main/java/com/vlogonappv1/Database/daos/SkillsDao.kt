package com.vlogonappv1.Database.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.vlogonappv1.Database.entities.SkillsData
@Dao
interface SkillsDao {


    @get:Query("SELECT * FROM skillsdata")
    val all: List<SkillsData>

    @Query("SELECT * FROM skillsdata WHERE personalemail IN (:personalemail)")
    fun loadAllByIds(personalemail: String): List<SkillsData>

    @Query("SELECT * FROM skillsdata WHERE personalemail IN (:personalemail) and source IN (:source)")
    fun checkdata(personalemail: String,source:String): List<SkillsData>

    @Query("SELECT * FROM skillsdata WHERE personalemail IN (:personalemail)")
    fun loaddatabypostid(personalemail:String): List<SkillsData>


    @Query("SELECT * FROM skillsdata WHERE personalemail IN (:personalemail) and source IN (:source)")
    fun getdatadata(personalemail: String,source:String): SkillsData

    @Query("UPDATE skillsdata SET skills = :skills WHERE personalemail =:id and source =:source and uid =:uid")
    fun update(skills: String, id: String,source: String,uid: Int)

    @Insert
    fun insertAll(providers: List<SkillsData>)

    @Insert
    fun insert(userData: SkillsData)

    @Delete
    fun delete(provider: SkillsData)
}