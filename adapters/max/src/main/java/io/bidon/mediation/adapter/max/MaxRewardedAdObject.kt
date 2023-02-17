package io.bidon.mediation.adapter.max

import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import io.bidon.mediation.adapter.max.MaxUtils.getCPM
import io.bidon.mediation.adapter.max.MaxUtils.setExtraParameters
import io.bidon.mediation.adapter.max.MaxUtils.setLocalExtraParameters
import io.bidon.mediation.adapter.max.MaxUtils.toMediationAdError
import io.bidon.mediation.adapter.max.MaxUtils.toReward
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.adobject.AdObjectParameters
import io.bidon.mediation.sdk.adobject.MediationError
import io.bidon.mediation.sdk.adobject.RewardedAdObject
import io.bidon.mediation.sdk.adobject.RewardedAdObjectListener

internal class MaxRewardedAdObject(private val networkAdUnit: MaxNetworkAdUnit) : RewardedAdObject() {

    private var maxRewardedAd: MaxRewardedAd? = null

    override fun load(contextProvider: ContextProvider,
                      adObjectParameters: AdObjectParameters,
                      listener: RewardedAdObjectListener) {
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
        maxRewardedAd = MaxRewardedAd.getInstance(adUnitId, activity).apply {
            setListener(Listener(listener))
            setExtraParameters(networkAdUnit.getExtraParameters())
            setLocalExtraParameters(networkAdUnit.getLocalExtraParameters())
            loadAd()
        }
    }

    override fun canShow(): Boolean = maxRewardedAd?.isReady == true

    override fun show(contextProvider: ContextProvider, listener: RewardedAdObjectListener) {
        maxRewardedAd?.showAd()
            ?: listener.onAdFailToShow(this, MediationError.invalidState("Rewarded object is null"))
    }

    override fun onDestroy() {
        maxRewardedAd?.destroy()
        maxRewardedAd = null
    }


    private inner class Listener(private val listener: RewardedAdObjectListener) : MaxRewardedAdListener {

        override fun onAdLoaded(maxAd: MaxAd) {
            listener.onAdLoaded(this@MaxRewardedAdObject, maxAd.getCPM())
        }

        override fun onAdLoadFailed(adUnitId: String, maxError: MaxError) {
            listener.onAdFailToLoad(this@MaxRewardedAdObject, maxError.toMediationAdError())
        }

        override fun onAdDisplayed(maxAd: MaxAd) {
            listener.onAdShown(this@MaxRewardedAdObject)
        }

        override fun onAdDisplayFailed(maxAd: MaxAd, maxError: MaxError) {
            listener.onAdFailToShow(this@MaxRewardedAdObject, maxError.toMediationAdError())
        }

        override fun onAdClicked(maxAd: MaxAd) {
            listener.onAdClicked(this@MaxRewardedAdObject)
        }

        override fun onAdHidden(maxAd: MaxAd) {
            listener.onAdClosed(this@MaxRewardedAdObject)
        }

        override fun onUserRewarded(maxAd: MaxAd, maxReward: MaxReward) {
            listener.onAdRewarded(this@MaxRewardedAdObject, maxReward.toReward())
        }

        override fun onRewardedVideoStarted(maxAd: MaxAd) {

        }

        override fun onRewardedVideoCompleted(maxAd: MaxAd) {

        }

    }

}