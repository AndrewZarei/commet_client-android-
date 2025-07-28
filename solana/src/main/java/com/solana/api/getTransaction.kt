package com.solana.api

import com.solana.models.ConfirmedSignFAddr2

fun Api.getTransactions(
    transaction: String,
    limit: Int? = null,
    before: String? = null,
    until: String? = null,
    onComplete: (String) -> Unit
) {
    val params: MutableList<Any> = ArrayList()
    params.add(transaction)
    params.add(ConfirmedSignFAddr2(limit = limit?.toLong(), before = before, until = until))

    router.request(
        "getTransaction", params,
    ) { result ->
        onComplete(result)
    }
}
