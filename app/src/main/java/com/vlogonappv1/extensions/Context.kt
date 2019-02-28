package com.vlogonappv1.extensions

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.BlockedNumberContract
import android.provider.BlockedNumberContract.BlockedNumbers
import android.provider.ContactsContract
import android.support.v4.content.FileProvider
import android.telecom.TelecomManager

import com.vlogonappv1.BuildConfig

import com.vlogonappv1.R
import com.vlogonappv1.models.*
import org.jetbrains.anko.toast
import java.io.File



val Context.telecomManager: TelecomManager get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    getSystemService(Context.TELECOM_SERVICE) as TelecomManager
} else {
    TODO("VERSION.SDK_INT < LOLLIPOP")
}


fun Context.sendEmailIntent(recipient: String) {
    Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.fromParts("mailto", recipient, null)
        if (resolveActivity(packageManager) != null) {
            startActivity(this)
        } else {
            toast("no_app_found")
        }
    }
}

fun Context.sendSMSIntent(recipient: String) {
    Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.fromParts("smsto", recipient, null)
        if (resolveActivity(packageManager) != null) {
            startActivity(this)
        } else {
            toast("no_app_found")
        }
    }
}

fun Context.sendAddressIntent(address: String) {
    val location = Uri.encode(address)
    val uri = Uri.parse("geo:0,0?q=$location")

    Intent(Intent.ACTION_VIEW, uri).apply {
        if (resolveActivity(packageManager) != null) {
            startActivity(this)
        } else {
            toast("no_app_found")
        }
    }
}

fun Context.openWebsiteIntent(url: String) {
    Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
        if (resolveActivity(packageManager) != null) {
            startActivity(this)
        } else {
            toast("no_app_found")
        }
    }
}

fun Context.getLookupUriRawId(dataUri: Uri): Int {
    val lookupKey = getLookupKeyFromUri(dataUri)
    if (lookupKey != null) {
        val uri = lookupContactUri(lookupKey, this)
        if (uri != null) {
            return getContactUriRawId(uri)
        }
    }
    return -1
}

fun Context.getContactUriRawId(uri: Uri): Int {
    val projection = arrayOf(ContactsContract.Contacts.NAME_RAW_CONTACT_ID)
    var cursor: Cursor? = null
    try {
        cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor.moveToFirst()) {
            return cursor.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID)
        }
    } catch (ignored: Exception) {
    } finally {
        cursor?.close()
    }
    return -1
}

// from https://android.googlesource.com/platform/packages/apps/Dialer/+/68038172793ee0e2ab3e2e56ddfbeb82879d1f58/java/com/android/contacts/common/util/UriUtils.java
fun getLookupKeyFromUri(lookupUri: Uri): String? {
    return if (!isEncodedContactUri(lookupUri)) {
        val segments = lookupUri.pathSegments
        if (segments.size < 3) null else Uri.encode(segments[2])
    } else {
        null
    }
}

fun isEncodedContactUri(uri: Uri?): Boolean {
    if (uri == null) {
        return false
    }
    val lastPathSegment = uri.lastPathSegment ?: return false
    return lastPathSegment == "encoded"
}

fun lookupContactUri(lookup: String, context: Context): Uri? {
    val lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookup)
    return try {
        ContactsContract.Contacts.lookupContact(context.contentResolver, lookupUri)
    } catch (e: Exception) {
        null
    }
}

fun Context.getCachePhoto(): File {
    val imagesFolder = File(cacheDir, "my_cache")
    if (!imagesFolder.exists()) {
        imagesFolder.mkdirs()
    }

    val file = File(imagesFolder, "Photo_${System.currentTimeMillis()}.jpg")
    file.createNewFile()
    return file
}

fun Context.getCachePhotoUri(file: File = getCachePhoto()) = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", file)

fun Context.getPhotoThumbnailSize(): Int {
    val uri = ContactsContract.DisplayPhoto.CONTENT_MAX_DIMENSIONS_URI
    val projection = arrayOf(ContactsContract.DisplayPhoto.THUMBNAIL_MAX_DIM)
    var cursor: Cursor? = null
    try {
        cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor?.moveToFirst() == true) {
            return cursor.getColumnIndex(ContactsContract.DisplayPhoto.THUMBNAIL_MAX_DIM)
        }
    } catch (ignored: Exception) {
    } finally {
        cursor?.close()
    }
    return 0
}





fun Context.sendSMSToContacts(contacts: ArrayList<Contact>) {
    val numbers = StringBuilder()
    contacts.forEach {
        val number = it.phoneNumbers.firstOrNull { it.type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE }
                ?: it.phoneNumbers.firstOrNull()
        if (number != null) {
            numbers.append("${number.value};")
        }

        val uriString = "smsto:${numbers.toString().trimEnd(';')}"
        Intent(Intent.ACTION_SENDTO, Uri.parse(uriString)).apply {
            if (resolveActivity(packageManager) != null) {
                startActivity(this)
            } else {
                toast("no_app_found")
            }
        }
    }
}

fun Context.sendEmailToContacts(contacts: ArrayList<Contact>) {
    val emails = ArrayList<String>()
    contacts.forEach {
        it.emails.forEach {
            if (it.value.isNotEmpty()) {
                emails.add(it.value)
            }
        }
    }

    Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, emails.toTypedArray())
        if (resolveActivity(packageManager) != null) {
            startActivity(this)
        } else {
            toast("no_app_found")
        }
    }
}

fun Context.getTempFile(): File? {
    val folder = File(cacheDir, "contacts")
    if (!folder.exists()) {
        if (!folder.mkdir()) {
            toast("no_app_found")
            return null
        }
    }

    return File(folder, "contacts.vcf")
}



@TargetApi(Build.VERSION_CODES.N)
fun Context.getBlockedNumbers(): ArrayList<BlockedNumber> {
    val blockedNumbers = ArrayList<BlockedNumber>()


    val uri = BlockedNumberContract.BlockedNumbers.CONTENT_URI
    val projection = arrayOf(
            BlockedNumberContract.BlockedNumbers.COLUMN_ID,
            BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
            BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER
    )

    var cursor: Cursor? = null
    try {
        cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor?.moveToFirst() == true) {
            do {
                val id = cursor.getColumnIndex(BlockedNumberContract.BlockedNumbers.COLUMN_ID).toLong()
                val number = cursor.getColumnIndex(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER) ?: ""
                val normalizedNumber = cursor.getColumnIndex(BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER) ?: ""
                val blockedNumber = BlockedNumber(id, number.toString(), normalizedNumber.toString())
                blockedNumbers.add(blockedNumber)
            } while (cursor.moveToNext())
        }
    } finally {
        cursor?.close()
    }

    return blockedNumbers
}



@TargetApi(Build.VERSION_CODES.N)
fun Context.addBlockedNumber(number: String) {
    ContentValues().apply {
        put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
        try {
            contentResolver.insert(BlockedNumbers.CONTENT_URI, this)
        } catch (e: Exception) {

        }
    }
}

@TargetApi(Build.VERSION_CODES.N)
fun Context.deleteBlockedNumber(number: String) {
    val values = ContentValues()
    values.put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
    val uri = contentResolver.insert(BlockedNumbers.CONTENT_URI, values)
    contentResolver.delete(uri, null, null)
}


