package com.vlogonappv1.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v4.app.ShareCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.R
import com.vlogonappv1.contactlist.AddContactActivity
import com.vlogonappv1.contactlist.AddressBookActivity
import com.vlogonappv1.dataclass.ContactListItem
import com.vlogonappv1.db.DBHelper
import java.util.*

class RegisterExpandableListAdapter (var context: Context, var expandableListView: ExpandableListView, var itemListheader: ArrayList<ContactListItem>) : BaseExpandableListAdapter() {


    var contactList: ArrayList<ContactListItem>
    var itemsList: ArrayList<ContactListItem>

    init {
        this.contactList = itemListheader
        this.itemsList = ArrayList<ContactListItem>()
        this.itemsList.addAll(contactList)

    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChild(groupPosition: Int, childPosition: Int): ContactListItem {
        return contactList.get(groupPosition)
    }

    override fun getGroup(groupPosition: Int): ContactListItem {
        return contactList.get(groupPosition)
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }



    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertViewMain = convertView

        if (convertViewMain == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertViewMain = inflater.inflate(R.layout.activity_listitems, null)
        }

        val ivContactImage = convertViewMain?.findViewById<View>(R.id.ivContactImage) as ImageView
        val tvContactName = convertViewMain.findViewById<View>(R.id.tvContactName) as TextView
        val tvPhoneNumber = convertViewMain.findViewById<View>(R.id.tvPhoneNumber) as TextView
        val tvEmailID = convertViewMain.findViewById<View>(R.id.tvEmailID) as TextView
        val btnupdate = convertViewMain.findViewById<View>(R.id.btnupdate) as Button


        tvContactName.text = contactList[groupPosition].contactusername
        tvPhoneNumber.text = contactList[groupPosition].contactNumber
        tvEmailID.text = contactList[groupPosition].contactemail
        // holder.ivContactImage.text = contactListItem.contactName!!.substring(0,1).toUpperCase()
        val generator = ColorGenerator.MATERIAL
        val drawable = TextDrawable.builder()
            .buildRound(contactList[groupPosition].contactusername!!.substring(0,1).toUpperCase(), generator.randomColor)
        ivContactImage.setImageDrawable(drawable)


        ivContactImage.setOnClickListener {
            if (expandableListView.isGroupExpanded(groupPosition))
                expandableListView.collapseGroup(groupPosition)
            else
                expandableListView.expandGroup(groupPosition)

        }


        ivContactImage.setOnClickListener {
            if (expandableListView.isGroupExpanded(groupPosition))
                expandableListView.collapseGroup(groupPosition)
            else
                expandableListView.expandGroup(groupPosition)

        }


        btnupdate.clicks().subscribe{

            val contactListItem = contactList[groupPosition]
            val intent = Intent(context, AddContactActivity::class.java)
            intent.putExtra("addcontact","update")
            intent.putExtra("value","true")
            intent.putExtra("registerid",contactListItem.contactid.toString())
            intent.putExtra("username",contactListItem.contactusername)
            intent.putExtra("firstname",contactListItem.contactfirstname)
            intent.putExtra("lastname",contactListItem.contactlastname)
            intent.putExtra("mobilenumber",contactListItem.contactNumber)
            intent.putExtra("countrycode",contactListItem.contactcountrycode)
            intent.putExtra("birthdate",contactListItem.contactebirthdate)
            intent.putExtra("profilepic",contactListItem.contactimage)
            intent.putExtra("gender",contactListItem.contactgender)
            intent.putExtra("address",contactListItem.contactaddress)
            intent.putExtra("additionalnumner",contactListItem.contactadditionalnumber)
            intent.putExtra("tagename",contactListItem.contacttage)
            intent.putExtra("emailid",contactListItem.contactemail)
            (context as AddressBookActivity).finish()
            context.startActivity(intent)


        }

        return convertViewMain
    }


    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertViewChidview = convertView
        if (convertViewChidview == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertViewChidview = inflater.inflate(R.layout.layout_register_child, null)


        }
        val llCall = convertViewChidview?.findViewById<View>(R.id.llCall) as LinearLayout
        val llmassage = convertViewChidview.findViewById<View>(R.id.llmassage) as LinearLayout
        val llblockcall = convertViewChidview.findViewById<View>(R.id.llblockcall) as LinearLayout
        val llshareContact = convertViewChidview.findViewById<View>(R.id.llshareContact) as LinearLayout

