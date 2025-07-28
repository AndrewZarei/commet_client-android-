package com.blockchain.commet.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.blockchain.commet.R


/***
 * get a number with pixel unit
 * @return number with dp unit
 */
val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/***
 * extension function
 * set visibility of view to VISIBLE
 */
fun View.show() {
    this.visibility = View.VISIBLE
}

/***
 * extension function
 * set visibility of view to INVISIBLE
 */
fun View.hide() {
    this.visibility = View.INVISIBLE
}

/***
 * extension function
 * set visibility of view to GONE
 */
fun View.gone() {
    this.visibility = View.GONE
}

/***
 * extension function
 * hide keyboard of view
 */
fun Activity.hideKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0) // hide
}

/***
 * extension function
 * hide keyboard of view
 */
fun EditText.hideKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

/***
 * extension function
 * show keyboard of view
 */
fun EditText.showKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

/***
 * extension function
 * show message as Toast short time
 */
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/***
 * extension function
 * show message as Toast short time
 */
fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

/***
 * extension function
 * show message as Lod.d
 */
fun Context.log(text: String) {
    Log.d(TAG, text)
}

/***
 * extension function
 * show message as Lod.d
 */
fun Fragment.log(text: String) {
    Log.d(TAG, text)
}

/***
 * extension function
 * set Light statusBar and dark font
 */
fun Fragment.setLightStatusBar(color: Int) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
        activity?.window?.insetsController?.setSystemBarsAppearance(
            APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        @Suppress("DEPRECATION")
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    activity?.window?.statusBarColor =
        ContextCompat.getColor(requireContext(), color)
}

/***
 * extension function
 * set Light statusBar and dark font
 */
fun Fragment.setDarkStatusBar() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
        activity?.window?.insetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        @Suppress("DEPRECATION")
        activity?.window?.decorView?.systemUiVisibility = 0
    }
    activity?.window?.statusBarColor =
        ContextCompat.getColor(requireContext(), R.color.colorPrimaryVariant)
}

/***
 * extension function
 * set Light statusBar and dark font
 */
fun Fragment.setCustomStatusBar() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
        activity?.window?.insetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        @Suppress("DEPRECATION")
        activity?.window?.decorView?.systemUiVisibility = 0
    }
    activity?.window?.statusBarColor =
        ContextCompat.getColor(requireContext(), R.color.splashStatus)
}

/***
 * extension function
 * get string from string files based on locale of app
 */
fun Application.getLocaleString(resourceId: Int): String {
    val result: String
    val config = Configuration(resources.configuration)
    config.setLocale(AppCompatDelegate.getApplicationLocales()[0])
    result = createConfigurationContext(config).getText(resourceId).toString()
    return result
}

/***
 * extension function
 * set textview drawable color
 */
fun TextView.setTextViewDrawableColor(color: Int) {
    for (drawable in compoundDrawablesRelative) {
        if (drawable != null) {
            drawable.colorFilter =
                PorterDuffColorFilter(
                    ContextCompat.getColor(context, color),
                    PorterDuff.Mode.SRC_IN
                )
        }
    }
}

/***
 * extension function
 * get resourceId of drawable by string name
 */
@SuppressLint("DiscouragedApi")
fun Context.getDrawableId(
    variableName: String?
): Int {
    return try {
        resources.getIdentifier(variableName, "drawable", packageName)
    } catch (e: Exception) {
        e.printStackTrace()
        -1
    }
}
