package io.bidon.mediation.sdk.adobject

interface BannerAdObjectListener : AdObjectListener<BannerAdObject> {

    fun onAdExpanded(adObject: BannerAdObject)

    fun onAdCollapsed(adObject: BannerAdObject)

}