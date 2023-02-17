package io.bidon.mediation.sdk.adblock

import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.AdObject
import io.bidon.mediation.sdk.adobject.MediationError

interface AdBlockListener<AdObjectType : AdObject<*>, AdBlockType : AdBlock<AdBlockType, *, AdObjectType>> :
        AdBlockLoadListener<AdObjectType, AdBlockType> {

    fun onAdShown(adBlock: AdBlockType, adInfo: AdInfo)

    fun onAdFailToShow(adBlock: AdBlockType, adInfo: AdInfo, adError: MediationError)

    fun onAdClicked(adBlock: AdBlockType, adInfo: AdInfo)

    fun onAdExpired(adBlock: AdBlockType, adInfo: AdInfo)

}