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

import android.os.Environment
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast



import com.vlogonappv1.MainActivity
import com.vlogonappv1.Permissions
import com.vlogonappv1.R
import com.vlogonappv1.db.DBHelper

import java.io.File


class LocalBackup(private val activity: MainActivity) {

    //ask to the user a name for the backup and perform it. The backup will be saved to a custom folder.
    fun performBackup(db: DBHelper, outFileName: String) {

        Permissions.verifyStoragePermissions(activity)

        val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + activity.resources.getString(R.string.app_name))

        var success = true
        if (!folder.exists())
            success = folder.mkdirs()
        if (success) {

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Backup Name")
            val input = EditText(activity)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton("Save") { dialog, which ->
                val m_Text = input.text.toString()
                val out = "$outFileName$m_Text.db"
                db.backup(out)
            }
            builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

            builder.show()
        } else
            Toast.makeText(activity, "Unable to create directory. Retry", Toast.LENGTH_SHORT).show()
    }

    //ask to the user what backup to restore
    fun performRestore(db: DBHelper) {

        Permissions.verifyStoragePermissions(activity)

        val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + activity.resources.getString(
            R.string.app_name))
        if (folder.exists()) {

            val files = folder.listFiles()

            val arrayAdapter = ArrayAdapter<String>(activity, android.R.layout.select_dialog_item)
            for (file in files)
                arrayAdapter.add(file.name)

            val builderSingle = AlertDialog.Builder(activity)
            builderSingle.setTitle("Restore:")
            builderSingle.setNegativeButton(
                    "cancel"
            ) { dialog, which -> dialog.dismiss() }
            builderSingle.setAdapter(
                    arrayAdapter
            ) { dialog, which ->
                try {
                    db.importDB(files[which].path)
                } catch (e: Exception) {
                    Toast.makeText(activity, "Unable to restore. Retry", Toast.LENGTH_SHORT).show()
                }
            }
            builderSingle.show()
        } else
            Toast.makeText(activity, "Backup folder not present.\nDo a backup before a restore!", Toast.LENGTH_SHORT).show()
    }

}
