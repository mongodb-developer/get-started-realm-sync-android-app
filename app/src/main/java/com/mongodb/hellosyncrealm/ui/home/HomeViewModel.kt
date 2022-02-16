package com.mongodb.hellosyncrealm.ui.home

import androidx.lifecycle.*
import com.mongodb.hellosyncrealm.ui.home.model.VisitInfo
import com.mongodb.hellosyncrealm.ui.home.model.updateCount
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.Credentials
import io.realm.mongodb.SyncConfiguration
import io.realm.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeViewModel(private val realmApp: App) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome to Realm"
    }
    val text: LiveData<String> = _text

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val visitInfo: LiveData<Int> = Transformations.map(onRefreshCount().asLiveData()) {
        it?.visitCount
    }

    init {
        updateData()
    }

    private fun updateData() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val user = realmApp.login(Credentials.anonymous())
            val config = SyncConfiguration.Builder(
                user = user,
                partitionValue = user.identity,
                schema = setOf(VisitInfo::class)
            ).build()

            val realm = Realm.open(configuration = config)
            realm.write {
                val visitInfo = this.query<VisitInfo>().first().find()
                copyToRealm(visitInfo?.updateCount()
                    ?: VisitInfo().apply {
                        partition = user.identity
                        visitCount = 1
                    })
            }
            _isLoading.postValue(false)
        }
    }

    fun onRefreshCount(): Flow<VisitInfo?> {

        val user = runBlocking { realmApp.login(Credentials.anonymous()) }
        val config = SyncConfiguration.Builder(
            user = user,
            partitionValue = user.identity,
            schema = setOf(VisitInfo::class)
        ).build()

        val realm = Realm.open(config)
        return realm.query<VisitInfo>().first().asFlow()
    }
}