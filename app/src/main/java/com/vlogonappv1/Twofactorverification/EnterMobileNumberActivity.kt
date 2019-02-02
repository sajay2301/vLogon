package com.vlogonappv1.Twofactorverification

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.MainActivity
import com.vlogonappv1.R
import com.vlogonappv1.SelectCountryActivity
import kotlinx.android.synthetic.main.activity_enter_mobile_number.*


import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*

class EnterMobileNumberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_mobile_number)

        toolbar?.apply {

            tvToolbarTitle.text = "First Step Verification"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility= View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
        btnsubmit.clicks().subscribe{


              if (etmobilenumber.text.toString() == "") {
                    Toast.makeText(
                        this@EnterMobileNumberActivity,
                        "Please Enter Your Mobile Number",
                        Toast.LENGTH_SHORT

                    ).show()
                } else {

                    val intent = Intent(this@EnterMobileNumberActivity, FirstStepMobileVerificationActivity::class.java)


                    intent.putExtra("mobilenumber", etmobilenumber.text.toString())
                    intent.putExtra("countrycode", selectcountrycode.text.toString())

                    startActivity(intent)
                    overridePendingTransition(R.anim.enter, R.anim.exit)
                    finish()
                }




        }
        selectcountrycode.clicks().subscribe {
            val intent = Intent(this, SelectCountryActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val countryCode = data!!.getStringExtra(SelectCountryActivity.RESULT_CONTRYCODE)
            //Toast.makeText(this, "You selected countrycode: $countryCode", Toast.LENGTH_LONG).show()
            selectcountrycode.text = countryCode
        }
    }
    override fun onBackPressed() {


        val intent = Intent(this@EnterMobileNumberActivity, MainActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()



    }
}
