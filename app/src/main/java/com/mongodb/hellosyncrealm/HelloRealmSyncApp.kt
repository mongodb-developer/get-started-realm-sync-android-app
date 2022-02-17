package com.mongodb.hellosyncrealm

import android.app.Application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HelloRealmSyncApp : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            RealmDatabase.init()
        }
    }
}