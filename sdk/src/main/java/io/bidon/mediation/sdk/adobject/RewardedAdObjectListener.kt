package io.bidon.mediation.sdk.adobject

interface RewardedAdObjectListener : FullscreenAdObjectListener<RewardedAdObject> {

    fun onAdRewarded(adObject: RewardedAdObject, reward: Reward)

}