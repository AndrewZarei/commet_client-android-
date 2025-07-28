package com.blockchain.commet.base

import androidx.lifecycle.ViewModel
import com.blockchain.commet.R
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class BaseViewModel
@Inject
constructor() : ViewModel() {
    var selectedTabId: Int = R.id.loginFragment

    protected val baseState = MutableStateFlow<BaseState>(BaseState.Loading(isLoading = false))

    protected fun CheckUser() {
        baseState.value = BaseState.CheckedUser
    }
}