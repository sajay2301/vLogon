package com.vlogonappv1.dataclass

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

import java.util.Arrays


class CodeGenerator : AsyncTask<Void, Void, Bitmap>() {

    private var resultListener: ResultListener? = null
    private var input: String? = null

    fun generateQRFor(input: String) {
        this.input = input
        TYPE = TYPE_QR
    }

    fun generateBarFor(input: String) {
        this.input = input
        TYPE = TYPE_BAR
    }

    fun setResultListener(resultListener: ResultListener) {
        this.resultListener = resultListener
    }

    override fun doInBackground(vararg voids: Void): Bitmap? {
        try {
            return if (TYPE == TYPE_QR) {
                createQRCode(this.input)
            } else {
                createBarcode(this.input)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(bitmap: Bitmap) {
        super.onPostExecute(bitmap)
        if (resultListener != null) {
            resultListener!!.onResult(bitmap)
        }
    }

    interface ResultListener {
        fun onResult(bitmap: Bitmap)
    }

    @Throws(WriterException::class)
    private fun createQRCode(str: String?): Bitmap? {
        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(
                str,
                BarcodeFormat.QR_CODE, QR_DIMENSION, QR_DIMENSION, null
            )
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }

        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (result.get(x, y)) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, QR_DIMENSION, 0, 0, w, h)
        return bitmap
    }

    @Throws(WriterException::class)
    private fun createBarcode(data: String?): Bitmap {
        val writer = MultiFormatWriter()
        val finalData = Uri.encode(data)

        // Use 1 as the height of the matrix as this is a 1D Barcode.
        val bm = writer.encode(finalData, BarcodeFormat.CODE_128, BAR_WIDTH, 1)
        val bmWidth = bm.width

        val imageBitmap = Bitmap.createBitmap(bmWidth, BAR_HEIGHT, Bitmap.Config.ARGB_8888)

        for (i in 0 until bmWidth) {
            // Paint columns of width 1
            val column = IntArray(BAR_HEIGHT)
            Arrays.fill(column, if (bm.get(i, 0)) Color.BLACK else Color.WHITE)
            imageBitmap.setPixels(column, 0, 1, i, 0, 1, BAR_HEIGHT)
        }

        return imageBitmap
    }

    companion object {

        val QR_DIMENSION = 1080
        val BAR_HEIGHT = 640
        val BAR_WIDTH = 1080

        private val WHITE = -0x1
        private val BLACK = -0x1000000
        private var TYPE: Int = 0
        private val TYPE_QR = 0
        private val TYPE_BAR = 1
    }


}
