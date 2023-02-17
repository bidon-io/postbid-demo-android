package io.bidon.mediation.sdk.adobject

interface AdObjectLoadListener<out AdObjectType : AdObject<*>> {

    /**
     * @param adObject loaded ad object
     * @param price eCPM of loaded ad object
     */
    fun onAdLoaded(adObject: @UnsafeVariance AdObjectType, price: Double?)

    fun onAdFailToLoad(adObject: @UnsafeVariance AdObjectType, adError: MediationError)

}