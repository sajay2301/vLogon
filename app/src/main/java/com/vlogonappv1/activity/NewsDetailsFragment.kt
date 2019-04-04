package com.vlogonappv1.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.app.Fragment
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.R
import com.vlogonappv1.adapter.TageAdapter
import com.vlogonappv1.dataclass.ContactListItem
import com.vlogonappv1.dataclass.ImagePickerHelper
import com.vlogonappv1.dataclass.getPath
import com.vlogonappv1.db.DBHelper
import com.vlogonappv1.spinnerdatepicker.DatePicker
import com.vlogonappv1.spinnerdatepicker.DatePickerDialog
import com.vlogonappv1.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_news_details.*
import java.io.File
import java.util.*
import com.vlogonappv1.dataclass.*

class NewsDetailsFragment : Fragment() ,DatePickerDialog.OnDateSetListener{



    override fun onDateSet(
        view: DatePicker?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) {
        var fm = "" + (monthOfYear + 1)
        var fd = "" + dayOfMonth
        if ((monthOfYear + 1) < 10) {
            fm = "0" + (monthOfYear + 1)
        }
        if (dayOfMonth < 10) {
            fd = "0$dayOfMonth"
        }
        etbirthdate.text = "$fd/$fm/$year"
    }

    val itemList = ArrayList<ContactListItem?>()

    private var postId = 0
    private var categoryName = ""
    private var contactnumber = ""
    lateinit var dialog: Dialog

    private var db: DBHelper? = null

    var exist: Int = 0
    lateinit var Firestoredb: FirebaseFirestore
    private var profilepic = ""
    private var gendername = ""
    private var tagname = ""
    var Registerid: Int= 0
    internal var flag = 0
    private var imagePickerHelper: ImagePickerHelper? = null
    private var adImage: File? = null
    private var fileRequest = AD_IMAGE

    companion object {
        const val POST_ID = "postId"
        const val ContactNumber = "contactnumber"
        private const val POST_IMAGE = 0
        private const val AD_IMAGE = 1
        fun newInstance(postId: Int, contactnumber: String): NewsDetailsFragment {
            val newsDetailsFragment = NewsDetailsFragment()
            val bundle = Bundle()
            bundle.putInt(POST_ID, postId)
            bundle.putString(ContactNumber, contactnumber)

            newsDetailsFragment.arguments = bundle
            return newsDetailsFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            postId = it.getInt(POST_ID)
            contactnumber = it.getString(ContactNumber)
            setupUi()

        }

    }
    private fun setupUi()
    {


        btnsavecontact.clicks().subscribe {

            if (etusername.text.toString().isEmpty()) {
                    Toast.makeText(
                        activity!!,
                        "Please Enter User Name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (etmobilenumber.text.toString().isEmpty()) {
                    Toast.makeText(
                        activity!!,
                        "Please Enter Mobile Number",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {


                    updateunregisterdata()


                }

        }

        val tagenamelist = arrayOf("Home", "Family", "Friends", "Other")

        var spinnerAdapter1: TageAdapter = TageAdapter(activity!!, tagenamelist)
        ettag.adapter = spinnerAdapter1

        Firestoredb = FirebaseFirestore.getInstance()

        ettag?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                tagname = tagenamelist[position]


            } // to close the onItemSelected


            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        txtcountrycode.clicks().subscribe {
            val intent = Intent(activity, SelectCountryActivity::class.java)
            startActivityForResult(intent, 1)
        }

        imagePickerHelper = ImagePickerHelper(activity!!)
        userImageProfile.clicks().subscribe {
            imagePickerHelper?.selectOptionToLoadImage(AD_IMAGE)
        }
        btnscanqrcode.visibility = View.VISIBLE
        btnusername.visibility = View.VISIBLE
        etbirthdate.clicks().subscribe {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            showDate(year, month, day, R.style.DatePickerSpinner)
        }

        btnscanqrcode.setOnClickListener { view ->
            val market_uri = "sent from 'Vlogon' Install it from  https://drive.google.com/file/d/1Y6CftjjDeDoKItJi-vkv47sUOC4twa5q/view?usp=sharing"
            val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, market_uri)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))

        }


        btnusername.setOnClickListener { view ->
            // enterUsername()

        }

