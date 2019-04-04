package com.vlogonappv1.contactlist

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.vlogonappv1.R
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

import android.app.Dialog
import android.support.v4.content.ContextCompat
import com.vlogonappv1.db.DBHelper
import android.app.LoaderManager
import android.content.Loader
import android.database.Cursor
import android.provider.ContactsContract
import android.content.CursorLoader
import android.content.Intent
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.adapter.MyPagerAdapter
import com.vlogonappv1.dataclass.ContactListItem
import com.vlogonappv1.dataclass.ProgressDialogshow
import kotlinx.android.synthetic.main.activity_address_book.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import android.widget.Toast
import android.app.SearchManager
import android.content.Context
import android.support.v7.widget.SearchView
import android.util.Log


class AddressBookActivity : AppCompatActivity(),LoaderManager.LoaderCallbacks<Cursor> {
    private var werePermissionsHandled = false
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    private var db: DBHelper? = null
    var permission: Boolean? = false
    var exist: Int = 0
    private val mOrderBy = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 99
    lateinit var dialog: Dialog

    private lateinit var registerfragment : RegisterFragment
    private lateinit var unregisterfragment : UnRegisterFragment


    companion object {
        var fa: Activity? = null

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_book)
        fa = this;
        toolbar?.apply {

            tvToolbarTitle.text = "Address Book"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }

        db = DBHelper(applicationContext)



        exist = db!!.tableExists()

        if (android.os.Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.M) {
            permission = checkContactsPermission()
            if(this.permission!!){
                if(exist==0)
                {

                    loaderManager.initLoader(1, Bundle(), this)

                }
                else
                {
                    displayAllContacts()
                }
            }
        }
        else {
            if(exist == 0) {

                loaderManager.initLoader(1, Bundle(), this)

            }
            else
            {
                displayAllContacts()
            }
        }
        registerfragment = RegisterFragment()


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.contact_main, menu)

        val searchViewItem = menu.findItem(R.id.action_search)
        val searchView = searchViewItem.actionView as SearchView
        searchView.queryHint = "Search for Name,Location,Tage"


        val queryTextListener = object : SearchView.OnQueryTextListener {
           override fun onQueryTextChange(newText: String): Boolean {
                // This is your adapter that will be filtered

               performSearch(newText)

                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)

                return true
            }
        }
        searchView.setOnQueryTextListener(queryTextListener)
        return true
    }
    fun performSearch(query: String) {
        if (viewpager.currentItem == 0) {
            RegisterFragment.searchfilterdata(query)
        } else if (viewpager.currentItem == 1) {
            UnRegisterFragment.searchfilterdata(query)
        }

        //  EditSearch.setText("")
        //EditSearch.visibility = GONE

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        when (id) {

            R.id.action_block_contact -> {
                val intent = Intent(this@AddressBookActivity, BlockContactActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                finish()
                //   passwordDialog("backup_Drive")
                /* isBackup = true
                 val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                 startActivityForResult(signInIntent, RC_SIGN_IN)*/
            }

            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayAllContacts() {

        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        viewpager.adapter = fragmentAdapter

        tabs_main.setupWithViewPager(viewpager)
      //  val contactAdapter = RegisterAdapter(contactList, applicationContext)
        //rvContacts.layoutManager = LinearLayoutManager(this)
       // rvContacts.adapter = contactAdapter
    }

    override fun onCreateLoader(i: Int, bundle: Bundle): Loader<Cursor>? {
        dialog = ProgressDialogshow.progressDialog(this@AddressBookActivity)
        dialog.show()
        return if (i == 1) {
            CursorLoader(this,
                ContactsContract.Contacts.CONTENT_URI, null, null, null,
                "upper(" +
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +
                        ") ASC")
        } else null
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        if (cursor !=
            null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))
                if (hasPhoneNumber > 0) {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                    val contentResolver = contentResolver
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id), null)

                    var phoneNumber :String = ""
                    if (phoneCursor!!.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        phoneCursor.close()
                        db = DBHelper(applicationContext)
                        val contactdata = ContactListItem()
                        contactdata.contactfirstname = ""
                        contactdata.contactlastname = ""
                        contactdata.contactNumber = phoneNumber
                        contactdata.contactemail = ""
                        contactdata.contactusername = name
                        contactdata.contactebirthdate = ""
                        contactdata.contactimage =""
                        contactdata.contactgender = ""
                        contactdata.contactaddress = ""
                        contactdata.contactcountrycode = ""
                        contactdata.contactadditionalnumber =""
                        contactdata.contacttage = ""
                        contactdata.status = ""
                        val id_db =  db!!.addContactdata(contactdata)

                        db!!.closeDB()
                    }

                }
            }
            dialog.dismiss()
            displayAllContacts()
        }
    }


    override fun onLoaderReset(loader: Loader<Cursor>) {}

    private fun checkContactsPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_CONTACTS),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_CONTACTS),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)
            }
            return false
        } else {
            return true
        }
    }

}
