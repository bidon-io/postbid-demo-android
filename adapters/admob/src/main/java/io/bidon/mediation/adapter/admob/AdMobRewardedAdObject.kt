package io.bidon.mediation.adapter.admob

import android.app.Activity
import android.content.Context
import androidx.annotation.UiThread
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import io.bidon.mediation.adapter.admob.AdMobUtils.findLineItem
import io.bidon.mediation.adapter.admob.AdMobUtils.toMediationAdError
import io.bidon.mediation.adapter.admob.AdMobUtils.toReward
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.Utils
import io.bidon.mediation.sdk.adobject.AdObjectParameters
import io.bidon.mediation.sdk.adobject.MediationError
import io.bidon.mediation.sdk.adobject.RewardedAdObject
import io.bidon.mediation.sdk.adobject.RewardedAdObjectListener

/**
 * Make all calls to the Mobile Ads SDK on the main thread.
 */
internal class AdMobRewardedAdObject(private val networkAdUnit: AdMobNetworkAdUnit) : RewardedAdObject() {

    private var lineItem: AdMobLineItem? = null
    private var rewardedAd: RewardedAd? = null

    /**
     * Finds the first [AdMobLineItem] whose price is equal to or greater than the price floor and loads it.
     */
    override fun load(contextProvider: ContextProvider,
                      adObjectParameters: AdObjectParameters,
                      listener: RewardedAdObjectListener) {
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
    private fun loadAd(context: Context, lineItem: AdMobLineItem, listener: RewardedAdObjectListener) {
        this.lineItem = lineItem

        try {
            RewardedAd.load(context,
                            lineItem.id,
                            networkAdUnit.obtainAdRequest(),
                            LoadListener(listener))
        } catch (e: Exception) {
            MediationLogger.throwable(e)
        }
    }

    override fun canShow(): Boolean = rewardedAd != null

    override fun show(contextProvider: ContextProvider, listener: RewardedAdObjectListener) {
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
    private fun showAd(activity: Activity, listener: RewardedAdObjectListener) {
        rewardedAd?.show(activity, RewardListener(listener))
            ?: listener.onAdFailToShow(this, MediationError.invalidState("Rewarded object is null"))
    }

    override fun onDestroy() {
        lineItem = null
        Utils.onUiThread {
            destroyAd()
        }
    }

    @UiThread
    private fun destroyAd() {
        rewardedAd?.fullScreenContentCallback = null
        rewardedAd = null
    }


    private inner class LoadListener(private val listener: RewardedAdObjectListener) : RewardedAdLoadCallback() {

        override fun onAdLoaded(rewardedAd: RewardedAd) {
            this@AdMobRewardedAdObject.rewardedAd = rewardedAd.apply {
                fullScreenContentCallback = ShowListener(listener)
            }

            listener.onAdLoaded(this@AdMobRewardedAdObject, lineItem?.price)
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            listener.onAdFailToLoad(this@AdMobRewardedAdObject, loadAdError.toMediationAdError())
        }

    }

    private inner class ShowListener(private val listener: RewardedAdObjectListener) : FullScreenContentCallback() {

        override fun onAdShowedFullScreenContent() {
            listener.onAdShown(this@AdMobRewardedAdObject)
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            listener.onAdFailToShow(this@AdMobRewardedAdObject, adError.toMediationAdError())
        }

        override fun onAdClicked() {
            listener.onAdClicked(this@AdMobRewardedAdObject)
        }

        override fun onAdDismissedFullScreenContent() {
            listener.onAdClosed(this@AdMobRewardedAdObject)
        }

    }

    private inner class RewardListener(private val listener: RewardedAdObjectListener) : OnUserEarnedRewardListener {

        override fun onUserEarnedReward(rewardItem: RewardItem) {
            listener.onAdRewarded(this@AdMobRewardedAdObject, rewardItem.toReward())
        }

    }

}