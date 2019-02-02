package com.vlogonappv1

import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.facebook.FacebookSdk
import io.reactivex.subjects.PublishSubject


import android.util.Log
import com.twitter.sdk.android.core.*
import com.vlogonappv1.pref.SessionHolder
import com.vlogonappv1.pref.getPrefInstance


class AppApplication : MultiDexApplication() {



    val notificationPublishSubject = PublishSubject.create<String>()

    companion object {
        lateinit var instance : AppApplication
        fun getApplicationContext() = instance
        lateinit var mSessionHolder: SessionHolder

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MultiDex.install(this)
        FacebookSdk.sdkInitialize(instance)
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(
                TwitterAuthConfig(
                    getString(R.string.twitter_consumer_key),
                    getString(R.string.twitter_consumer_secret)
                )
            )
            .debug(true)
            .build()
        Twitter.initialize(config)
        mSessionHolder = SessionHolder(AppApplication.instance.getPrefInstance("SessionData"))

    }




}