package io.bidon.mediation.sdk.adobject

interface FullscreenAdObjectListener<out AdObjectType : AdObject<*>> : AdObjectListener<AdObjectType> {

    fun onAdClosed(adObject: @UnsafeVariance AdObjectType)

}