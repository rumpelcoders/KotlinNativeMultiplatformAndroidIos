package com.rumpel.mpp.statesonsteroids.android.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Todo implement"
    }
    val text: LiveData<String> = _text
}
