package com.mongodb.hellosyncrealm

import android.app.Application
import io.realm.mongodb.App

class HelloRealmSyncApp : Application() {

    val realmSync by lazy { App.create(BuildConfig.RealmAppId) }

}