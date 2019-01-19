package com.vlogon.Database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class EducationData constructor(personalemail:String,source:String,degree:String, fieldtostudy:String, college:String, Yearofcomletion:String) {

    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    @ColumnInfo(name = "personalemail")
    var personalemail: String = personalemail

    @ColumnInfo(name = "source")
    var source: String = source

    @ColumnInfo(name = "degree")
    var degree: String = degree

    @ColumnInfo(name = "fieldtostudy")
    var fieldtostudy: String = fieldtostudy

    @ColumnInfo(name = "college")
    var college: String = college

    @ColumnInfo(name = "Yearofcomletion")
    var Yearofcomletion: String = Yearofcomletion



}