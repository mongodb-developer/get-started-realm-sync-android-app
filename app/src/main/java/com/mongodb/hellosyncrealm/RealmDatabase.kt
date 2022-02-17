package com.mongodb.hellosyncrealm

import com.mongodb.hellosyncrealm.ui.home.model.VisitInfo
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.Credentials
import io.realm.mongodb.SyncConfiguration
import io.realm.mongodb.User
import io.realm.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object RealmDatabase {
    private lateinit var realm: Realm
    private lateinit var user: User
    private val _isSyncReady = MutableStateFlow(false)
    val isSyncReady: StateFlow<Boolean> = _isSyncReady

    suspend fun init() {
        user = App.create(BuildConfig.RealmAppId).login(Credentials.anonymous())
        val config = SyncConfiguration.Builder(
            user = user,
            partitionValue = user.identity,
            schema = setOf(VisitInfo::class)
        ).build()
        realm = Realm.open(config)
        _isSyncReady.value = true
    }

    suspend fun decrementViewsBy(count: Int): VisitInfo {
        return realm.write {
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
    }

    suspend fun incrementViewsBy(count: Int) : VisitInfo {
        return realm.write {
            val visitInfo: VisitInfo? = this.query<VisitInfo>().first().find()
            visitInfo?.apply {
                visitCount += count
            } ?: VisitInfo().apply {
                visitCount = count
                _id = user.identity
                partition = user.identity
                copyToRealm(this)
            }
        }
    }

    fun queryFirst(): Flow<VisitInfo?> {
        return realm.query<VisitInfo>().first().asFlow()
    }
}