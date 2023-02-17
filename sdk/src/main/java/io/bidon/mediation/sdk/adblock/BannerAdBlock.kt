package io.bidon.mediation.sdk.adblock

import io.bidon.mediation.sdk.AdUnitTransformResult
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.NetworkManager
import io.bidon.mediation.sdk.adobject.BannerAdObject
import io.bidon.mediation.sdk.adobject.BannerAdObjectListener
import io.bidon.mediation.sdk.network.NetworkAdUnit

class BannerAdBlock : ViewAdBlock<BannerAdBlock, BannerAdBlockListener, BannerAdObject, BannerAdObjectListener>() {

    /**
     * Creates list of banner ad objects.
     */
    override fun createAdObjectList(contextProvider: ContextProvider,
                                    adUnitList: List<NetworkAdUnit>): List<AdUnitTransformResult<BannerAdObject>> =
        NetworkManager.createBannerAdObjectList(contextProvider, adUnitList)

    override fun createAdObjectListener(): BannerAdObjectListener = BannerListener()


    private open inner class BannerListener : BaseAdObjectListener(), BannerAdObjectListener {

        override fun onAdExpanded(adObject: BannerAdObject) {
            MediationLogger.log(adObject.tag, "onAdExpanded ($adObject)")

            listener?.onAdExpanded(this@BannerAdBlock, adObject.adInfo)
        }

        override fun onAdCollapsed(adObject: BannerAdObject) {
            MediationLogger.log(adObject.tag, "onAdCollapsed ($adObject)")

            listener?.onAdExpanded(this@BannerAdBlock, adObject.adInfo)
        }

    }

}