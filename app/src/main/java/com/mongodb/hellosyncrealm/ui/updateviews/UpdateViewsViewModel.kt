package com.mongodb.hellosyncrealm.ui.updateviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongodb.hellosyncrealm.RealmDatabase
import com.mongodb.hellosyncrealm.ui.home.model.VisitInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateViewsViewModel : ViewModel() {

    private val _visitInfo = MutableLiveData<VisitInfo>()
    val visitInfo: LiveData<Int> = Transformations.map(_visitInfo) {
        it.visitCount
    }

    fun updateViewCount(count: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            val updated = RealmDatabase.incrementViewsBy(count)
            withContext(Dispatchers.Main) {
                _visitInfo.value = updated
            }
        }
    }
}