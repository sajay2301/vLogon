package com.vlogonappv1.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.vlogonappv1.activity.MainActivity.Companion.REQUEST_CODE_PERMISSIONS


object Permissions {

    // Storage Permissions variables
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS,Manifest.permission.CAMERA)

    //check permissions.
    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have read or write permission
        val writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        val readcontact = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
        val camerapermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        if (writePermission != PackageManager.PERMISSION_GRANTED && readPermission != PackageManager.PERMISSION_GRANTED && readcontact != PackageManager.PERMISSION_GRANTED && camerapermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                PERMISSIONS_STORAGE,
                    REQUEST_CODE_PERMISSIONS
            )
        }
    }

}
