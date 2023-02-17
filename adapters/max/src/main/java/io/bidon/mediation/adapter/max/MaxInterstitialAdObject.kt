package io.bidon.mediation.adapter.max

import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import io.bidon.mediation.adapter.max.MaxUtils.getCPM
import io.bidon.mediation.adapter.max.MaxUtils.setExtraParameters
import io.bidon.mediation.adapter.max.MaxUtils.setLocalExtraParameters
import io.bidon.mediation.adapter.max.MaxUtils.toMediationAdError
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.adobject.AdObjectParameters
import io.bidon.mediation.sdk.adobject.InterstitialAdObject
import io.bidon.mediation.sdk.adobject.InterstitialAdObjectListener
import io.bidon.mediation.sdk.adobject.MediationError

internal class MaxInterstitialAdObject(private val networkAdUnit: MaxNetworkAdUnit) : InterstitialAdObject() {

    private var maxInterstitialAd: MaxInterstitialAd? = null

    override fun load(contextProvider: ContextProvider,
                      adObjectParameters: AdObjectParameters,
                      listener: InterstitialAdObjectListener) {
        val activity = contextProvider.activity
        if (activity == null) {
            listener.onAdFailToLoad(this, MediationError.invalidParameter("Activity is null"))
            return
        }
        val adUnitId = networkAdUnit.getAdUnitId()
        if (adUnitId.isNullOrEmpty()) {
            listener.onAdFailToLoad(this, MediationError.invalidParameter("AdUnitId is null or empty"))
            return
        }
        maxInterstitialAd = MaxInterstitialAd(adUnitId, activity).apply {
            setListener(Listener(listener))
            setExtraParameters(networkAdUnit.getExtraParameters())
            setLocalExtraParameters(networkAdUnit.getLocalExtraParameters())
            loadAd()
        }
    }

    override fun canShow(): Boolean = maxInterstitialAd?.isReady == true

    override fun show(contextProvider: ContextProvider, listener: InterstitialAdObjectListener) {
        maxInterstitialAd?.showAd()
            ?: listener.onAdFailToShow(this, MediationError.invalidState("Interstitial object is null"))
    }

    override fun onDestroy() {
        maxInterstitialAd?.destroy()
        maxInterstitialAd = null
    }


    private inner class Listener(private val listener: InterstitialAdObjectListener) : MaxAdListener {

        override fun onAdLoaded(maxAd: MaxAd) {
            listener.onAdLoaded(this@MaxInterstitialAdObject, maxAd.getCPM())
        }

        override fun onAdLoadFailed(adUnitId: String, maxError: MaxError) {
            listener.onAdFailToLoad(this@MaxInterstitialAdObject, maxError.toMediationAdError())
        }

        override fun onAdDisplayed(maxAd: MaxAd) {
            listener.onAdShown(this@MaxInterstitialAdObject)
        }

        override fun onAdDisplayFailed(maxAd: MaxAd, maxError: MaxError) {
            listener.onAdFailToShow(this@MaxInterstitialAdObject, maxError.toMediationAdError())
        }

        override fun onAdClicked(maxAd: MaxAd) {
            listener.onAdClicked(this@MaxInterstitialAdObject)
        }

        override fun onAdHidden(maxAd: MaxAd) {
            listener.onAdClosed(this@MaxInterstitialAdObject)
        }

    }

}