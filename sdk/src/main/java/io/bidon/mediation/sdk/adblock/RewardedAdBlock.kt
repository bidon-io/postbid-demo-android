package io.bidon.mediation.sdk.adblock

import io.bidon.mediation.sdk.AdUnitTransformResult
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.NetworkManager
import io.bidon.mediation.sdk.adobject.Reward
import io.bidon.mediation.sdk.adobject.RewardedAdObject
import io.bidon.mediation.sdk.adobject.RewardedAdObjectListener
import io.bidon.mediation.sdk.network.NetworkAdUnit

class RewardedAdBlock :
        FullscreenAdBlock<RewardedAdBlock, RewardedAdBlockListener, RewardedAdObject, RewardedAdObjectListener>() {

    /**
     * Creates list of rewarded ad objects.
     */
    override fun createAdObjectList(contextProvider: ContextProvider,
                                    adUnitList: List<NetworkAdUnit>): List<AdUnitTransformResult<RewardedAdObject>> =
        NetworkManager.createRewardedAdObjectList(contextProvider, adUnitList)

    override fun createAdObjectListener(): RewardedAdObjectListener = RewardedListener()


    private inner class RewardedListener : FullscreenListener(), RewardedAdObjectListener {

        override fun onAdRewarded(adObject: RewardedAdObject, reward: Reward) {
            MediationLogger.log(adObject.tag, "onAdRewarded ($adObject)")

            listener?.onAdRewarded(this@RewardedAdBlock, adObject.adInfo, reward)
        }

    }

}