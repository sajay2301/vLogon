package com.vlogon.Adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.vlogon.R
import com.vlogon.SelectCountryActivity
import java.util.*

class CountryListArrayAdapter(private val context: Activity, private val list: ArrayList<SelectCountryActivity.Country>) :
    ArrayAdapter<SelectCountryActivity.Country>(context, R.layout.countrylist_view_row, list), Filterable {


    var countrydata: ArrayList<SelectCountryActivity.Country>
    internal val TAG = "AutocompleteCustomArrayAdapter.java"

    var countryItemClassarraylist: ArrayList<SelectCountryActivity.Country>

    init {
        this.countrydata = list

        this.countryItemClassarraylist = ArrayList<SelectCountryActivity.Country>()
        this.countryItemClassarraylist.addAll(countrydata)
    }


    internal class ViewHolder {
        var countryname: TextView? = null
        var countrycode: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = null

        if (convertView == null) {
            val inflator = context.layoutInflater
            view = inflator.inflate(R.layout.countrylist_view_row, null)
            val viewHolder = ViewHolder()
            viewHolder.countryname = view!!.findViewById<View>(R.id.countryname) as TextView
            viewHolder.countrycode = view!!.findViewById<View>(R.id.countrycode) as TextView

            view.tag = viewHolder
        } else {
            view = convertView
        }

        val holder = view.tag as ViewHolder
        holder.countryname!!.text = list[position].name
        holder.countrycode!!.text = list[position].code
        return view
    }


    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    try {
                        //get data from the web
                        countrydata = getCountrydatafilter(constraint.toString())

                    } catch (e: Exception) {
                        Log.d("HUS", "EXCEPTION $e")
                    }

                    filterResults.values = countrydata
                    filterResults.count = countrydata.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    fun getCountrydatafilter(charText: String): ArrayList<SelectCountryActivity.Country> {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        countrydata.clear()
        if (charText.length == 0) {
            countrydata.addAll(countryItemClassarraylist)
        } else {
            for (wp in countryItemClassarraylist) {
                if (wp.name.toLowerCase(Locale.getDefault())
                        .contains(charText)
                ) {
                    countrydata.add(wp)
                }
            }
        }
        notifyDataSetChanged()
        return countrydata
    }

    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        countrydata.clear()
        if (charText.length == 0) {
            countrydata.addAll(countryItemClassarraylist)
        } else {
            for (wp in countryItemClassarraylist) {
                if (wp.name.toLowerCase(Locale.getDefault())
                        .contains(charText)
                ) {
                    countrydata.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }
}