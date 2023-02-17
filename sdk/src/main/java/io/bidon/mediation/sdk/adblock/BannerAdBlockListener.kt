package io.bidon.mediation.sdk.adblock

import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.BannerAdObject

interface BannerAdBlockListener : AdBlockListener<BannerAdObject, BannerAdBlock> {

    fun onAdExpanded(adBlock: BannerAdBlock, adInfo: AdInfo)

    fun onAdCollapsed(adBlock: BannerAdBlock, adInfo: AdInfo)

}