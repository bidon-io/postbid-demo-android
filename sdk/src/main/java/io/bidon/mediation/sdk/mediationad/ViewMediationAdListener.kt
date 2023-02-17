package io.bidon.mediation.sdk.mediationad

import io.bidon.mediation.sdk.adobject.AdInfo

interface ViewMediationAdListener<MediationAdType : MediationAd<*>> : MediationAdListener<MediationAdType> {

    /**
     * Called when the mediation ad has been expanded to full screen.
     */
    fun onAdExpanded(mediationAd: MediationAdType, adInfo: AdInfo)

    /**
     * Called when the mediation ad has been collapsed from full screen.
     */
    fun onAdCollapsed(mediationAd: MediationAdType, adInfo: AdInfo)

}