        val llsimplecall = convertViewChidview.findViewById<View>(R.id.llsimplecall) as LinearLayout
        val llwhatsappcall = convertViewChidview.findViewById<View>(R.id.llwhatsappcall) as LinearLayout
        val llmessagnercall = convertViewChidview.findViewById<View>(R.id.llmessagnercall) as LinearLayout
        val llinstacall = convertViewChidview.findViewById<View>(R.id.llinstacall) as LinearLayout
        val llhungoutcall = convertViewChidview.findViewById<View>(R.id.llhungoutcall) as LinearLayout

        val llsimplemessage = convertViewChidview.findViewById<View>(R.id.llsimplemessage) as LinearLayout
        val llwhatsappmessage = convertViewChidview.findViewById<View>(R.id.llwhatsappmessage) as LinearLayout
        val llmessagnermessage = convertViewChidview.findViewById<View>(R.id.llmessagnermessage) as LinearLayout
        val llinstamessage = convertViewChidview.findViewById<View>(R.id.llinstamessage) as LinearLayout
        val llhungoutmessage = convertViewChidview.findViewById<View>(R.id.llhungoutmessage) as LinearLayout


        val layoutcall = convertViewChidview.findViewById<View>(R.id.layoutcall) as LinearLayout
        val layoutmessage = convertViewChidview.findViewById<View>(R.id.layoutmessage) as LinearLayout

        llCall.setOnClickListener {
            // layoutcall.visibility=View.VISIBLE
            // layoutmessage.visibility=View.GONE
        }
        llmassage.setOnClickListener {
            layoutcall.visibility= View.GONE
            layoutmessage.visibility= View.VISIBLE
        }

