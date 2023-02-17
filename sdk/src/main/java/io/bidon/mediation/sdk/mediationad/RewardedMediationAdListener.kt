package io.bidon.mediation.sdk.mediationad

import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.Reward

interface RewardedMediationAdListener : FullscreenMediationAdListener<RewardedMediationAd> {

    /**
     * Called when the mediation ad has been rewarded.
     */
    fun onAdRewarded(mediationAd: RewardedMediationAd, adInfo: AdInfo, reward: Reward)

}