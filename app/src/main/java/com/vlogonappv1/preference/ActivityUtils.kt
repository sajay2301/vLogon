package com.vlogonappv1.preference

import android.app.Activity
import android.content.Intent


/**
 * Created by Ashiq on 5/11/16.
 */
class ActivityUtils {

    fun invokeActivity(activity: Activity, tClass: Class<*>, shouldFinish: Boolean) {
        val intent = Intent(activity, tClass)
        activity.startActivity(intent)
        if (shouldFinish) {
            activity.finish()
        }
    }

    companion object {

        private var sActivityUtils: ActivityUtils? = null

        val instance: ActivityUtils
            get() {
                if (sActivityUtils == null) {
                    sActivityUtils = ActivityUtils()
                }
                return sActivityUtils!!
            }
    }


}
