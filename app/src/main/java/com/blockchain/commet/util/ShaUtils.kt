package com.blockchain.commet.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object ShaUtils {


    /***
     * Function sha256String converts username to password with sha256
     * @param source as String type.
     */
    fun sha256String(source: String): String? {
        var hash: ByteArray? = null
        var hashCode: String? = null

        try {
            val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
            hash = digest.digest(source.toByteArray())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        if (hash != null) {
            val hashBuilder = StringBuilder()
            for (b in hash) {
                val hex = Integer.toHexString(b.toInt())
                if (hex.length == 1) {
                    hashBuilder.append("0")
                    hashBuilder.append(hex[hex.length - 1])
                } else {
                    hashBuilder.append(hex.substring(hex.length - 2))
                }
            }
            hashCode = hashBuilder.toString()
        }

        return hashCode
    }


}