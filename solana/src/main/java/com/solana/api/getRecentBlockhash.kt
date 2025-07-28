package com.solana.api

import android.util.Log
import com.solana.models.RecentBlockhash
import java.lang.RuntimeException

fun Api.getRecentBlockhash(onComplete: ((Result<String>) -> Unit)) {
    val params: MutableList<Any> = ArrayList()
    val parameterMap: MutableMap<String, Any?> = HashMap()
    parameterMap["commitment"] = "finalized"
    params.add(parameterMap)
//    return router.request<RecentBlockhash>("getRecentBlockhash", params, RecentBlockhash::class.java){ result ->
    return router.request<RecentBlockhash>("getLatestBlockhash", params, RecentBlockhash::class.java){ result ->
        result.onSuccess { recentBlockHash ->
            onComplete(Result.success(recentBlockHash.value.blockhash))
            return@request
        }.onFailure {
            onComplete(Result.failure(RuntimeException(it)))
            return@request
        }
    }
}