        llsimplecall.setOnClickListener {
            try {
                var phone=contactList[groupPosition].contactNumber
                val intent = Intent().apply {
                    action = Intent.ACTION_DIAL
                    data = Uri.parse("tel:$phone")
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        llwhatsappcall.setOnClickListener {
            val packageName = "com.whatsapp"
            val isYoutubeInstalled = isAppInstalled(packageName)
            if(isYoutubeInstalled) {

                var phone=contactList[groupPosition].contactNumber
                val intent = context.getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                context.startActivity(intent)

            }else {
                Toast.makeText(context, "Whatsapp app isn't found", Toast.LENGTH_LONG).show()
            }

        }


        llmessagnercall.setOnClickListener {
            val packageName = "com.facebook.orca"
            val isYoutubeInstalled = isAppInstalled(packageName)
            if(isYoutubeInstalled) {

                var phone=contactList[groupPosition].contactNumber
                val intent = context.getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
                context.startActivity(intent)


            }else {
                Toast.makeText(context, "Whatsapp app isn't found", Toast.LENGTH_LONG).show()
            }

        }
        llinstacall.setOnClickListener {
            val packageName = "com.instagram.android"
            val isYoutubeInstalled = isAppInstalled(packageName)
            if(isYoutubeInstalled) {

                var phone=contactList[groupPosition].contactNumber
                val intent = context.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                context.startActivity(intent)


            }else {
                Toast.makeText(context, "Whatsapp app isn't found", Toast.LENGTH_LONG).show()
            }

        }
        llhungoutcall.setOnClickListener {
            val packageName = "com.google.android.talk"
            val isYoutubeInstalled = isAppInstalled(packageName)
            if(isYoutubeInstalled) {

                var phone=contactList[groupPosition].contactNumber
                val intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.talk");
                context.startActivity(intent)


            }else {
                Toast.makeText(context, "Whatsapp app isn't found", Toast.LENGTH_LONG).show()
            }

        }

        llsimplemessage.setOnClickListener {
            try {
                var phone=contactList[groupPosition].contactNumber
                val intent = Intent().apply {
                    action = Intent.ACTION_SENDTO
                    data = Uri.parse("sms:$phone")
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


        llwhatsappmessage.setOnClickListener {
            val packageName = "com.whatsapp"
            val isYoutubeInstalled = isAppInstalled(packageName)
            if(isYoutubeInstalled) {

                var phone=contactList[groupPosition].contactNumber

                val intent = context.getPackageManager().getLaunchIntentForPackage("com.whatsapp");

                context.startActivity(intent)

            }else {
                Toast.makeText(context, "Whatsapp app isn't found", Toast.LENGTH_LONG).show()
            }

        }


        llmessagnermessage.setOnClickListener {
            val packageName = "com.facebook.orca"
            val isYoutubeInstalled = isAppInstalled(packageName)
            if(isYoutubeInstalled) {

                var phone=contactList[groupPosition].contactNumber
                val intent = context.getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
                context.startActivity(intent)


            }else {
                Toast.makeText(context, "Whatsapp app isn't found", Toast.LENGTH_LONG).show()
            }

        }
        llinstamessage.setOnClickListener {
            val packageName = "com.instagram.android"
            val isYoutubeInstalled = isAppInstalled(packageName)
            if(isYoutubeInstalled) {

                var phone=contactList[groupPosition].contactNumber
                val intent = context.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                context.startActivity(intent)


            }else {
                Toast.makeText(context, "Whatsapp app isn't found", Toast.LENGTH_LONG).show()
            }

        }
        llhungoutmessage.setOnClickListener {
            val packageName = "com.google.android.talk"
            val isYoutubeInstalled = isAppInstalled(packageName)
            if(isYoutubeInstalled) {

                var phone=contactList[groupPosition].contactNumber
                val intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.talk");
                context.startActivity(intent)


            }else {
                Toast.makeText(context, "Whatsapp app isn't found", Toast.LENGTH_LONG).show()
            }

        }

        llblockcall.setOnClickListener {
            blockdialog(contactList[groupPosition],groupPosition)

        }

        llshareContact.setOnClickListener {
            var sbShareData: StringBuffer? = null
            sbShareData = StringBuffer()
            var phone=contactList[groupPosition].contactNumber
            var username=contactList[groupPosition].contactusername

            sbShareData.append(context.resources.getString(R.string.label_name)
                    + " : " + phone)


            sbShareData.append("\n")
            sbShareData.append(context.resources.getString(R.string.label_contact_number)
                    + " : " + username)

            ShareCompat.IntentBuilder
                .from(context as Activity?)
                .setText(sbShareData.toString())
                .setType("text/plain")
                .setChooserTitle(context.resources.getString(R.string.label_share_contact_using))
                .startChooser()
        }




        return convertViewChidview
    }

    fun blockdialog(item: ContactListItem, groupPosition: Int) {



        val dialogue = AlertDialog.Builder(context)
        dialogue.setTitle("Alert!")
        dialogue.setMessage("Are You Sure To Want Block This Contact?")
        dialogue.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->


            var gg = contactList.removeAt(groupPosition)

            var db: DBHelper? = null

            db = DBHelper(context)
            val id_db = db!!.addBlockContactdata(item)
            db!!.deletecontact(item.contactid!!.toInt())
            db.closeDB()
            notifyDataSetChanged()
            //setListItems(filteredList)

        }

        dialogue.setNegativeButton("No", null)
        val showDialog: AlertDialog = dialogue.create()
        showDialog.show()



    }

    private fun isAppInstalled(packageName: String): Boolean {
        val mIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        return mIntent != null
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return contactList.size
    }



    fun filter(charText: String) {
        Log.e("itemsList", contactList.size.toString())
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        contactList.clear()
        if (charText.isEmpty()) {
            contactList.addAll(itemsList)
        } else {
            for (wp in itemsList) {
                if (wp.contactusername!!.toLowerCase(Locale.getDefault()).contains(charText) || wp.contacttage!!.toLowerCase(
                        Locale.getDefault()).contains(charText) || wp.contactaddress!!.toLowerCase(Locale.getDefault()).contains(charText)) {
                    contactList.add(wp)
                }else
                {

                }
            }
        }
        notifyDataSetChanged()
    }

}