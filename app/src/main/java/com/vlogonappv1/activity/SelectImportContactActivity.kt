package com.vlogonappv1.activity

import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.AppApplication
import com.vlogonappv1.R
import com.vlogonappv1.adapter.UnregisterAdapter
import com.vlogonappv1.contactlist.AddressBookActivity
import com.vlogonappv1.dataclass.ContactListItem
import com.vlogonappv1.dataclass.ProgressDialogshow
import com.vlogonappv1.dataclass.UserRegistrationClass
import com.vlogonappv1.db.DBHelper
import kotlinx.android.synthetic.main.activity_select_import_contact.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*

class SelectImportContactActivity : AppCompatActivity() {


    private var db: DBHelper? = null

    var exist: Int = 0
    lateinit var Firestoredb: FirebaseFirestore


    internal var flag = 0




    lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_import_contact)

        toolbar?.apply {

            tvToolbarTitle.text = "Add Contact"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }

        Firestoredb = FirebaseFirestore.getInstance()


        displayAllContacts()

        btnaddcontact.clicks().subscribe{

            dialog = ProgressDialogshow.progressDialog(this@SelectImportContactActivity)
            dialog.show()

            val contactList = ArrayList<ContactListItem>()
            var contactListItem: ContactListItem

            for(item in UnregisterAdapter.itemsList) {

                contactListItem = ContactListItem()
                contactListItem.contactNumber = item.contactNumber
                contactListItem.contactemail = item.contactemail
                contactListItem.contactusername = item.contactusername
                contactListItem.contactfirstname = item.contactfirstname
                contactListItem.contactlastname = item.contactlastname
                contactListItem.contactebirthdate = item.contactebirthdate
                contactListItem.contactimage = item.contactimage
                contactListItem.contactgender = item.contactgender
                contactListItem.contactaddress = item.contactaddress
                contactListItem.contactcountrycode = item.contactcountrycode
                contactListItem.contactadditionalnumber = item.contactadditionalnumber
                contactListItem.contacttage = item.contacttage
                contactListItem.status = "update"
                contactList.add(contactListItem)
                db = DBHelper(this@SelectImportContactActivity)
                val checkdata = db!!.getCheckdataData(item.contactNumber.toString())
                if (checkdata != null && checkdata.count > 0)
                {
                }else
                {
                    val id_db = db!!.addDublicateContactdata(contactListItem)
                }
                db!!.closeDB()

                            //filterdata.add(item)
            }

            dialog.dismiss()

            DetailViewPagerActivity.openPostNewsDetails(this, UnregisterAdapter.itemsList, 0)
            finish()
        }
    }



    private fun displayAllContacts() {

        val contactList = ArrayList<ContactListItem>()
        var contactListItem: ContactListItem
        db = DBHelper(this@SelectImportContactActivity)

        val c = db!!.getAllData()
        if (c != null && c.count > 0) {
            while (c.moveToNext()) {

                val userid = c.getInt(0)
                val username = c.getString(1)
                val phoneNo = c.getString(2)
                val emailid = c.getString(3)
                val firstname = c.getString(4)
                val lastname = c.getString(5)
                val birthdate = c.getString(6)
                val profilepic = c.getString(7)
                val gender = c.getString(8)
                val address = c.getString(9)
                val countrycode = c.getString(10)
                val additionalnumber = c.getString(11)
                val contact_tage = c.getString(12)


                contactListItem = ContactListItem()
                contactListItem.contactid = userid
                contactListItem.contactNumber = phoneNo
                contactListItem.contactemail = emailid
                contactListItem.contactusername = username
                contactListItem.contactfirstname = firstname
                contactListItem.contactlastname = lastname
                contactListItem.contactebirthdate = birthdate
                contactListItem.contactimage = profilepic
                contactListItem.contactgender = gender
                contactListItem.contactaddress = address
                contactListItem.contactcountrycode = countrycode
                contactListItem.contactadditionalnumber = additionalnumber
                contactListItem.contacttage = contact_tage


                if(AppApplication.mSessionHolder.User_Countrycode +""+AppApplication.mSessionHolder.User_Mobilenumber == (countrycode + "" + phoneNo).replace(" ", "") || AppApplication.mSessionHolder.User_Mobilenumber == phoneNo.replace(" ", ""))
                {

                }else {
                    val checkdata = db!!.getCheckdataData(phoneNo.toString())
                    if (checkdata != null && checkdata.count > 0) {
                        contactListItem.isSelected = true
                        contactList.add(contactListItem)
                    } else {
                        contactListItem.isSelected = false
                        contactList.add(contactListItem)
                    }
                    checkdata.close()
                }

            }

        }
          c.close()
          val contactAdapter = UnregisterAdapter(contactList,this@SelectImportContactActivity)
          rvContacts.layoutManager = LinearLayoutManager(this@SelectImportContactActivity)
          rvContacts.adapter = contactAdapter


    }

    override fun onBackPressed() {

        val intent = Intent(this@SelectImportContactActivity, AddressBookActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()

    }
}
