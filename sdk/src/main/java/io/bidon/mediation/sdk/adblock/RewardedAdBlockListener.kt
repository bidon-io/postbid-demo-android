package io.bidon.mediation.sdk.adblock

import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.Reward
import io.bidon.mediation.sdk.adobject.RewardedAdObject

interface RewardedAdBlockListener : FullscreenAdBlockListener<RewardedAdObject, RewardedAdBlock> {

    fun onAdRewarded(adBlock: RewardedAdBlock, adInfo: AdInfo, reward: Reward)

}