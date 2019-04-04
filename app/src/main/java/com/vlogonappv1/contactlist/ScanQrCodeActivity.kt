package com.vlogonappv1.contactlist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.view.ViewGroup
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.vlogonappv1.R
import com.vlogonappv1.activity.CommonAddActivity
import com.vlogonappv1.preference.ActivityUtils
import com.vlogonappv1.preference.AppPreference
import com.vlogonappv1.preference.AppUtils
import com.vlogonappv1.preference.PrefKey
import kotlinx.android.synthetic.main.activity_scan_qr_code.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.ArrayList
import android.graphics.BitmapFactory
import android.graphics.Bitmap




class ScanQrCodeActivity : AppCompatActivity() {


    private var mActivity: Activity? = null
    private var mContext: Context? = null
    private var contentFrame: ViewGroup? = null
    private var zXingScannerView: ZXingScannerView? = null
    private var mSelectedIndices: ArrayList<Int>? = null

    private var isFlash: Boolean = false
    private var isAutoFocus:Boolean = false
    private var camId: Int = 0
    private var frontCamId:Int = 0
    private var rearCamId:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr_code)

        contentFrame = findViewById<ViewGroup>(R.id.content_frame)

        initConfigs()
        initVar()
        zXingScannerView = ZXingScannerView(mActivity)
        setupFormats()
        initListener()

    }

    private fun initVar() {
        mActivity = this@ScanQrCodeActivity
        mContext = applicationContext

        isFlash = AppPreference.getInstance( this@ScanQrCodeActivity).getBoolean(PrefKey.FLASH, false)!! // flash off by default
        isAutoFocus = AppPreference.getInstance( this@ScanQrCodeActivity).getBoolean(PrefKey.FOCUS, true) !!// auto focus on by default
        camId = AppPreference.getInstance( this@ScanQrCodeActivity).getInteger(PrefKey.CAM_ID) // back camera by default
        if (camId == -1) {
            camId = rearCamId
        }

        loadCams()
    }
    private fun initConfigs() {
        if (isFlash) {
            flash.setImageResource(R.drawable.ic_flash_off)
        } else {
            flash.setImageResource(R.drawable.ic_flash_on)
        }
        if (isAutoFocus) {
            focus.setImageResource(R.drawable.ic_focus_off)
        } else {
            focus.setImageResource(R.drawable.ic_focus_on)
        }
    }

    private fun initListener() {

        flash.setOnClickListener { toggleFlash() }

        focus.setOnClickListener { toggleFocus() }

        camera.setOnClickListener { toggleCamera() }

        zXingScannerView!!.setResultHandler(object : ZXingScannerView.ResultHandler {
            override fun handleResult(result: Result) {

                val resultStr = result.text
             /*   val previousResult = AppPreference.getInstance(this@ScanQrCodeActivity).getStringArray(PrefKey.RESULT_LIST)
                previousResult.add(resultStr)
                AppPreference.getInstance(this@ScanQrCodeActivity).setStringArray(PrefKey.RESULT_LIST, previousResult)
*/
                zXingScannerView!!.resumeCameraPreview(this)
                val intent = Intent(this@ScanQrCodeActivity, AddContactActivity::class.java)
                intent.putExtra("addcontact","qrreader")
                intent.putExtra("value",resultStr)
                overridePendingTransition(R.anim.enter, R.anim.exit)
                startActivity(intent)
                finish()
               // ActivityUtils.instance.invokeActivity(this@ScanQrCodeActivity, AddContactActivity::class.java, false)

            }
        })

    }

    private fun activateScanner() {
        if (zXingScannerView != null) {

            if (zXingScannerView!!.parent != null) {
                (zXingScannerView!!.parent as ViewGroup).removeView(zXingScannerView) // to prevent crush on re adding view
            }
            contentFrame!!.addView(zXingScannerView)

            if (zXingScannerView!!.isActivated) {
                zXingScannerView!!.stopCamera()
            }

            zXingScannerView!!.startCamera(camId)
            zXingScannerView!!.flash = isFlash
            zXingScannerView!!.setAutoFocus(isAutoFocus)
        }
    }


    fun setupFormats() {
        val formats = ArrayList<BarcodeFormat>()
        if (mSelectedIndices == null || mSelectedIndices!!.isEmpty()) {
            mSelectedIndices = ArrayList()
            for (i in ZXingScannerView.ALL_FORMATS.indices) {
                mSelectedIndices!!.add(i)
            }
        }

        for (index in mSelectedIndices!!) {
            formats.add(ZXingScannerView.ALL_FORMATS[index])
        }
        if (zXingScannerView != null) {
            zXingScannerView!!.setFormats(formats)
        }
    }

    public override fun onResume() {
        super.onResume()
        activateScanner()
    }

    public override fun onPause() {
        super.onPause()
        if (zXingScannerView != null) {
            zXingScannerView!!.stopCamera()
        }
    }




    private fun toggleFlash() {
        if (isFlash) {
            isFlash = false
            flash.setImageResource(R.drawable.ic_flash_on)
        } else {
            isFlash = true
            flash.setImageResource(R.drawable.ic_flash_off)
        }
        AppPreference.getInstance(this@ScanQrCodeActivity).setBoolean(PrefKey.FLASH, isFlash)
        zXingScannerView!!.flash = isFlash
    }

    private fun toggleFocus() {
        if (isAutoFocus) {
            isAutoFocus = false
            focus.setImageResource(R.drawable.ic_focus_on)
            AppUtils.showToast(this@ScanQrCodeActivity, getString(R.string.autofocus_off))
        } else {
            isAutoFocus = true
            focus.setImageResource(R.drawable.ic_focus_off)
            AppUtils.showToast(this@ScanQrCodeActivity, getString(R.string.autofocus_on))
        }
        AppPreference.getInstance(this@ScanQrCodeActivity).setBoolean(PrefKey.FOCUS, isAutoFocus)
        zXingScannerView!!.isFocusable = isAutoFocus
    }

    private fun toggleCamera() {

        if (camId == rearCamId) {
            camId = frontCamId
        } else {
            camId = rearCamId
        }
        AppPreference.getInstance(this@ScanQrCodeActivity).setInteger(PrefKey.CAM_ID, camId)
        zXingScannerView!!.stopCamera()
        zXingScannerView!!.startCamera(camId)
    }



    private fun loadCams() {
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontCamId = i
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                rearCamId = i
            }
        }
        AppPreference.getInstance(this@ScanQrCodeActivity).setInteger(PrefKey.CAM_ID, rearCamId)

    }

    override fun onBackPressed() {

        val intent = Intent( this@ScanQrCodeActivity, CommonAddActivity::class.java)
        overridePendingTransition(R.anim.enter, R.anim.exit)
        startActivity(intent)
        finish()


    }
}
