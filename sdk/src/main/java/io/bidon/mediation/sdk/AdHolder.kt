package io.bidon.mediation.sdk

import io.bidon.mediation.sdk.adobject.AdObject

interface AdHolder<AdObjectType : AdObject<*>> {

    fun getLoadedAdObjectList(): List<AdObjectType>

    fun getLoadedAdObjectSize(): Int = getLoadedAdObjectList().size

    fun getMostExpensiveAd(): AdObjectType?

}