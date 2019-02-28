package com.vlogonappv1.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.amulyakhare.textdrawable.TextDrawable
import com.vlogonappv1.dataclass.ContactListItem
import com.vlogonappv1.R
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.SelectCountryActivity
import com.vlogonappv1.contactlist.AddContactActivity
import com.vlogonappv1.dataclass.UserRegistrationClass
import java.util.*


class RegisterAdapter(var list: ArrayList<ContactListItem>,
                      private val userdatalist: ArrayList<UserRegistrationClass>,
                      private val mContext: Context) : RecyclerView.Adapter<RegisterAdapter.ContactViewHolder>(),
    Filterable  {
    var contactList: ArrayList<ContactListItem>
    var itemsList: ArrayList<ContactListItem>

    init {
        this.contactList = list
        this.itemsList = ArrayList<ContactListItem>()
        this.itemsList.addAll(contactList)

    }
    companion object {
        private val POST_IMAGE = 0
        private const val AD_IMAGE = 1

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.activity_listitems, null)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contactListItem = contactList[position]
      //  val userdatalistItem = userdatalist[position]



            holder.tvContactName.text = contactListItem.contactusername
            holder.tvPhoneNumber.text = contactListItem.contactNumber

            val generator = ColorGenerator.MATERIAL
            val drawable = TextDrawable.builder()
                .buildRound(contactListItem.contactusername!!.substring(0,1).toUpperCase(), generator.randomColor)
            holder.ivContactImage.setImageDrawable(drawable)

            holder.btnupdate.clicks().subscribe{

                val intent = Intent(mContext, AddContactActivity::class.java)
                intent.putExtra("addcontact","update")
                intent.putExtra("value","true")

                intent.putExtra("registerid",contactListItem.contactid)
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




                mContext.startActivity(intent)


            }


    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var ivContactImage: ImageView
        internal var tvContactName: TextView
        internal var tvPhoneNumber: TextView
        internal var tvEmailID: TextView
        internal var btnupdate: Button

        init {
            ivContactImage = itemView.findViewById<View>(R.id.ivContactImage) as ImageView
            tvContactName = itemView.findViewById<View>(R.id.tvContactName) as TextView
            tvPhoneNumber = itemView.findViewById<View>(R.id.tvPhoneNumber) as TextView
            tvEmailID = itemView.findViewById<View>(R.id.tvEmailID) as TextView
            btnupdate = itemView.findViewById<View>(R.id.btnupdate) as Button
        }


    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                Log.e("itemsList", charString)
                if (charString.isEmpty()) {
                    contactList.addAll(itemsList)
                } else {
                    val filteredList = ArrayList<ContactListItem>()
                    for (wp in itemsList) {
                        if (wp.contactusername!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(wp)
                        }
                    }
                    contactList = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = contactList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                contactList = filterResults.values as ArrayList<ContactListItem>

                notifyDataSetChanged()
            }
        }
    }

   /* override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    try {
                        //get data from the web
                        contactList = getCountrydatafilter(constraint.toString())

                    } catch (e: Exception) {
                        Log.d("HUS", "EXCEPTION $e")
                    }

                    filterResults.values = contactList
                    filterResults.count = contactList.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    //notifyDataSetInvalidated()
                }
            }
        }
    }*/

    fun getCountrydatafilter(charText: String): ArrayList<ContactListItem> {
        Log.e("itemsList", itemsList.size.toString())
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        contactList.clear()
        if (charText.isEmpty()) {
            contactList.addAll(itemsList)
        } else {
            for (wp in itemsList) {
                if (wp.contactusername!!.toLowerCase(Locale.getDefault()).contains(charText)
                ) {
                    contactList.add(wp)
                }
            }
        }
        notifyDataSetChanged()
        return contactList
    }

    fun filter(charText: String) {
        Log.e("itemsList", itemsList.size.toString())
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        contactList.clear()
        if (charText.isEmpty()) {
            contactList.addAll(itemsList)
        } else {
            for (wp in itemsList) {
                if (wp.contactusername!!.toLowerCase(Locale.getDefault()).contains(charText) || wp.contacttage!!.toLowerCase(Locale.getDefault()).contains(charText) || wp.contactaddress!!.toLowerCase(Locale.getDefault()).contains(charText)) {

                    contactList.add(wp)
                }else
                {

                }
            }
        }
        notifyDataSetChanged()
    }
}