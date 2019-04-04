package com.vlogonappv1.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.vlogonappv1.dataclass.ContactListItem
import com.vlogonappv1.R
import android.widget.CheckBox
import java.util.Collections.replaceAll
import android.widget.CompoundButton
import java.util.ArrayList





class UnregisterAdapter (private val contactList: List<ContactListItem>, private val mContext: Context) : RecyclerView.Adapter<UnregisterAdapter.ContactViewHolder>() {



    var Selected_contacts = ArrayList<String>()

    companion object {
        val itemsList = ArrayList<ContactListItem>()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.checklistselected, null)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contactListItem = contactList[position]
        holder.tvContactName.text = contactListItem.contactusername
        holder.tvPhoneNumber.text = contactListItem.contactNumber
        holder.tvEmailID.text = contactListItem.contactemail
        // holder.ivContactImage.text = contactListItem.contactName!!.substring(0,1).toUpperCase()
        val generator = ColorGenerator.MATERIAL
        val drawable = TextDrawable.builder()
            .buildRound(contactListItem.contactusername!!.substring(0,1).toUpperCase(), generator.randomColor)
        holder.ivContactImage.setImageDrawable(drawable)

        holder.check_icon.setOnCheckedChangeListener(null)
        holder.check_icon.isChecked = contactList[position].isSelected


        holder.check_icon.setOnCheckedChangeListener { compoundButton, b ->
            val contact = contactList[position]
            if (holder.check_icon.isChecked) {
                itemsList!!.add(contact)
                Selected_contacts.add(contact.contactusername.toString());
                contactList[holder.adapterPosition].isSelected=b
            } else {

                if (Selected_contacts.contains(contactList.get(position).contactusername)) {
                    itemsList!!.remove(contact)
                    Selected_contacts.remove(contact.contactusername.toString());
                    contactList[holder.adapterPosition].isSelected=b
                }


            }
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
        internal var check_icon: CheckBox
        init {
            ivContactImage = itemView.findViewById<View>(R.id.ivContactImage) as ImageView
            tvContactName = itemView.findViewById<View>(R.id.tvContactName) as TextView
            tvPhoneNumber = itemView.findViewById<View>(R.id.tvPhoneNumber) as TextView
            tvEmailID = itemView.findViewById<View>(R.id.tvEmailID) as TextView
            check_icon = itemView.findViewById<View>(R.id.check_icon) as CheckBox
        }
    }


}