package io.bidon.mediation.sdk.mediationad

import android.content.Context
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.Utils
import io.bidon.mediation.sdk.adblock.BaseAdBlock
import io.bidon.mediation.sdk.adblock.FullscreenAdBlockListener
import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.FullscreenAdObject
import io.bidon.mediation.sdk.adobject.FullscreenAdObjectListener
import io.bidon.mediation.sdk.adobject.MediationError

abstract class FullscreenMediationAd<
        SelfType : FullscreenMediationAd<SelfType, MediationAdListenerType, AdBlockType, AdObjectType, AdObjectListenerType>,
        MediationAdListenerType : FullscreenMediationAdListener<SelfType>,
        AdBlockType : BaseAdBlock<AdBlockType, *, AdObjectType, AdObjectListenerType>,
        AdObjectType : FullscreenAdObject<AdObjectListenerType>,
        AdObjectListenerType : FullscreenAdObjectListener<AdObjectType>>(context: Context) :
        BaseMediationAd<SelfType, MediationAdListenerType, AdBlockType, AdObjectType>(context) {

    /**
     * Shows ad object with highest price.
     */
    fun showAd() {
        getMostExpensiveAd()?.also {
            MediationLogger.log(tag, "showAd ($it)")

            it.show(contextProvider, it.listener)
        } ?: listener?.onAdFailToShow(this as SelfType, MediationError.invalidState("Nothing to show"))
    }

    private fun onAdClosed(adBlock: AdBlockType, adInfo: AdInfo) {
        MediationLogger.log(adBlock.tag, "onAdClosed ($adInfo)")

        Utils.onUiThread {
            listener?.onAdClosed(this as SelfType, adInfo)
        }
    }


    protected open inner class FullscreenPreBidAdBlockListener : PreBidAdBlockListener(),
            FullscreenAdBlockListener<AdObjectType, AdBlockType> {

        override fun onAdClosed(adBlock: AdBlockType, adInfo: AdInfo) {
            this@FullscreenMediationAd.onAdClosed(adBlock, adInfo)
        }

    }

    protected open inner class FullscreenPostBidAdBlockListener : PostBidAdBlockListener(),
            FullscreenAdBlockListener<AdObjectType, AdBlockType> {

        override fun onAdClosed(adBlock: AdBlockType, adInfo: AdInfo) {
            this@FullscreenMediationAd.onAdClosed(adBlock, adInfo)
        }

    }

}