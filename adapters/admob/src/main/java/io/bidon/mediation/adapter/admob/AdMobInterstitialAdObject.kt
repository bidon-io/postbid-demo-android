package io.bidon.mediation.adapter.admob

import android.app.Activity
import android.content.Context
import androidx.annotation.UiThread
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import io.bidon.mediation.adapter.admob.AdMobUtils.findLineItem
import io.bidon.mediation.adapter.admob.AdMobUtils.toMediationAdError
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.Utils
import io.bidon.mediation.sdk.adobject.AdObjectParameters
import io.bidon.mediation.sdk.adobject.InterstitialAdObject
import io.bidon.mediation.sdk.adobject.InterstitialAdObjectListener
import io.bidon.mediation.sdk.adobject.MediationError

/**
 * Make all calls to the Mobile Ads SDK on the main thread.
 */
internal class AdMobInterstitialAdObject(private val networkAdUnit: AdMobNetworkAdUnit) : InterstitialAdObject() {

    private var lineItem: AdMobLineItem? = null
    private var interstitialAd: InterstitialAd? = null

    /**
     * Finds the first [AdMobLineItem] whose price is equal to or greater than the price floor and loads it.
     */
    override fun load(contextProvider: ContextProvider,
                      adObjectParameters: AdObjectParameters,
                      listener: InterstitialAdObjectListener) {
        val lineItems = networkAdUnit.getLineItems()
        if (lineItems.isNullOrEmpty()) {
            listener.onAdFailToLoad(this, MediationError.invalidParameter("LineItems is null or empty"))
            return
        }
        val priceFloor = adObjectParameters.priceFloor
        lineItems.findLineItem(priceFloor)?.also { lineItem ->
            Utils.onUiThread {
                loadAd(contextProvider.context, lineItem, listener)
            }
        } ?: listener.onAdFailToLoad(this,
                                     MediationError.invalidParameter("Can't find AdMobAdUnit at this price floor - $priceFloor"))
    }

    @UiThread
    private fun loadAd(context: Context, lineItem: AdMobLineItem, listener: InterstitialAdObjectListener) {
        this.lineItem = lineItem

        try {
            InterstitialAd.load(context,
                                lineItem.id,
                                networkAdUnit.obtainAdRequest(),
                                LoadListener(listener))
        } catch (e: Exception) {
            MediationLogger.throwable(e)
        }
    }

    override fun canShow(): Boolean = interstitialAd != null

    override fun show(contextProvider: ContextProvider, listener: InterstitialAdObjectListener) {
        val activity = contextProvider.activity
        if (activity == null) {
            listener.onAdFailToShow(this, MediationError.invalidState("Activity is null"))
            return
        }
        Utils.onUiThread {
            showAd(activity, listener)
        }
    }

    @UiThread
    private fun showAd(activity: Activity, listener: InterstitialAdObjectListener) {
        interstitialAd?.show(activity)
            ?: listener.onAdFailToShow(this, MediationError.invalidState("Interstitial object is null"))
    }

    override fun onDestroy() {
        lineItem = null
        Utils.onUiThread {
            destroyAd()
        }
    }

    @UiThread
    private fun destroyAd() {
        interstitialAd?.fullScreenContentCallback = null
        interstitialAd = null
    }


    private inner class LoadListener(private val listener: InterstitialAdObjectListener) :
            InterstitialAdLoadCallback() {

        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            this@AdMobInterstitialAdObject.interstitialAd = interstitialAd.apply {
                fullScreenContentCallback = ShowListener(listener)
            }

            listener.onAdLoaded(this@AdMobInterstitialAdObject, lineItem?.price)
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            listener.onAdFailToLoad(this@AdMobInterstitialAdObject, loadAdError.toMediationAdError())
        }

    }

    private inner class ShowListener(private val listener: InterstitialAdObjectListener) : FullScreenContentCallback() {

        override fun onAdShowedFullScreenContent() {
            listener.onAdShown(this@AdMobInterstitialAdObject)
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            listener.onAdFailToShow(this@AdMobInterstitialAdObject, adError.toMediationAdError())
        }

        override fun onAdClicked() {
            listener.onAdClicked(this@AdMobInterstitialAdObject)
        }

        override fun onAdDismissedFullScreenContent() {
            listener.onAdClosed(this@AdMobInterstitialAdObject)
        }

    }

}