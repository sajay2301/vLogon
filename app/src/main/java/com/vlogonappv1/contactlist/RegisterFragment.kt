package com.vlogonappv1.contactlist

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.vlogonappv1.AppApplication
import com.vlogonappv1.adapter.RegisterAdapter
import com.vlogonappv1.dataclass.ContactListItem
import com.vlogonappv1.dataclass.ProgressDialogshow
import com.vlogonappv1.dataclass.UserRegistrationClass
import com.vlogonappv1.R
import com.vlogonappv1.activity.CommonAddActivity
import com.vlogonappv1.adapter.ExpandableListAdapter
import com.vlogonappv1.adapter.RegisterExpandableListAdapter
import com.vlogonappv1.db.DBHelper
import kotlinx.android.synthetic.main.activity_select_country.*
import kotlinx.android.synthetic.main.registerlayout.*
import java.lang.Exception
import java.util.*



class RegisterFragment : Fragment() {

    private var werePermissionsHandled = false
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    private var db: DBHelper? = null
    var permission: Boolean? = false
    var exist: Int = 0
    private val mOrderBy = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 99
    lateinit var Firestoredb: FirebaseFirestore

    lateinit var dialog: Dialog
    internal var flag = 0


    private var registerfab: FloatingActionButton? = null

    private val userdataList = ArrayList<UserRegistrationClass>()
    private val filterdata = ArrayList<UserRegistrationClass>()
    val registercontactList = ArrayList<ContactListItem>()
    private var rootview: View? = null
    val mContext: Context?
        get() = context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootview=inflater.inflate(R.layout.registerlayout, container, false)

        Firestoredb = FirebaseFirestore.getInstance()
        db = DBHelper(activity!!)
        exist = db!!.tableExists()
        registerfab = rootview?.findViewById(R.id.registerfab)

        registerfab!!.setOnClickListener { view ->

            val intent = Intent( activity!!, CommonAddActivity::class.java)
            activity!!.overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)

        }

        getdata()


        return rootview
    }


    private fun getdata() {
        flag = 0
        dialog = ProgressDialogshow.progressDialog(activity!!)
        dialog.show()
        userdataList.clear()
        filterdata.clear()
        Firestoredb.collection("RegisterUser")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var contactListItem: UserRegistrationClass

                    for (document in task.result!!) {

                        contactListItem = UserRegistrationClass()
                        contactListItem.username= document.get("UserName").toString()
                        contactListItem.mobilenumber=document.get("Mobile Number").toString()
                        contactListItem.gender= document.get("Gender").toString()
                        contactListItem.profilepic=document.get("ProfilePic").toString()
                        contactListItem.address=document.get("Address").toString()
                        contactListItem.additionalnumber= document.get("AdditionalNumber").toString()
                        contactListItem.birthdate=document.get("BirthDate").toString()
                        contactListItem.countrycode=document.get("Countrycode").toString()
                        contactListItem.firstname= document.get("Firstname").toString()
                        contactListItem.lastname=document.get("Lastname").toString()
                        contactListItem.personalemail=document.get("Primary Email").toString()
                        contactListItem.tagename=""
                        userdataList.add(contactListItem)


                        flag=1

                    }
                    if (flag == 0) {
                        Toast.makeText(activity!!, "Data Not Found", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()
                    } else {
                        dialog.dismiss()
                        displayAllContacts()

                    }
                } else {
                    dialog.dismiss()
                }
            }




    }

    private fun displayAllContacts() {
        filterdata.clear()
        registercontactList.clear()
        val contactList = ArrayList<ContactListItem>()
        var contactListItem: ContactListItem


        for(item in userdataList) {


                val c = db!!.getAllData()
                if (c != null && c.count > 0) {
                while (c.moveToNext()) {
                    val name = c.getString(1)
                    val phoneNo = c.getString(2)
                    val emailid = c.getString(3)
                    contactListItem = ContactListItem()
                    contactListItem.contactusername = name
                    contactListItem.contactNumber = phoneNo
                    contactListItem.contactemail = emailid
                    contactList.add(contactListItem)

                    val matchphone=phoneNo.replace(" ","")

                    if(item.countrycode+""+item.mobilenumber.toString() == matchphone || item.mobilenumber== matchphone)
                    {


                        var db: DBHelper? = null

                        db = DBHelper(activity!!)

                        val checkdata = db.getRegisterData(item.countrycode+""+item.mobilenumber, item.mobilenumber!!)
                        if (checkdata != null && checkdata.count > 0)
                        {

                        }else
                        {

                            val id_db = db.addregisterContactdata(item)

                        }


                        db.closeDB()

                        //filterdata.add(item)
                    }


                }
            }
        }



        var registercontactListItem: ContactListItem

        val getRegisterData = db!!.allgetRegisterData()
        if (getRegisterData != null && getRegisterData.count > 0) {
            while (getRegisterData.moveToNext()) {
                val userid = getRegisterData.getInt(0)
                val username = getRegisterData.getString(1)
                val phoneNo = getRegisterData.getString(2)
                val emailid = getRegisterData.getString(3)
                val firstname = getRegisterData.getString(4)
                val lastname = getRegisterData.getString(5)
                val birthdate = getRegisterData.getString(6)
                val profilepic = getRegisterData.getString(7)
                val gender = getRegisterData.getString(8)
                val address = getRegisterData.getString(9)
                val countrycode = getRegisterData.getString(10)
                val additionalnumber = getRegisterData.getString(11)
                val contact_tage = getRegisterData.getString(12)


                val checkdata = db!!.getBlockContactData(phoneNo)
                if (checkdata != null && checkdata.count > 0)
                {

                }else
                {
                    if(phoneNo == AppApplication.mSessionHolder.User_Mobilenumber)
                    {
                    }else {
                        registercontactListItem = ContactListItem()
                        registercontactListItem.contactid = userid
                        registercontactListItem.contactNumber = phoneNo
                        registercontactListItem.contactemail = emailid
                        registercontactListItem.contactusername = username
                        registercontactListItem.contactfirstname = firstname
                        registercontactListItem.contactlastname = lastname
                        registercontactListItem.contactebirthdate = birthdate
                        registercontactListItem.contactimage = profilepic
                        registercontactListItem.contactgender = gender
                        registercontactListItem.contactaddress = address
                        registercontactListItem.contactcountrycode = countrycode
                        registercontactListItem.contactadditionalnumber = additionalnumber
                        registercontactListItem.contacttage = contact_tage
                        registercontactList.add(registercontactListItem)
                    }
                }




            }
        }
       /* contactAdapter = RegisterAdapter(registercontactList,filterdata,mContext!!)
        RegisterRecyclerView!!.layoutManager = LinearLayoutManager(mContext!!)
        RegisterRecyclerView!!.adapter = contactAdapter*/

        registerexpandablelistadapter = RegisterExpandableListAdapter(activity!!, rvContacts, registercontactList)
        rvContacts.setAdapter(registerexpandablelistadapter)
    }


    companion object {
        var registerexpandablelistadapter: RegisterExpandableListAdapter? = null
        fun searchfilterdata(query : String) {
            // contactAdapter?.filter(query)
            registerexpandablelistadapter!!.filter(query)
        }

    }

}