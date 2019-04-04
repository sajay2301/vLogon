package com.vlogonappv1.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*


import java.util.ArrayList
import java.util.Locale
import android.content.res.TypedArray

import android.widget.AdapterView
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.R
import com.vlogonappv1.adapter.CountryListArrayAdapter
import kotlinx.android.synthetic.main.activity_select_country.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*


class SelectCountryActivity : AppCompatActivity() {

     var toolbar_title: TextView? = null
     var closebutton: ImageView? = null
    var countrynames: Array<String>? = null
    var countrycodes:Array<String>? = null
    private val imgs: TypedArray? = null
    lateinit var countryList: ArrayList<Country>



    companion object {
        var RESULT_CONTRYCODE = "countrycode"
        var RESULT_CONTRYNAME= "countryname"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_country)


        toolbar?.apply {

            tvToolbarTitle.text = "Select Country"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }


        populateCountryList()
        val adapter = CountryListArrayAdapter(this, countryList)
        list_view.adapter=adapter

        list_view.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val c = countryList!!.get(position)
            val returnIntent = Intent()
            returnIntent.putExtra(RESULT_CONTRYCODE, c.code)
            returnIntent.putExtra(RESULT_CONTRYNAME, c.name)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        inputSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(arg0: Editable) {
                // TODO Auto-generated method stub
                val text = inputSearch.text.toString().toLowerCase(Locale.getDefault())
                adapter.filter(text)
            }

            override fun beforeTextChanged(
                arg0: CharSequence, arg1: Int,
                arg2: Int, arg3: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(
                arg0: CharSequence, arg1: Int, arg2: Int,
                arg3: Int
            ) {
                // TODO Auto-generated method stub
            }
        })
    }



    private fun populateCountryList() {
        countryList = ArrayList<Country>()
        countrynames = resources.getStringArray(R.array.country_names)
        countrycodes = resources.getStringArray(R.array.country_codes)

        for (i in 0 until (countrycodes as Array<String>).size) {
            (countryList as ArrayList<Country>).add(Country((countrynames as Array<String>)[i], (countrycodes as Array<String>)[i]))
        }
    }

    inner class Country(val name: String, val code: String)


}
