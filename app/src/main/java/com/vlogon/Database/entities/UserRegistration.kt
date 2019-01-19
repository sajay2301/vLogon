package com.vlogon.Database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey







/**
 * Created by axier on 7/2/18.
 */

@Entity
class UserRegistration constructor(firstname:String, lastname:String, mobilenumber:String, personalemail:String, officeemail:String, password:String, profilepic:String,source:String,gender:String, address:String, city:String,country:String,countrycode:String) {

    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    @ColumnInfo(name = "firstname")
    var firstname: String = firstname

    @ColumnInfo(name = "lastname")
    var lastname: String = lastname

    @ColumnInfo(name = "mobilenumber")
    var mobilenumber: String = mobilenumber

    @ColumnInfo(name = "personalemail")
    var personalemail: String = personalemail

    @ColumnInfo(name = "officeemail")
    var officeemail: String = officeemail

    @ColumnInfo(name = "password")
    var password: String = password

    @ColumnInfo(name = "profilepic")
    var profilepic: String = profilepic

    @ColumnInfo(name = "source")
    var source: String = source

    @ColumnInfo(name = "gender")
    var gender: String = gender

    @ColumnInfo(name = "address")
    var address: String = address

    @ColumnInfo(name = "city")
    var city: String = city

    @ColumnInfo(name = "country")
    var country: String = country


    @ColumnInfo(name = "countrycode")
    var countrycode: String = countrycode



}