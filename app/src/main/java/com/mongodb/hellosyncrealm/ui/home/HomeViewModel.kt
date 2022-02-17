package com.mongodb.hellosyncrealm.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongodb.hellosyncrealm.RealmDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _visitInfoCount = MutableStateFlow(0)
    val visitInfoCount: StateFlow<Int> = _visitInfoCount


    fun start() {
        viewModelScope.launch {
            RealmDatabase.queryFirst().collect {
                if (it != null) {
                    _visitInfoCount.value = it.visitCount
                }
            }
        }
        updateData()
    }

    private fun updateData() {
        viewModelScope.launch(Dispatchers.IO) {
            RealmDatabase.incrementViewsBy(1)
        }
    }

}