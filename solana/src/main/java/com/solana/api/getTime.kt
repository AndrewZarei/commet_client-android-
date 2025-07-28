package com.solana.api

fun Api.getTime( onComplete: ((String) -> Unit)) {
    val params: MutableList<Any> = ArrayList()
  router.request("getTime", params, onComplete)
}