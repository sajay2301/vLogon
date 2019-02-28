package com.vlogonappv1.extensions

import android.annotation.SuppressLint
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.widget.TextView
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun String.getDateTimeFromDateString(viewToUpdate: TextView? = null): String {
    val dateFormats = ""
   return dateFormats
}

@SuppressLint("NewApi")
fun String.normalizeNumber() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    PhoneNumberUtils.normalizeNumber(this)
} else {
    PhoneNumberUtils.normalizeNumber(this)
}
