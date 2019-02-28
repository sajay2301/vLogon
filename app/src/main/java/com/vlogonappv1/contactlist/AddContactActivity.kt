package com.vlogonappv1.contactlist

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.adapter.TageAdapter
import com.vlogonappv1.R
import com.vlogonappv1.SelectCountryActivity
import com.vlogonappv1.dataclass.*
import com.vlogonappv1.db.DBHelper
import com.vlogonappv1.preference.AppPreference
import com.vlogonappv1.preference.PrefKey
import com.vlogonappv1.spinnerdatepicker.DatePicker
import com.vlogonappv1.spinnerdatepicker.DatePickerDialog
import com.vlogonappv1.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_add_contact.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import java.io.File
import java.util.*

class AddContactActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {


    override fun onDateSet(
        view: DatePicker?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) {
        var fm = "" + (monthOfYear +1)
        var fd = "" + dayOfMonth
        if((monthOfYear+1)<10){
            fm = "0"+(monthOfYear +1)
        }
        if (dayOfMonth<10){
            fd= "0$dayOfMonth"
        }
        etbirthdate.text = "$fd/$fm/$year"
    }
    private var db: DBHelper? = null
    private var location: String = ""
    private var imagePickerHelper: ImagePickerHelper? = null
    private var adImage: File? = null
    var profilepic: String? = ""
    var gender: String? = ""
    var tagname: String? = ""
    private var fileRequest = AD_IMAGE
    lateinit var dialog: Dialog
    internal var flag = 0
    internal var checkdata = 0
    lateinit var Firestoredb: FirebaseFirestore
    var usernamevalue: String? = ""
    var contactaddvalue: String? = ""
    var Registerid: String? = ""
    companion object {
        private const val POST_IMAGE = 0
        private const val AD_IMAGE = 1

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        toolbar?.apply {

            tvToolbarTitle.text = "Add Contact"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
        val tagenamelist = arrayOf("Home", "Family", "Friends","Other")

        var spinnerAdapter1: TageAdapter = TageAdapter(this@AddContactActivity, tagenamelist)
        ettag.adapter = spinnerAdapter1

        Firestoredb = FirebaseFirestore.getInstance()

        ettag?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                Log.e("position", position.toString())
                tagname = tagenamelist[position]


            } // to close the onItemSelected


            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        btnsavecontact.clicks().subscribe {

            if(btnsavecontact.text.toString() == "Update")
            {
                updatedata()
            }else {
                if (etusername.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@AddContactActivity,
                        "Please Enter User Name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (etmobilenumber.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@AddContactActivity,
                        "Please Enter Mobile Number",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {


                    saveTask()


                }
            }
        }
        radiofontsize?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radiofemale -> gender = "Female"
                R.id.radiomale -> gender = "Male"
                R.id.radioother -> gender = "Other"
            }
        }
        txtcountrycode.clicks().subscribe {
            val intent = Intent(this, SelectCountryActivity::class.java)
            startActivityForResult(intent, 1)
        }

        imagePickerHelper = ImagePickerHelper(this)
        userImageProfile.clicks().subscribe {
            imagePickerHelper?.selectOptionToLoadImage(AD_IMAGE)
        }

        etbirthdate.clicks().subscribe{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            showDate(year, month, day, R.style.DatePickerSpinner)
        }


        val i = intent.extras
        usernamevalue = i.getString("value")
        contactaddvalue = i.getString("addcontact")

        if(usernamevalue.equals("false"))
        {

        }else  if(usernamevalue.equals("true"))
        {


            Registerid = i.getString("registerid")
            var username = i.getString("username")
            var firstname = i.getString("firstname")
            var lastname = i.getString("lastname")
            var mobilenumber = i.getString("mobilenumber")
            var countrycode = i.getString("countrycode")
            var birthdate = i.getString("birthdate")
            var profilepic = i.getString("profilepic")
            var gender = i.getString("gender")
            var address = i.getString("address")
            var additionalnumner = i.getString("additionalnumner")
            var tagename = i.getString("tagename")
            var emailid = i.getString("emailid")

            btnsavecontact.text="Update"
            btnscanqrcode.visibility=View.GONE
            btnusername.visibility=View.GONE


            etfisrtname.text = Editable.Factory.getInstance().newEditable(firstname)
            etlastname.text = Editable.Factory.getInstance().newEditable(lastname)
            etmobilenumber.text = Editable.Factory.getInstance().newEditable(mobilenumber)
            etpersonalemail.text = Editable.Factory.getInstance().newEditable(emailid)
            txtcountrycode.text = countrycode

            etbirthdate.text = birthdate
            etadditionalnumber.text = Editable.Factory.getInstance().newEditable(additionalnumner)
            etusername.text = Editable.Factory.getInstance().newEditable(username)


            profilepic = profilepic
            Glide.with(applicationContext).load(profilepic)
                .apply(
                    RequestOptions()
                        .placeholder(R.mipmap.ic_launcher_round)
                )
                .into(userImageProfile)

            when {
                gender.toString().equals("Female") -> radiofontsize.check(R.id.radiofemale)
                gender.toString().equals("Male") -> radiofontsize.check(R.id.radiomale)
                gender.toString().equals("Other") -> radiofontsize.check(R.id.radioother)

            }

            etaddress.text = Editable.Factory.getInstance().newEditable(address)


        }
        else
        {
            btnsavecontact.text="Add"
            searchuser()
        }


