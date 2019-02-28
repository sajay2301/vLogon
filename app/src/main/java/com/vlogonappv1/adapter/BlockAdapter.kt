package com.vlogonappv1.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.vlogonappv1.R
import com.vlogonappv1.dataclass.ContactListItem
import com.vlogonappv1.dataclass.UserRegistrationClass
import com.vlogonappv1.db.DBHelper

class BlockAdapter (private var contactList: List<ContactListItem>, private val mContext: Context) : RecyclerView.Adapter<BlockAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.blocklistitem, null)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contactListItem = contactList[position]


        holder.tvContactName.text = contactListItem.contactusername
        holder.tvPhoneNumber.text = contactListItem.contactNumber

        val generator = ColorGenerator.MATERIAL
        val drawable = TextDrawable.builder()
            .buildRound(contactListItem.contactusername!!.substring(0,1).toUpperCase(), generator.randomColor)
        holder.ivContactImage.setImageDrawable(drawable)

        holder.btnUnblock.setOnClickListener {
            unblockdialog(contactListItem,position)

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

        internal var btnUnblock: Button

        init {
            ivContactImage = itemView.findViewById<View>(R.id.ivContactImage) as ImageView
            tvContactName = itemView.findViewById<View>(R.id.tvContactName) as TextView
            tvPhoneNumber = itemView.findViewById<View>(R.id.tvPhoneNumber) as TextView
            btnUnblock = itemView.findViewById<View>(R.id.btnUnblock) as Button
            tvEmailID = itemView.findViewById<View>(R.id.tvEmailID) as TextView
        }
    }

    fun unblockdialog(item: ContactListItem, groupPosition: Int) {



        val dialogue = AlertDialog.Builder(mContext)
        dialogue.setTitle("Alert!")
        dialogue.setMessage("Are You Sure To Want Unblock This Contact?")
        dialogue.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->


            contactList = contactList.minus(element = item);

            var db: DBHelper? = null

            db = DBHelper(mContext)
            val id_db = db!!.addContactdata(item)
            db!!.deleteblockcontact(item.contactid!!.toInt())
            db.closeDB()
            notifyDataSetChanged()
            //setListItems(filteredList)

        }

        dialogue.setNegativeButton("No", null)
        val showDialog: AlertDialog = dialogue.create()
        showDialog.show()



    }
}