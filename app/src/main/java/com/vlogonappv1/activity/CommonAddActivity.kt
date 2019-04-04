package com.vlogonappv1.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Base64
import android.view.*
import android.widget.EditText
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.R
import com.vlogonappv1.contactlist.AddContactActivity
import com.vlogonappv1.contactlist.AddressBookActivity
import com.vlogonappv1.contactlist.ScanQrCodeActivity
import com.vlogonappv1.dataclass.ImagePickerHelper
import com.vlogonappv1.dataclass.getPath
import kotlinx.android.synthetic.main.activity_common_add.*
import kotlinx.android.synthetic.main.enteronlymobilenumber.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import java.io.File
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.BinaryBitmap
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.LuminanceSource
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.BufferedInputStream
import java.io.FileInputStream


class CommonAddActivity : AppCompatActivity() {


    companion object {
        var entermobiledialog: Dialog? = null
        private const val AD_IMAGE = 2
    }
    private var imagePickerHelper: ImagePickerHelper? = null
    private var adImage: File? = null
    private var fileRequest = AD_IMAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_add)

        toolbar?.apply {
            tvToolbarTitle.text = "Add Contact"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)
        }
        imagePickerHelper = ImagePickerHelper(this)
        linearaddmanually.clicks().subscribe{
            val intent = Intent(this@CommonAddActivity, AddContactActivity::class.java)
            intent.putExtra("addcontact","manually")
            intent.putExtra("value","false")
            overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)
            finish()


        }

        linearaddressbook.clicks().subscribe{
            val intent = Intent(this@CommonAddActivity, SelectImportContactActivity::class.java)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)
            finish()
        }

        linearqrscan.clicks().subscribe{
            val intent = Intent( this@CommonAddActivity, ScanQrCodeActivity::class.java)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)
            finish()
        }

        linearaddmobilenumber.clicks().subscribe{
            entermobiledialog = EnterMobileDialog(this@CommonAddActivity,"+91")
            entermobiledialog!!.show()
        }

        linearqrscangallery.clicks().subscribe{

            imagePickerHelper?.LoadImage(AD_IMAGE)

        }

    }

    fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null

        val intArray = IntArray(bMap.width * bMap.height)
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)

        val source = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        val reader = MultiFormatReader()
        try {
            val result = reader.decode(bitmap)
            contents = result.text
            Log.e("QrTest", contents)
        } catch (e: Exception) {
            Log.e("QrTest", "Error decoding barcode", e)
        }

        return contents
    }
    fun EnterMobileDialog(context: Context,countrycode :String): Dialog {


        val inflate = LayoutInflater.from(context).inflate(R.layout.enteronlymobilenumber, null)
        val resetdialog = Dialog(context)
        resetdialog.setContentView(inflate)
        resetdialog.setCancelable(false)
        resetdialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val window = resetdialog.window
        val wlp = window.attributes
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        resetdialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )


        resetdialog.buttoncancel.clicks().subscribe {
            resetdialog.dismiss()
        }

        resetdialog.selectcountrycode.clicks().subscribe {
            resetdialog.dismiss()
            val intent = Intent(this, SelectCountryActivity::class.java)
            startActivityForResult(intent, 1)
        }

        resetdialog.selectcountrycode.text=countrycode
        resetdialog.buttonok.clicks().subscribe {


            val countrycode = resetdialog.selectcountrycode.text.toString()
            val etmobilenumber = resetdialog.etmobilenumber.text.toString()
            val intent = Intent(this@CommonAddActivity, AddContactActivity::class.java)
            intent.putExtra("addcontact","addmobilenumber")
            intent.putExtra("value",etmobilenumber)
            intent.putExtra("countrycode",countrycode)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            startActivity(intent)
            AddressBookActivity.fa!!.finish();
            finish()
            resetdialog.dismiss()
        }


        resetdialog.setOnKeyListener { dialog, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                resetdialog.dismiss()
                true
            } else {
                false
            }
        }


        return resetdialog
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
                            }
                        }
                    }
                    ImagePickerHelper.SELECT_FILE1 -> {
                        when (imagePickerHelper?.fileRequest) {
                            AD_IMAGE -> {
                                contentResolver.notifyChange(data?.data, null)
                                data?.data?.let {
                                    adImage = File(getPath(it))
                                    val `is` = BufferedInputStream(FileInputStream(adImage))
                                    val bitmap = BitmapFactory.decodeStream(`is`)
                                    val decoded = scanQRImage(bitmap)

                                    if(decoded==null) {
                                       Toast.makeText(this@CommonAddActivity,"QR Code Not Read",Toast.LENGTH_SHORT).show()
                                    }else
                                    {
                                        val intent = Intent(this@CommonAddActivity, AddContactActivity::class.java)
                                        intent.putExtra("addcontact", "qrreader")
                                        intent.putExtra("value", decoded)
                                        overridePendingTransition(R.anim.enter, R.anim.exit)
                                        startActivity(intent)
                                        AddressBookActivity.fa!!.finish();
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            try {
                val countryCode = data!!.getStringExtra(SelectCountryActivity.RESULT_CONTRYCODE)
                //Toast.makeText(this, "You selected countrycode: $countryCode", Toast.LENGTH_LONG).show()
                entermobiledialog = EnterMobileDialog(this@CommonAddActivity, countryCode)
                entermobiledialog!!.show()
            }catch (e : IllegalStateException)
            {

            }
        }
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

}
