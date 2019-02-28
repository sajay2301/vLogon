package com.vlogonappv1.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey



@Entity
class ExperienceData constructor(personalemail:String,source:String,company:String, title:String, location:String, startdate:String, enddate:String, description:String, currentlyworkingornot:Boolean) {

    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    @ColumnInfo(name = "personalemail")
    var personalemail: String = personalemail

    @ColumnInfo(name = "source")
    var source: String = source

    @ColumnInfo(name = "company")
    var company: String = company

    @ColumnInfo(name = "title")
    var title: String = title

    @ColumnInfo(name = "location")
    var location: String = location

    @ColumnInfo(name = "startdate")
    var startdate: String = startdate

    @ColumnInfo(name = "enddate")
    var enddate: String = enddate

    @ColumnInfo(name = "description")
    var description: String = description

    @ColumnInfo(name = "currentlyworkingornot")
    var currentlyworkingornot: Boolean = currentlyworkingornot

}