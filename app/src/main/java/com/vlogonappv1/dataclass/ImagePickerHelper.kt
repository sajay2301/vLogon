package com.vlogonappv1.dataclass

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import com.vlogonappv1.BuildConfig
import com.vlogonappv1.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImagePickerHelper(private val activity: AppCompatActivity) {

    companion object {
        const val REQUEST_CAMERA = 0
        const val SELECT_FILE = 1
        const val PERMISSIONS_TAKE_IMAGE = 2004
        const val PERMISSIONS_PICK_IMAGE = 2005
    }

    var mFile: FileModel? = null
    var fileRequest = 0

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(imageFileName, ".png", storageDir)
    }

    fun selectOptionToLoadImage(fileRequest: Int) {
        this.fileRequest = fileRequest
        val items: Array<String> = activity.resources.getStringArray(R.array.dialog_items_choose_image)
        try {
            val builder = AlertDialog.Builder(activity, R.style.AlertDialogTheme)
            builder.setTitle(activity.getString(R.string.pick_image))
            builder.setItems(items) { dialog, item ->
                when {
                    items[item].equals(activity.resources.getString(R.string.camera), ignoreCase = true) -> {
                        dialog.dismiss()
                        actionTakeImage(fileRequest)
                    }
                    items[item].equals(activity.resources.getString(R.string.gallery), ignoreCase = true) -> {
                        dialog.dismiss()
                        actionPickImage(fileRequest)
                    }
                    items[item].equals(activity.resources.getString(R.string.cancel), ignoreCase = true) -> dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun actionTakeImage(fileRequest: Int) {
        this.fileRequest = fileRequest
        if (hasRequiredPermissionForTakeImage()) {
            fromCamera()
        } else {
            requestPermissionForTakeImage()
        }
    }

    fun actionPickImage(fileRequest: Int) {
        this.fileRequest = fileRequest
        if (hasRequiredPermissionForPickImage()) {
            fromGallery()
        } else {
            requestPermissionForPickImage()
        }
    }

    private fun hasRequiredPermissionForTakeImage(): Boolean {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForTakeImage() {
        ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_TAKE_IMAGE)
    }

    private fun requestPermissionForPickImage() {
        ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_PICK_IMAGE)
    }

    private fun hasRequiredPermissionForPickImage(): Boolean {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun fromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity.packageManager) != null) {
            try {
                mFile = FileModel(fileRequest, createImageFile())
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mFile?.let { mFile->
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                val fileUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID
                        + ".provider", mFile.mFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                activity.startActivityForResult(intent, REQUEST_CAMERA)
            }
        }
    }

    private fun fromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        activity.startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE)
    }

    data class FileModel(val fileRequest: Int, val mFile: File)
}