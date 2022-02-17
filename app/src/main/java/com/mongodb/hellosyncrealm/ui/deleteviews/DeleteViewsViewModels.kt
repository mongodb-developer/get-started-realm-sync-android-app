package com.mongodb.hellosyncrealm.ui.deleteviews

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

class DeleteViewsViewModels : ViewModel() {

    private val _visitInfo = MutableLiveData<VisitInfo>()
    val visitInfo: LiveData<Int> = Transformations.map(_visitInfo) {
        it.visitCount
    }

    fun deleteViewCount(count: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val visitInfo = RealmDatabase.decrementViewsBy(count)
            withContext(Dispatchers.Main) {
                _visitInfo.value = visitInfo
            }
        }
    }
}