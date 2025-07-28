package com.blockchain.commet.base

/**
 * maintain states of BaseViewModel
 */
sealed interface BaseState {

    data class Loading(val isLoading: Boolean) : BaseState

    data class Message(val text: String) : BaseState

    data object CheckedUser : BaseState

}