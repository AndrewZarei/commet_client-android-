package com.solana.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecentBlockhash(
    override val value: Value
) : RPC<RecentBlockhash.Value>(null, value) {
    @JsonClass(generateAdapter = true)
    class Value (
        val blockhash: String
    )
}