package io.bidon.mediation.adapter.bidmachine

import io.bidmachine.AdContentType
import io.bidmachine.CustomParams
import io.bidmachine.NetworkConfig
import io.bidmachine.TargetingParams
import io.bidmachine.banner.BannerSize
import io.bidon.mediation.sdk.network.NetworkAdUnit

class BidMachineNetworkAdUnit(params: MutableMap<String, Any> = mutableMapOf()) :
        NetworkAdUnit(BidMachineNetworkAdapter.KEY, params) {

    companion object {
        const val PARAM_TARGETING_PARAMS: String = "targeting_params"
        const val PARAM_NETWORKS: String = "networks"
        const val PARAM_CUSTOM_PARAMS: String = "custom_params"
        const val PARAM_PLACEMENT_ID: String = "placement_od"
        const val PARAM_BANNER_SIZE: String = "banner_size"
        const val PARAM_AD_CONTENT_TYPE: String = "ad_content_type"

        val DEFAULT_BANNER_SIZE: BannerSize = BannerSize.Size_320x50
        val DEFAULT_AD_CONTENT_TYPE: AdContentType = AdContentType.All
    }

    fun setTargetingParams(targetingParams: TargetingParams): BidMachineNetworkAdUnit {
        params[PARAM_TARGETING_PARAMS] = targetingParams
        return this
    }

    fun setNetworks(networkList: List<NetworkConfig>): BidMachineNetworkAdUnit {
        params[PARAM_NETWORKS] = networkList
        return this
    }

    fun setCustomParams(customParams: CustomParams): BidMachineNetworkAdUnit {
        params[PARAM_CUSTOM_PARAMS] = customParams
        return this
    }

    fun setPlacementId(placementId: String): BidMachineNetworkAdUnit {
        params[PARAM_PLACEMENT_ID] = placementId
        return this
    }

    fun setBannerSize(bannerSize: BannerSize): BidMachineNetworkAdUnit {
        params[PARAM_BANNER_SIZE] = bannerSize
        return this
    }

    fun setAdContentType(adContentType: AdContentType): BidMachineNetworkAdUnit {
        params[PARAM_AD_CONTENT_TYPE] = adContentType
        return this
    }

    internal fun getTargetingParams(): TargetingParams? = params[PARAM_TARGETING_PARAMS] as? TargetingParams

    internal fun getNetworks(): List<NetworkConfig>? {
        return if (params.containsKey(PARAM_NETWORKS)) {
            (params[PARAM_NETWORKS] as? List<*>)?.filterIsInstance(NetworkConfig::class.java)
        } else {
            null
        }
    }

    internal fun getCustomParams(): CustomParams? = params[PARAM_CUSTOM_PARAMS] as? CustomParams

    internal fun getPlacementId(): String? = params[PARAM_PLACEMENT_ID] as? String

    internal fun obtainBannerSize(): BannerSize = params[PARAM_BANNER_SIZE] as? BannerSize ?: DEFAULT_BANNER_SIZE

    internal fun obtainAdContentType(): AdContentType =
        params[PARAM_AD_CONTENT_TYPE] as? AdContentType ?: DEFAULT_AD_CONTENT_TYPE

}