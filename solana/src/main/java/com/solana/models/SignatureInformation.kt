package com.solana.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignatureInformation(
    var blockTime: Double?,
    var err: String?,
    val memo: String?,
    val signature: String?,
    val confirmationStatus: String?,
    val slot: Double?
)