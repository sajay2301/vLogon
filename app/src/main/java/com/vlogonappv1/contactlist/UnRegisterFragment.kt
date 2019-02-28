package com.vlogonappv1.contactlist

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.vlogonappv1.adapter.ExpandableListAdapter
import com.vlogonappv1.dataclass.ContactListItem
import com.vlogonappv1.dataclass.ProgressDialogshow
import com.vlogonappv1.dataclass.UserRegistrationClass
import com.vlogonappv1.R
import com.vlogonappv1.db.DBHelper
import kotlinx.android.synthetic.main.unregisterlayout.*
import java.lang.Exception


class UnRegisterFragment : Fragment() {

    private var werePermissionsHandled = false
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    private var db: DBHelper? = null
    var permission: Boolean? = false
    var exist: Int = 0
    lateinit var Firestoredb: FirebaseFirestore

    private val userdataList = ArrayList<UserRegistrationClass>()
    private val filterdata = ArrayList<UserRegistrationClass>()
    internal var flag = 0

    private val mOrderBy = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 99


    lateinit var dialog: Dialog
    val mContext: Context?
        get() = context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.unregisterlayout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Firestoredb = FirebaseFirestore.getInstance()
        db = DBHelper(activity!!)
        exist = db!!.tableExists()

        getdata()


        fab.setOnClickListener { view ->
            val intent = Intent( activity!!, AddContactActivity::class.java)
            intent.putExtra("addcontact","manually")
            intent.putExtra("value","false")
            activity!!.overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)

        }
    }


    private fun getdata() {
        userdataList.clear()
        filterdata.clear()
        flag = 0
        dialog = ProgressDialogshow.progressDialog(activity!!)
        dialog.show()


        Firestoredb.collection("RegisterUser")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var contactListItem: UserRegistrationClass

                    for (document in task.result!!) {
                        //  Log.e("data", document.getId() + " => " + document.get("name"));


                        contactListItem = UserRegistrationClass()
                        contactListItem.username = document.get("UserName").toString()
                        contactListItem.mobilenumber = document.get("Mobile Number").toString()
                        contactListItem.countrycode = document.get("Countrycode").toString()
                        userdataList.add(contactListItem)

                        dialog.dismiss()
                        flag = 1

                    }
                    if (flag == 0) {
                        Toast.makeText(activity!!, "Data Not Found", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()
                    } else {
                        displayAllContacts()

                    }
                } else {
                    dialog.dismiss()
                    Log.e("dasd", "Error getting documents.", task.exception)
                }
            }


    }

    private fun displayAllContacts() {

        val contactList = ArrayList<ContactListItem>()
        var contactListItem: ContactListItem



        Log.e("size", userdataList.size.toString())
        val c = db!!.getAllData()
        if (c != null && c.count > 0) {
            while (c.moveToNext()) {

                val userid = c.getString(0)
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




                var found = false
                for (item in userdataList) {
                    if (item.countrycode + "" + item.mobilenumber == phoneNo.replace(" ", "") || item.mobilenumber == phoneNo.replace(" ", "")) {
                        found = true
                    }
                }
                if (!found) {
                    contactList.add(contactListItem)
                }

            }
        }

      /*  val contactAdapter = UnregisterAdapter(contactList, activity!!)
        rvContacts.layoutManager = LinearLayoutManager(activity!!)
        rvContacts.adapter = contactAdapter*/
        expandablelistadapter = ExpandableListAdapter(activity!!, rvContacts, contactList)
        rvContacts.setAdapter(expandablelistadapter)
        //rvContacts.setAdapter(ExpandableListAdapter(activity!!, rvContacts, contactList))

    }
    companion object {
        var expandablelistadapter:ExpandableListAdapter? = null
        fun searchfilterdata(query : String) {
            // contactAdapter?.filter(query)
            expandablelistadapter!!.filter(query)
        }

    }

}