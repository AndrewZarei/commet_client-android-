package com.blockchain.commet.util

sealed class DataState<T> {

    data class Message<T>(
        val message: String
    ) : DataState<T>()

    data class Data<T>(
        val data: T? = null
    ) : DataState<T>()

    data class Loading<T>(
        val isLoading: Boolean = false
    ) : DataState<T>()
}