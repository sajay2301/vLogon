package com.vlogonappv1.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vlogonappv1.AppApplication.Companion.mSessionHolder


class AutoStart : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {

        mSessionHolder.backuptimeset = "true"

    }
}
