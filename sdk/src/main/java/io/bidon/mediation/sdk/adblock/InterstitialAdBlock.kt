package io.bidon.mediation.sdk.adblock

import io.bidon.mediation.sdk.AdUnitTransformResult
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.NetworkManager
import io.bidon.mediation.sdk.adobject.InterstitialAdObject
import io.bidon.mediation.sdk.adobject.InterstitialAdObjectListener
import io.bidon.mediation.sdk.network.NetworkAdUnit

class InterstitialAdBlock :
        FullscreenAdBlock<InterstitialAdBlock, InterstitialAdBlockListener, InterstitialAdObject, InterstitialAdObjectListener>() {

    /**
     * Creates list of interstitial ad objects.
     */
    override fun createAdObjectList(contextProvider: ContextProvider,
                                    adUnitList: List<NetworkAdUnit>): List<AdUnitTransformResult<InterstitialAdObject>> =
        NetworkManager.createInterstitialAdObjectList(contextProvider, adUnitList)

    override fun createAdObjectListener(): InterstitialAdObjectListener = InterstitialListener()


    private inner class InterstitialListener : FullscreenListener(), InterstitialAdObjectListener

}