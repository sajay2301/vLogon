package com.vlogonappv1.Database.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.vlogonappv1.Database.entities.UserRegistration


/**
 * Created by axier on 7/2/18.
 */

@Dao
interface UserRegistrationDao {

    @get:Query("SELECT * FROM userregistration")
    val all: List<UserRegistration>

    @Query("SELECT * FROM userregistration WHERE personalemail IN (:providerIds)")
    fun loadAllByIds(providerIds: Int): List<UserRegistration>

    @Query("SELECT * FROM userregistration WHERE personalemail IN (:emailid) and source IN (:source)")
    fun checkdata(emailid: String,source:String): List<UserRegistration>

    @Query("SELECT * FROM userregistration WHERE personalemail IN (:postid)")
    fun loaddatabypostid(postid:Int): List<UserRegistration>


    @Query("SELECT * FROM userregistration WHERE personalemail IN (:userid) and password IN (:postid)")
    fun getdatadata(userid: String,postid:String): List<UserRegistration>

    @Query("UPDATE userregistration SET firstname = :firstname, lastname= :lastname,countrycode= :countrycode,mobilenumber= :mobilenumber,officeemail= :officeemail,profilepic= :profilepic,gender = :gender, address= :address,city= :city,country= :country WHERE personalemail =:id and source =:source")
    fun update(firstname: String, lastname: String,countrycode: String,mobilenumber: String, officeemail: String,  profilepic: String,gender: String, address: String,city: String, country: String,id: String,source: String)


    @Insert
    fun insertAll(providers: List<UserRegistration>)

    @Insert
    fun insert(userData: UserRegistration)

    @Delete
    fun delete(provider: UserRegistration)

}
