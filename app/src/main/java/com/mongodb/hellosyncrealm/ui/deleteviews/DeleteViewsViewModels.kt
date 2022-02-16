package com.mongodb.hellosyncrealm.ui.deleteviews

import androidx.lifecycle.*
import com.mongodb.hellosyncrealm.ui.home.model.VisitInfo
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.Credentials
import io.realm.mongodb.SyncConfiguration
import io.realm.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteViewsViewModels(private val realmApp: App) : ViewModel() {

    private val _visitInfo = MutableLiveData<VisitInfo>()
    val visitInfo: LiveData<Int> = Transformations.map(_visitInfo) {
        it.visitCount
    }

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun deleteViewCount(count: Int) {
        _isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            val user = realmApp.login(Credentials.anonymous())
            val config = SyncConfiguration.Builder(
                user = user,
                partitionValue = user.identity,
                schema = setOf(VisitInfo::class)
            ).build()
            val realm = Realm.open(config)
            val visitInfo = realm.write {
                val info = query<VisitInfo>().first().find()
                val updated = info?.apply {
                    visitCount = if (info.visitCount.minus(count) >= 0)
                        info.visitCount.minus(count)
                    else
                        0
                } ?: VisitInfo().apply {
                    _id = user.identity
                }
                copyToRealm(updated)
            }


            withContext(Dispatchers.Main) {
                _visitInfo.value = visitInfo
            }
        }
    }
}