package io.bidon.mediation.sdk.mediationad

import android.content.Context
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.Utils
import io.bidon.mediation.sdk.adblock.BannerAdBlock
import io.bidon.mediation.sdk.adblock.BannerAdBlockListener
import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.BannerAdObject
import io.bidon.mediation.sdk.adobject.BannerAdObjectListener

class BannerMediationAd(context: Context) :
        ViewMediationAd<BannerMediationAd, BannerMediationAdListener, BannerAdBlock, BannerAdObject, BannerAdObjectListener>(context) {

    override fun createPreBidAdBlock(): BannerAdBlock = BannerAdBlock().apply {
        listener = BannerPreBidAdBlockListener()
    }

    override fun createPostBidAdBlock(): BannerAdBlock = BannerAdBlock().apply {
        listener = BannerPostBidAdBlockListener()
    }

    private fun onAdExpanded(adBlock: BannerAdBlock, adInfo: AdInfo) {
        MediationLogger.log(adBlock.tag, "onAdExpanded ($adInfo)")

        Utils.onUiThread {
            listener?.onAdExpanded(this, adInfo)
        }
    }

    private fun onAdCollapsed(adBlock: BannerAdBlock, adInfo: AdInfo) {
        MediationLogger.log(adBlock.tag, "onAdCollapsed ($adInfo)")

        Utils.onUiThread {
            listener?.onAdCollapsed(this, adInfo)
        }
    }


    private inner class BannerPreBidAdBlockListener : PreBidAdBlockListener(), BannerAdBlockListener {

        override fun onAdExpanded(adBlock: BannerAdBlock, adInfo: AdInfo) {
            this@BannerMediationAd.onAdExpanded(adBlock, adInfo)
        }

        override fun onAdCollapsed(adBlock: BannerAdBlock, adInfo: AdInfo) {
            this@BannerMediationAd.onAdCollapsed(adBlock, adInfo)
        }

    }

    private inner class BannerPostBidAdBlockListener : PostBidAdBlockListener(), BannerAdBlockListener {

        override fun onAdExpanded(adBlock: BannerAdBlock, adInfo: AdInfo) {
            this@BannerMediationAd.onAdExpanded(adBlock, adInfo)
        }

        override fun onAdCollapsed(adBlock: BannerAdBlock, adInfo: AdInfo) {
            this@BannerMediationAd.onAdCollapsed(adBlock, adInfo)
        }

    }

}