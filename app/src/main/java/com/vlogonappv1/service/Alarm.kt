package com.vlogonappv1.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import com.vlogonappv1.BackupActivity
import java.util.*


class Alarm (private val activity: BackupActivity) {


    fun setAlarm(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, AutoStart::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, i, 0)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY,5)
        calendar.set(Calendar.MINUTE, 1)
        calendar.set(Calendar.SECOND, 0)
        am.setRepeating(AlarmManager.RTC,  calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pi)
    }

    fun cancleAlarm(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, AutoStart::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, i, 0)
        am.cancel(pi)
    }
}