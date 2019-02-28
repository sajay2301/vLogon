package com.vlogonappv1.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

import java.util.ArrayList
import java.util.Arrays


/**
 * Created by Ashiq on 5/16/16.
 */
class AppPreference private constructor() {

    // common
    private val sharedPreferences: SharedPreferences
    private val settingsPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        sharedPreferences = mContext!!.getSharedPreferences(PrefKey.APP_PREF_NAME, Context.MODE_PRIVATE)
        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        editor = sharedPreferences.edit()
    }

    fun setString(key: String, value: String) {
        editor.putString(key, value)
        editor.commit()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun setBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean? {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setInteger(key: String, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun getInteger(key: String): Int {
        return sharedPreferences.getInt(key, -1)
    }

    fun setStringArray(key: String, values: ArrayList<String>?) {
        if (values != null && !values.isEmpty()) {
            var value = ""
            for (str in values) {
                if (value.isEmpty()) {
                    value = str
                } else {
                    value = "$value,$str"
                }
            }
            setString(key, value)
        }
    }

    fun getStringArray(key: String): ArrayList<String> {
        var arrayList = ArrayList<String>()
        val value = getString(key)
        if (value != null) {
            arrayList =
                ArrayList(Arrays.asList(*value.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
        }
        return arrayList
    }

    companion object {

        // declare context
        private var mContext: Context? = null

        // singleton
        private var appPreference: AppPreference? = null

        fun getInstance(context: Context): AppPreference {
            if (appPreference == null) {
                mContext = context
                appPreference = AppPreference()
            }
            return appPreference as AppPreference
        }
    }


}