        db = DBHelper(activity!!)
        val c = db!!.getCheckdataData(contactnumber)
        if (c != null && c.count > 0)
        {
            while (c.moveToNext()) {
                Registerid = c.getInt(0)
                val username = c.getString(1)
                val phoneNo = c.getString(2)
                val emailid = c.getString(3)
                val firstname = c.getString(4)
                val lastname = c.getString(5)
                val birthdate = c.getString(6)
                val profilepic = c.getString(7)
                gendername = c.getString(8)
                val address = c.getString(9)
                var countrycode = c.getString(10)
                val additionalnumber = c.getString(11)
                val contact_tage = c.getString(12)
                val contact_status = c.getString(13)
                var mobilenumber=phoneNo.replace("-", "")
                var mobilenumber1=mobilenumber.replace(" ", "")

                if(mobilenumber1.length > 10)
                {

                    countrycode=mobilenumber1.substring(0,3)
                    mobilenumber1=mobilenumber1.substring(3,mobilenumber1.length).trim()

                }


                etfisrtname.text = Editable.Factory.getInstance().newEditable(firstname)
                etlastname.text = Editable.Factory.getInstance().newEditable(lastname)
                etmobilenumber.text = Editable.Factory.getInstance().newEditable(mobilenumber1.replace("-", ""))
                etpersonalemail.text = Editable.Factory.getInstance().newEditable(emailid)
                txtcountrycode.text = countrycode
                if (countrycode.equals("")) {
                    txtcountrycode.text = "+00"
                }
                etbirthdate.text = birthdate
                etadditionalnumber.text = Editable.Factory.getInstance().newEditable(additionalnumber)
                etusername.text = Editable.Factory.getInstance().newEditable(username)



                Glide.with(activity!!).load(profilepic)
                    .apply(RequestOptions().placeholder(R.mipmap.ic_launcher_round))
                    .into(userImageProfile)

                when {
                    gendername.toString().equals("Female") -> radiofontsize.check(R.id.radiofemale)
                    gendername.toString().equals("Male") -> radiofontsize.check(R.id.radiomale)
                    gendername.toString().equals("Other") -> radiofontsize.check(R.id.radioother)

                }

                etaddress.text = Editable.Factory.getInstance().newEditable(address)
            }
        }else
        {

        }
        db!!.closeDB()
    }
    private fun updateunregisterdata() {

        class SaveTask : AsyncTask<String, Int, String>() {

            @SuppressLint("WrongThread")
            override fun doInBackground(vararg params: String): String? {

                //creating a task

                //adding to database
                db = DBHelper(activity!!)


                val contactdata = ContactListItem()
                contactdata.contactfirstname = etfisrtname.text.toString()
                contactdata.contactlastname = etlastname.text.toString()
                contactdata.contactNumber = etmobilenumber.text.toString()
                contactdata.contactemail = etpersonalemail.text.toString()
                contactdata.contactusername = etusername.text.toString()
                contactdata.contactebirthdate = etbirthdate.text.toString()
                contactdata.contactimage = profilepic.toString()
                contactdata.contactgender = gendername.toString()
                contactdata.contactaddress = etaddress.text.toString()
                contactdata.contactcountrycode = txtcountrycode.text.toString()
                contactdata.contactadditionalnumber = etadditionalnumber.text.toString()
                contactdata.contacttage = tagname
                contactdata.contactid = Registerid!!.toInt()
                contactdata.status = "Add"

                val id_db = db!!.updateDublicateContactdata(contactdata)


                return "string"
            }


            override fun onPostExecute(response: String) {
                super.onPostExecute(response)

                db!!.closeDB()
                Toast.makeText(activity, "Successfully Update", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {


            Activity.RESULT_OK -> {
                when (requestCode) {
                    ImagePickerHelper.REQUEST_CAMERA -> {
                        when (imagePickerHelper?.mFile?.fileRequest) {

                            AD_IMAGE -> {
                                adImage = imagePickerHelper?.mFile?.mFile
                                loadImage(adImage, AD_IMAGE, Uri.fromFile(adImage))
                            }
                        }
                    }
                    ImagePickerHelper.SELECT_FILE -> {
                        when (imagePickerHelper?.fileRequest) {
                            AD_IMAGE -> {
                                activity!!.contentResolver.notifyChange(data?.data, null)
                                data?.data?.let {
                                    adImage = File(activity!!.getPath(it))
                                    loadImage(adImage, AD_IMAGE, it)
                                }
                            }
                        }
                    }
                }
            }


        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val countryCode = data!!.getStringExtra(SelectCountryActivity.RESULT_CONTRYCODE)
            //Toast.makeText(this, "You selected countrycode: $countryCode", Toast.LENGTH_LONG).show()
            txtcountrycode.text = countryCode
        }
    }

    private fun loadImage(imageFile: File?, request: Int, uri: Uri) {

        profilepic = ""
        profilepic = imageFile.toString()
        Glide.with(activity!!).load(imageFile).apply(RequestOptions().placeholder(R.mipmap.ic_launcher_round))
            .into(userImageProfile)


    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ImagePickerHelper.PERMISSIONS_PICK_IMAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePickerHelper?.actionPickImage(fileRequest)
            }
        } else if (requestCode == ImagePickerHelper.PERMISSIONS_TAKE_IMAGE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                imagePickerHelper?.actionTakeImage(fileRequest)
            }
        }
    }

    @VisibleForTesting
    internal fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        SpinnerDatePickerDialogBuilder()
            .context(activity)
            .callback(this)
            .spinnerTheme(spinnerTheme)
            .defaultDate(year, monthOfYear, dayOfMonth)
            .build()
            .show()
    }

}