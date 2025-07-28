package com.blockchain.commet.util

import java.text.DecimalFormat


/***
 * extension function
 * set 2 digit for numbers
 */
fun Int.twoDigit(): String {
    return DecimalFormat("00").format(this)
}

/***
 * extension function
 * set 2 digit for numbers
 */
fun String.priceFormat(): String {
    val decimalFormat = DecimalFormat("###,###,###,###,###,###,###,###")
    return decimalFormat.format(decimalFormat.parse(this))
}


fun String.convertNumbersToEnglish(): String {
    var result = ""
    var en = '0'
    for (ch in this) {
        en = ch
        when (ch) {
            '۰' -> en = '0'
            '۱' -> en = '1'
            '۲' -> en = '2'
            '۳' -> en = '3'
            '۴' -> en = '4'
            '۵' -> en = '5'
            '۶' -> en = '6'
            '۷' -> en = '7'
            '۸' -> en = '8'
            '۹' -> en = '9'
        }
        result = "${result}$en"
    }
    return result
}