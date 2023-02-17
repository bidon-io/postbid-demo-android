package io.bidon.mediation.sdk.adblock

import io.bidon.mediation.sdk.adobject.AdObject

interface AdBlockLoadListener<AdObjectType : AdObject<*>, AdBlockType : AdBlock<AdBlockType, *, AdObjectType>> {

    fun onAdBlockLoaded(adBlock: AdBlockType)

    fun onAdBlockFailToLoad(adBlock: AdBlockType)

}