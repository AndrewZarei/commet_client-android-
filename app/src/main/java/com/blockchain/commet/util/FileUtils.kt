package com.blockchain.commet.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

/***
 * extension function
 * get data from backStack Fragments
 */
fun Context.openPDF(file: File?) {
    val pdfIntent = Intent(Intent.ACTION_VIEW)
    val pdfURI = FileProvider.getUriForFile(
        this, "$packageName.provider",
        file!!
    )
    pdfIntent.setDataAndType(pdfURI, "application/pdf")
    pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    try {
        startActivity(pdfIntent)
    } catch (e: ActivityNotFoundException) {
        toast("متاسفانه برنامه ای برای باز کردن این فایل یافت نشد!")
    }
}