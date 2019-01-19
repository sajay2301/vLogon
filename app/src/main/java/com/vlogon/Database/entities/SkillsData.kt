package com.vlogon.Database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class SkillsData constructor(personalemail:String,source:String,skills:String) {

    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    @ColumnInfo(name = "personalemail")
    var personalemail: String = personalemail

    @ColumnInfo(name = "source")
    var source: String = source

    @ColumnInfo(name = "skills")
    var skills: String = skills



}