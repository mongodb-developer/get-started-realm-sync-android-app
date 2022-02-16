package com.mongodb.hellosyncrealm.ui.updateviews

import androidx.lifecycle.*
import com.mongodb.hellosyncrealm.ui.home.model.VisitInfo
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.Credentials
import io.realm.mongodb.SyncConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateViewsViewModel(private val realmApp: App) : ViewModel() {

    private val _visitInfo = MutableLiveData<VisitInfo>()
    val visitInfo: LiveData<Int> = Transformations.map(_visitInfo) {
        it.visitCount
    }

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun updateViewCount(count: Int) {

        _isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            val user = realmApp.login(Credentials.anonymous())
            val syncCong = SyncConfiguration.Builder(
                user = user,
                schema = setOf(VisitInfo::class),
                partitionValue = user.identity
            ).build()

            val realm = Realm.open(syncCong)
            val updated = realm.write {
                val visitInfo = query(VisitInfo::class).first().find()
                val info = visitInfo?.apply {
                    visitCount += count
                }
                    ?: VisitInfo().apply {
                        visitCount = count
                        _id = user.identity
                    }
                copyToRealm(info)
            }

            withContext(Dispatchers.Main) {
                _visitInfo.value = updated
            }
        }
    }
}