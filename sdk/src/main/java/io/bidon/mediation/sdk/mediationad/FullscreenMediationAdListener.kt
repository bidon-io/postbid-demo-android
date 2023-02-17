package io.bidon.mediation.sdk.mediationad

import io.bidon.mediation.sdk.adobject.AdInfo

interface FullscreenMediationAdListener<MediationAdType : MediationAd<*>> : MediationAdListener<MediationAdType> {

    /**
     * Called when the mediation ad has been closed.
     */
    fun onAdClosed(mediationAd: MediationAdType, adInfo: AdInfo)

}