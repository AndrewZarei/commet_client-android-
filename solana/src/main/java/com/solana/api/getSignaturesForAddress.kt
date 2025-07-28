package com.solana.api

import com.solana.core.PublicKey
import com.solana.models.ConfirmedSignFAddr2
import com.solana.models.SignatureInformation
import java.util.Date

fun Api.getSignaturesForAddress(
    account: PublicKey,
    limit: Int? = null,
    before: String? = null,
    until: String? = null,
    onComplete: (String) -> Unit
) {
    val params: MutableList<Any> = ArrayList()
    params.add(account.toString())
    params.add(ConfirmedSignFAddr2(limit = limit?.toLong(), before = before, until = until))

    router.request(
        "getSignaturesForAddress", params,
    ) { result ->
        onComplete(result)
    }
}
