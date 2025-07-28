package com.blockchain.commet.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.blockchain.commet.R
import com.google.android.material.snackbar.Snackbar


object PermissionUtil {


    /***
     * Function setupWriteExternalStoragePermissions
     * for request permission
     * with code checks for permission and
     * then request to it.
     */
    fun setupPermissions(
        context: Context,
        activity: Activity,
        permissionCode: Int
    ) {
        val permission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                permissionCode
            )
        }
    }


    /***
     *Function onSnack to show custom snackBar
     * @param view as View Type
     * @param context as Context
     */
    fun onSNACK(view: View, context: Context) {
        val snackBar = Snackbar.make(
            view, "اجازه ی دسترسی توسط کاربر داده نشد.",
            Snackbar.LENGTH_LONG
        ).setAction("Action", null)
        snackBar.setActionTextColor(Color.WHITE)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
        val textView =
            snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 22f
        textView.gravity = Gravity.CENTER
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER;
        } else {
            textView.gravity = Gravity.CENTER_HORIZONTAL
        }
        val font = ResourcesCompat.getFont(context, R.font.yekan_bakh_medium)
        textView.typeface = font
        snackBar.show()
    }

}

