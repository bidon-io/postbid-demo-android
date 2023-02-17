package io.bidon.mediation.sdk.adblock

import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.AdObject

interface FullscreenAdBlockListener<AdObjectType : AdObject<*>, AdBlockType : AdBlock<AdBlockType, *, AdObjectType>> :
        AdBlockListener<AdObjectType, AdBlockType> {

    fun onAdClosed(adBlock: AdBlockType, adInfo: AdInfo)

}