package io.bidon.mediation.sdk.mediationad

import io.bidon.mediation.sdk.network.NetworkAdUnit

interface MediationAd<MediationAdListenerType> {

    /**
     * Sets listener.
     */
    var listener: MediationAdListenerType?

    /**
     * Sets time-out of pre bid block in milliseconds.
     */
    var preBidTimeOutMs: Long

    /**
     * Sets time-out of post bid block in milliseconds.
     */
    var postBidTimeOutMs: Long

    /**
     * Sets price floor of pre bid block.
     */
    var postBidPriceFloor: Double?

    /**
     * Sets ad unit list of pre bid block.
     */
    fun setPreBidNetworkAdUnit(networkAdUnitList: List<NetworkAdUnit>)

    /**
     * Sets ad unit list of post bid block.
     */
    fun setPostBidNetworkAdUnit(networkAdUnitList: List<NetworkAdUnit>)

    /**
     * At first load PreBid ad objects and then based on this result load PostBid ad objects.
     */
    fun loadAd()

    /**
     * Indicates that the mediation ad has finished loading all ad objects.
     */
    fun isLoadingCompleted(): Boolean

    /**
     * Indicates that the mediation ad has loaded ad objects that can be shown.
     */
    fun canShowAd(): Boolean

    /**
     * Clears objects.
     */
    fun destroy()

}