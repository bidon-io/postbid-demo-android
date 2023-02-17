package io.bidon.mediation.adapter.bidmachine

import io.bidmachine.interstitial.InterstitialAd
import io.bidmachine.interstitial.InterstitialListener
import io.bidmachine.interstitial.InterstitialRequest
import io.bidmachine.utils.BMError
import io.bidon.mediation.adapter.bidmachine.BidMachineUtils.toMediationAdError
import io.bidon.mediation.adapter.bidmachine.BidMachineUtils.toPriceFloorParams
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.adobject.AdObjectParameters
import io.bidon.mediation.sdk.adobject.InterstitialAdObject
import io.bidon.mediation.sdk.adobject.InterstitialAdObjectListener
import io.bidon.mediation.sdk.adobject.MediationError

internal class BidMachineInterstitialAdObject(private val networkAdUnit: BidMachineNetworkAdUnit) :
        InterstitialAdObject() {

    private var interstitialAd: InterstitialAd? = null

    override fun load(contextProvider: ContextProvider,
                      adObjectParameters: AdObjectParameters,
                      listener: InterstitialAdObjectListener) {
        val request = BidMachineUtils.fillAdRequest(InterstitialRequest.Builder(), networkAdUnit)
                .setPriceFloorParams(adObjectParameters.priceFloor?.toPriceFloorParams())
                .setAdContentType(networkAdUnit.obtainAdContentType())
                .build()
        interstitialAd = InterstitialAd(contextProvider.context).apply {
            setListener(Listener(listener))
            load(request)
        }
    }

    override fun canShow(): Boolean = interstitialAd?.canShow() == true

    override fun show(contextProvider: ContextProvider, listener: InterstitialAdObjectListener) {
        interstitialAd?.show()
            ?: listener.onAdFailToShow(this, MediationError.invalidState("Interstitial object is null"))
    }

    override fun onDestroy() {
        interstitialAd?.also {
            it.setListener(null)
            it.destroy()
        }
        interstitialAd = null
    }


    private inner class Listener(private val listener: InterstitialAdObjectListener) : InterstitialListener {

        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            listener.onAdLoaded(this@BidMachineInterstitialAdObject, interstitialAd.auctionResult?.price)
        }

        override fun onAdLoadFailed(interstitialAd: InterstitialAd, bmError: BMError) {
            listener.onAdFailToLoad(this@BidMachineInterstitialAdObject, bmError.toMediationAdError())
        }

        override fun onAdShowFailed(interstitialAd: InterstitialAd, bmError: BMError) {
            listener.onAdFailToShow(this@BidMachineInterstitialAdObject, bmError.toMediationAdError())
        }

        override fun onAdImpression(interstitialAd: InterstitialAd) {
            listener.onAdShown(this@BidMachineInterstitialAdObject)
        }

        override fun onAdClicked(interstitialAd: InterstitialAd) {
            listener.onAdClicked(this@BidMachineInterstitialAdObject)
        }

        override fun onAdClosed(interstitialAd: InterstitialAd, finished: Boolean) {
            listener.onAdClosed(this@BidMachineInterstitialAdObject)
        }

        override fun onAdExpired(interstitialAd: InterstitialAd) {
            listener.onAdExpired(this@BidMachineInterstitialAdObject)
        }

    }

}