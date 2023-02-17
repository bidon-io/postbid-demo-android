package io.bidon.mediation.sdk.mediationad

import android.content.Context
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.Utils
import io.bidon.mediation.sdk.adblock.RewardedAdBlock
import io.bidon.mediation.sdk.adblock.RewardedAdBlockListener
import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.Reward
import io.bidon.mediation.sdk.adobject.RewardedAdObject
import io.bidon.mediation.sdk.adobject.RewardedAdObjectListener

class RewardedMediationAd(context: Context) :
        FullscreenMediationAd<RewardedMediationAd, RewardedMediationAdListener, RewardedAdBlock, RewardedAdObject, RewardedAdObjectListener>(context) {

    override fun createPreBidAdBlock(): RewardedAdBlock = RewardedAdBlock().apply {
        listener = PreBidAdBlockListener()
    }

    override fun createPostBidAdBlock(): RewardedAdBlock = RewardedAdBlock().apply {
        listener = PostBidAdBlockListener()
    }

    private fun onAdRewarded(adBlock: RewardedAdBlock, adInfo: AdInfo, reward: Reward) {
        MediationLogger.log(adBlock.tag, "onAdRewarded ($adInfo)")

        Utils.onUiThread {
            listener?.onAdRewarded(this, adInfo, reward)
        }
    }


    private inner class PreBidAdBlockListener : FullscreenPreBidAdBlockListener(), RewardedAdBlockListener {

        override fun onAdRewarded(adBlock: RewardedAdBlock, adInfo: AdInfo, reward: Reward) {
            this@RewardedMediationAd.onAdRewarded(adBlock, adInfo, reward)
        }

    }

    private inner class PostBidAdBlockListener : FullscreenPostBidAdBlockListener(), RewardedAdBlockListener {

        override fun onAdRewarded(adBlock: RewardedAdBlock, adInfo: AdInfo, reward: Reward) {
            this@RewardedMediationAd.onAdRewarded(adBlock, adInfo, reward)
        }

    }

}