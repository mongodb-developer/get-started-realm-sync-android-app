package com.mongodb.hellosyncrealm.ui.home.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class VisitInfo : RealmObject {

    @PrimaryKey
    var _id: String = UUID.randomUUID().toString()

    var visitCount: Int = 0

    var partition: String = ""
}

fun VisitInfo.updateCount(): VisitInfo {
    return apply {
        visitCount++
    }
}