package com.vlogonappv1.contactlist

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.R
import com.vlogonappv1.adapter.BlockAdapter
import com.vlogonappv1.dataclass.ContactListItem
import com.vlogonappv1.dataclass.ProgressDialogshow
import com.vlogonappv1.db.DBHelper
import kotlinx.android.synthetic.main.activity_block_contact.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*


class BlockContactActivity : AppCompatActivity() {


    private var db: DBHelper? = null
    var exist: Int = 0
    lateinit var Firestoredb: FirebaseFirestore
    lateinit var dialog: Dialog
    internal var flag = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_contact)
        toolbar?.apply {

            tvToolbarTitle.text = "Block Contact"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
        Firestoredb = FirebaseFirestore.getInstance()
        db = DBHelper(this@BlockContactActivity)
        exist = db!!.tableExists()

        getdata()
    }

    private fun getdata() {
        flag = 0
        dialog = ProgressDialogshow.progressDialog(this@BlockContactActivity)
        dialog.show()
        val contactList = ArrayList<ContactListItem>()
        var contactListItem: ContactListItem

        val c = db!!.getBlockContactData()
        if (c != null && c.count > 0) {
            while (c.moveToNext()) {
                val blockcontactid = c.getInt(0)
                val name = c.getString(1)
                val phoneNo = c.getString(2)
                val emailid = c.getString(3)
                contactListItem = ContactListItem()
                contactListItem.contactid = blockcontactid
                contactListItem.contactusername = name
                contactListItem.contactNumber = phoneNo
                contactListItem.contactemail = emailid
                contactList.add(contactListItem)
            }
        }

        dialog.dismiss()
        val contactAdapter = BlockAdapter(contactList, this@BlockContactActivity)
        rvContacts.layoutManager = LinearLayoutManager(this@BlockContactActivity)
        rvContacts.adapter = contactAdapter
    }

    override fun onBackPressed() {

        val intent = Intent(this@BlockContactActivity, AddressBookActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        finish()


    }


}