        btnscanqrcode.setOnClickListener { view ->
            val intent = Intent( this@AddContactActivity, ScanQrCodeActivity::class.java)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)
            finish()

        }


        btnusername.setOnClickListener { view ->
            enterUsername()

        }




    }
    @VisibleForTesting
    internal fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        SpinnerDatePickerDialogBuilder()
            .context(this@AddContactActivity)
            .callback(this)
            .spinnerTheme(spinnerTheme)
            .defaultDate(year, monthOfYear, dayOfMonth)
            .build()
            .show()
    }



    fun enterUsername() {


        val builder = AlertDialog.Builder(this@AddContactActivity)
        builder.setTitle("Enter User Name")
        val input = EditText(this@AddContactActivity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("Ok") { dialog, which ->
            val m_Text = input.text.toString()
            usernamevalue =m_Text.toString()
            btnsavecontact.text="Add"
            searchuser()


        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()

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
                                contentResolver.notifyChange(data?.data, null)
                                data?.data?.let {
                                    adImage = File(getPath(it))
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
        Glide.with(applicationContext).load(imageFile)
            .apply(
                RequestOptions()
                    .placeholder(R.mipmap.ic_launcher_round)
            )
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

    private fun saveTask() {

        class SaveTask : AsyncTask<String, Int, String>() {

            @SuppressLint("WrongThread")
            override fun doInBackground(vararg params: String): String? {

                //creating a task

                //adding to database
                db = DBHelper(applicationContext)


                val contactdata = ContactListItem()
                contactdata.contactfirstname = etfisrtname.text.toString()
                contactdata.contactlastname = etlastname.text.toString()
                contactdata.contactNumber = etmobilenumber.text.toString()
                contactdata.contactemail = etpersonalemail.text.toString()
                contactdata.contactusername = etusername.text.toString()
                contactdata.contactebirthdate = etbirthdate.text.toString()
                contactdata.contactimage =profilepic.toString()
                contactdata.contactgender = gender.toString()
                contactdata.contactaddress = etaddress.text.toString()
                contactdata.contactcountrycode = txtcountrycode.text.toString()
                contactdata.contactadditionalnumber = etadditionalnumber.text.toString()
                contactdata.contacttage = tagname



                val id_db = db!!.addContactdata(contactdata)


                return "string"
            }


            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                Log.e("Response", "" + response)


                db!!.closeDB()

                finish();
                Toast.makeText(applicationContext, "Successfully Saved", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }


    private fun updatedata() {

        class SaveTask : AsyncTask<String, Int, String>() {

            @SuppressLint("WrongThread")
            override fun doInBackground(vararg params: String): String? {

                //creating a task

                //adding to database
                db = DBHelper(applicationContext)


                val contactdata = UserRegistrationClass()
                contactdata.firstname = etfisrtname.text.toString()
                contactdata.lastname = etlastname.text.toString()
                contactdata.mobilenumber = etmobilenumber.text.toString()
                contactdata.personalemail = etpersonalemail.text.toString()
                contactdata.username = etusername.text.toString()
                contactdata.birthdate = etbirthdate.text.toString()
                contactdata.profilepic =profilepic.toString()
                contactdata.gender = gender.toString()
                contactdata.address = etaddress.text.toString()
                contactdata.countrycode = txtcountrycode.text.toString()
                contactdata.additionalnumber = etadditionalnumber.text.toString()
                contactdata.tagename = tagname
                contactdata.registerid = Registerid!!.toInt()

                val id_db = db!!.updateregisterContactdata(contactdata)


                return "string"
            }


            override fun onPostExecute(response: String) {
                super.onPostExecute(response)
                Log.e("Response", "" + response)


                db!!.closeDB()

                finish();
                Toast.makeText(applicationContext, "Successfully Update", Toast.LENGTH_LONG).show()
            }
        }

        val st = SaveTask()
        st.execute()
    }

    private fun searchuser() {

        flag=0
        dialog = ProgressDialogshow.progressDialog(this@AddContactActivity)
        dialog.show()
        Firestoredb.collection("RegisterUser").whereEqualTo("UserName", usernamevalue)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        //  Log.e("data", document.getId() + " => " + document.get("name"));
                        flag = 1
                        dialog.dismiss()
                        etfisrtname.text = Editable.Factory.getInstance().newEditable(document.get("Firstname").toString())
                        etlastname.text = Editable.Factory.getInstance().newEditable(document.get("Lastname").toString())
                        etmobilenumber.text = Editable.Factory.getInstance().newEditable(document.get("Mobile Number").toString())
                        etpersonalemail.text = Editable.Factory.getInstance().newEditable(document.get("Primary Email").toString())
                        txtcountrycode.text = document.get("Countrycode").toString()

                        etbirthdate.text = document.get("BirthDate").toString()
                        etadditionalnumber.text = Editable.Factory.getInstance().newEditable(document.get("AdditionalNumber").toString())
                        etusername.text = Editable.Factory.getInstance().newEditable(document.get("UserName").toString())


                        profilepic = document.get("ProfilePic").toString()
                        Glide.with(applicationContext).load(profilepic)
                            .apply(
                                RequestOptions()
                                    .placeholder(R.mipmap.ic_launcher_round)
                            )
                            .into(userImageProfile)

                        when {
                            document.get("Gender").toString().equals("Female") -> radiofontsize.check(R.id.radiofemale)
                            document.get("Gender").toString().equals("Male") -> radiofontsize.check(R.id.radiomale)
                            document.get("Gender").toString().equals("Other") -> radiofontsize.check(R.id.radioother)

                        }

                        etaddress.text = Editable.Factory.getInstance().newEditable(document.get("Address").toString())




                    }
                    if (flag == 0) {
                        Toast.makeText(this@AddContactActivity, "Username Not Found", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()


                    } else {

                        dialog.dismiss()
                    }
                } else {
                    dialog.dismiss()
                    Log.e("dasd", "Error getting documents.", task.exception)
                }
            }


    }
    override fun onBackPressed() {

        finish()


    }
}
