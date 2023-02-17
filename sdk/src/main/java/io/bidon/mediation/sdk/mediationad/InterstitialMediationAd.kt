package io.bidon.mediation.sdk.mediationad

import android.content.Context
import io.bidon.mediation.sdk.adblock.InterstitialAdBlock
import io.bidon.mediation.sdk.adblock.InterstitialAdBlockListener
import io.bidon.mediation.sdk.adobject.InterstitialAdObject
import io.bidon.mediation.sdk.adobject.InterstitialAdObjectListener

class InterstitialMediationAd(context: Context) :
        FullscreenMediationAd<InterstitialMediationAd, InterstitialMediationAdListener, InterstitialAdBlock, InterstitialAdObject, InterstitialAdObjectListener>(context) {

    override fun createPreBidAdBlock(): InterstitialAdBlock = InterstitialAdBlock().apply {
        listener = PreBidAdBlockListener()
    }

    override fun createPostBidAdBlock(): InterstitialAdBlock = InterstitialAdBlock().apply {
        listener = PostBidAdBlockListener()
    }


    private inner class PreBidAdBlockListener : FullscreenPreBidAdBlockListener(), InterstitialAdBlockListener

    private inner class PostBidAdBlockListener : FullscreenPostBidAdBlockListener(), InterstitialAdBlockListener

}