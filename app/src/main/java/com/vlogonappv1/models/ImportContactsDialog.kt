package com.vlogonappv1.models

import android.app.AlertDialog
import android.view.ViewGroup

import com.vlogonappv1.contactlist.AddressBookActivity
import com.vlogonappv1.contactlist.VcfImporter

import com.vlogonappv1.R
import org.jetbrains.anko.toast


class ImportContactsDialog(val activity: AddressBookActivity, val path: String, private val callback: (refreshView: Boolean) -> Unit) {
    private var targetContactSource = ""

    init {
        val view = (activity.layoutInflater.inflate(R.layout.dialog_import_contacts, null) as ViewGroup).apply {

        }
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Enter Additional Number")
        builder.setPositiveButton("Ok") { dialog, which ->

            Thread {
                val result = VcfImporter(activity).importContacts(path, targetContactSource)
                handleParseResult(result)
                dialog.dismiss()
            }.start()




        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()

    }

    private fun handleParseResult(result: VcfImporter.ImportResult) {
        activity.toast(when (result) {
            VcfImporter.ImportResult.IMPORT_OK -> "importing_successful"
            VcfImporter.ImportResult.IMPORT_PARTIAL -> "importing_some_entries_failed"
            else -> "importing_failed"
        })
        callback(result != VcfImporter.ImportResult.IMPORT_FAIL)
    }
}
