package com.vlogonappv1.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class AchievementsData constructor(personalemail:String,source:String,company:String, title:String, link:String, discription:String) {

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

    @ColumnInfo(name = "link")
    var link: String = link

    @ColumnInfo(name = "discription")
    var discription: String = discription



}