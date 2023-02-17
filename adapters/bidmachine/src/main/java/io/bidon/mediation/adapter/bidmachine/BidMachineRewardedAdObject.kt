package io.bidon.mediation.adapter.bidmachine

import io.bidmachine.rewarded.RewardedAd
import io.bidmachine.rewarded.RewardedListener
import io.bidmachine.rewarded.RewardedRequest
import io.bidmachine.utils.BMError
import io.bidon.mediation.adapter.bidmachine.BidMachineUtils.toMediationAdError
import io.bidon.mediation.adapter.bidmachine.BidMachineUtils.toPriceFloorParams
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.adobject.*

internal class BidMachineRewardedAdObject(private val networkAdUnit: BidMachineNetworkAdUnit) : RewardedAdObject() {

    private var rewardedAd: RewardedAd? = null

    override fun load(contextProvider: ContextProvider,
                      adObjectParameters: AdObjectParameters,
                      listener: RewardedAdObjectListener) {
        val request = BidMachineUtils.fillAdRequest(RewardedRequest.Builder(), networkAdUnit)
                .setPriceFloorParams(adObjectParameters.priceFloor?.toPriceFloorParams())
                .build()
        rewardedAd = RewardedAd(contextProvider.context).apply {
            setListener(Listener(listener))
            load(request)
        }
    }

    override fun canShow(): Boolean = rewardedAd?.canShow() == true

    override fun show(contextProvider: ContextProvider, listener: RewardedAdObjectListener) {
        rewardedAd?.show()
            ?: listener.onAdFailToShow(this, MediationError.invalidState("Rewarded object is null"))
    }

    override fun onDestroy() {
        rewardedAd?.also {
            it.setListener(null)
            it.destroy()
        }
        rewardedAd = null
    }


    private inner class Listener(private val listener: RewardedAdObjectListener) : RewardedListener {

        override fun onAdLoaded(rewardedAd: RewardedAd) {
            listener.onAdLoaded(this@BidMachineRewardedAdObject, rewardedAd.auctionResult?.price)
        }

        override fun onAdLoadFailed(rewardedAd: RewardedAd, bmError: BMError) {
            listener.onAdFailToLoad(this@BidMachineRewardedAdObject, bmError.toMediationAdError())
        }

        override fun onAdShowFailed(rewardedAd: RewardedAd, bmError: BMError) {
            listener.onAdFailToShow(this@BidMachineRewardedAdObject, bmError.toMediationAdError())
        }

        override fun onAdImpression(rewardedAd: RewardedAd) {
            listener.onAdShown(this@BidMachineRewardedAdObject)
        }

        override fun onAdClicked(rewardedAd: RewardedAd) {
            listener.onAdClicked(this@BidMachineRewardedAdObject)
        }

        override fun onAdClosed(rewardedAd: RewardedAd, finished: Boolean) {
            listener.onAdClosed(this@BidMachineRewardedAdObject)
        }

        override fun onAdExpired(rewardedAd: RewardedAd) {
            listener.onAdExpired(this@BidMachineRewardedAdObject)
        }

        override fun onAdRewarded(rewardedAd: RewardedAd) {
            listener.onAdRewarded(this@BidMachineRewardedAdObject, Reward())
        }

    }

}