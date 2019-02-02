package com.vlogonappv1.pref

import android.content.SharedPreferences


/**
 * Created by stllpt031 on 05/02/2018.
 */
class SessionHolder(val pref: SharedPreferences) {
    var User_Login : String by pref.prefString("")
    var Source_login : String by pref.prefString("")
    var USER_LAT : Long by pref.prefLong(0L)
    var USER_LNG : Long by pref.prefLong(0L)
    var USER_ID : Int by pref.prefInt(0)

    var Login_id : String by pref.prefString("")
    var Login_Password :String by pref.prefString("")


    var User_Firstname : String by pref.prefString("")
    var User_Lastname : String by pref.prefString("")
    var User_Countrycode : String by pref.prefString("")
    var User_Mobilenumber : String by pref.prefString("")
    var User_PersonalEmailId :String by pref.prefString("")
    var User_OfficeEmailId :String by pref.prefString("")
    var User_termandcondition :Boolean by pref.prefBoolean(false)
    var User_allowoffer :Boolean by pref.prefBoolean(false)
    var User_LoginSource :String by pref.prefString("")
    var User_EmailSource :String by pref.prefString("")

    var User_Password :String by pref.prefString("")
    var User_ConfirmPassword :String by pref.prefString("")

    var User_VerifyEmailId :String by pref.prefString("")

    var User_personalcolorcode :String by pref.prefString("")
    var User_officecolorcode :String by pref.prefString("")

    var User_ActivityName :String by pref.prefString("")

    var Backupname :String by pref.prefString("")
    var twostepverify :String by pref.prefString("false")


    var accountname :String by pref.prefString("")
    var setonnotset :String by pref.prefString("")



    var firsttimeload :String by pref.prefString("true")

    var setpaswordornot :String by pref.prefString("false")

    var backupsetupornot :String by pref.prefString("false")


    var backuptimeset :String by pref.prefString("false")

    var alarmsetornot :Boolean by pref.prefBoolean(false)

}