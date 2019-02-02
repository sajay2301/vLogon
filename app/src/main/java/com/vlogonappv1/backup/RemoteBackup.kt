/*
 *   Copyright 2016 Marco Gomiero
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.vlogonappv1.backup

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.os.ParcelFileDescriptor
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.drive.*
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import com.google.android.gms.tasks.*
import com.vlogonappv1.AppApplication
import com.vlogonappv1.Class.ProgressDialogshow
import com.vlogonappv1.db.DBHelper.Companion.DATABASE_NAME

import java.io.*
import java.text.SimpleDateFormat
import java.util.*


abstract class RemoteBackup: AppCompatActivity()  {

    protected var driveClient: DriveClient? = null
        private set
    open lateinit var dialog: Dialog

    protected var driveResourceClient: DriveResourceClient? = null
        private set

    var signInAccount: GoogleSignInAccount? = null

    var backupname:String=""

    private var mOpenItemTaskSource: TaskCompletionSource<DriveId>? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                if (resultCode != Activity.RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    Log.e(TAG, "Sign-in failed.")
                    finish()
                    return
                }

                val getAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                if (getAccountTask.isSuccessful) {

                    initializeDriveClient(getAccountTask.result)
                } else {
                    Log.e(TAG, "Sign-in failed.")
                    finish()
                }
            }
            REQUEST_CODE_OPEN_ITEM -> if (resultCode == Activity.RESULT_OK) {
                val driveId = data.getParcelableExtra<DriveId>(
                    OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID
                )
                mOpenItemTaskSource!!.setResult(driveId)
            } else {
                mOpenItemTaskSource!!.setException(RuntimeException("Unable to open file"))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

     fun signIn(backup: String) {

        backupname=backup
        val requiredScopes = HashSet<Scope>(2)
        requiredScopes.add(Drive.SCOPE_FILE)
        requiredScopes.add(Drive.SCOPE_APPFOLDER)
        signInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (signInAccount != null && signInAccount!!.grantedScopes.containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount)
        } else {

            startActivityForResult(googleSignInClient.signInIntent, REQUEST_CODE_SIGN_IN)
        }
    }

    private val googleSignInClient: GoogleSignInClient by lazy {

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Drive.SCOPE_FILE)
            .requestScopes(Drive.SCOPE_APPFOLDER)
            .build()
        GoogleSignIn.getClient(this, signInOptions)
    }

    /**
     * Continues the sign-in process, initializing the Drive clients with the current
     * user's account.
     */
     fun initializeDriveClient(signInAccount: GoogleSignInAccount?) {
        driveClient = Drive.getDriveClient(this, signInAccount!!)
        driveResourceClient = Drive.getDriveResourceClient(this, signInAccount)

        AppApplication.mSessionHolder.accountname= signInAccount!!.email.toString()
        AppApplication.mSessionHolder.setonnotset= "set"

        onDriveClientReady()



    }


      abstract fun onDriveClientReady()

    fun logout() {
        googleSignInClient.signOut()
        signInAccount = null
    }





    companion object {
        private val TAG = "BaseDriveActivity"

        /**
         * Request code for Google Sign-in
         */
        protected val REQUEST_CODE_SIGN_IN = 0

        /**
         * Request code for the Drive picker
         */
        protected val REQUEST_CODE_OPEN_ITEM = 1

    }
}
