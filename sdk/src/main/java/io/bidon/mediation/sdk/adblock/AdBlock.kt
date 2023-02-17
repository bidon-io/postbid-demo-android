package io.bidon.mediation.sdk.adblock

import android.content.Context
import io.bidon.mediation.sdk.AdHolder
import io.bidon.mediation.sdk.adobject.AdObject
import io.bidon.mediation.sdk.network.NetworkAdUnit

interface AdBlock<
        AdBlockType : AdBlock<AdBlockType, AdBlockListenerType, AdObjectType>,
        AdBlockListenerType,
        AdObjectType : AdObject<*>> :
        AdHolder<AdObjectType> {

    var listener: AdBlockListenerType?
    var timeOutMs: Long

    fun load(context: Context, adBlockParameters: AdBlockParameters, networkAdUnitList: List<NetworkAdUnit>)

    fun canShowAd(): Boolean = getMostExpensiveAd() != null

    fun destroy()

}