package io.bidon.mediation.adapter.max

import com.applovin.mediation.MaxAdFormat
import io.bidon.mediation.sdk.Utils.filterIsInstance
import io.bidon.mediation.sdk.network.NetworkAdUnit

class MaxNetworkAdUnit : NetworkAdUnit {

    companion object {
        const val PARAM_AD_UNIT_ID: String = "ad_unit_id"
        const val PARAM_MAX_AD_FORMAT: String = "max_ad_format"
        const val PARAM_EXTRA_PARAMETERS: String = "extra_parameters"
        const val PARAM_LOCAL_EXTRA_PARAMETERS: String = "local_extra_parameters"
        const val PARAM_CUSTOM_DATA: String = "custom_data"
        const val PARAM_PLACEMENT: String = "placement"

        val DEFAULT_MAX_AD_FORMAT: MaxAdFormat = MaxAdFormat.BANNER
    }

    constructor(adUnitId: String) : this() {
        params[PARAM_AD_UNIT_ID] = adUnitId
    }

    constructor(params: MutableMap<String, Any> = mutableMapOf()) : super(MaxNetworkAdapter.KEY, params)

    fun setMaxAdFormat(maxAdFormat: MaxAdFormat): MaxNetworkAdUnit {
        params[PARAM_MAX_AD_FORMAT] = maxAdFormat
        return this
    }

    fun setExtraParameters(extraParameters: Map<String, String>): MaxNetworkAdUnit {
        params[PARAM_EXTRA_PARAMETERS] = extraParameters
        return this
    }

    fun setLocalExtraParameters(localExtraParameters: Map<String, Any>): MaxNetworkAdUnit {
        params[PARAM_LOCAL_EXTRA_PARAMETERS] = localExtraParameters
        return this
    }

    fun setCustomData(customData: String): MaxNetworkAdUnit {
        params[PARAM_CUSTOM_DATA] = customData
        return this
    }

    fun setPlacement(placement: String): MaxNetworkAdUnit {
        params[PARAM_PLACEMENT] = placement
        return this
    }

    internal fun getAdUnitId(): String? = params[PARAM_AD_UNIT_ID] as? String

    internal fun obtainMaxAdFormat(): MaxAdFormat = params[PARAM_MAX_AD_FORMAT] as? MaxAdFormat ?: DEFAULT_MAX_AD_FORMAT

    internal fun getExtraParameters(): Map<String, String>? {
        return if (params.containsKey(PARAM_EXTRA_PARAMETERS)) {
            (params[PARAM_EXTRA_PARAMETERS] as? Map<*, *>)?.filterIsInstance(String::class.java, String::class.java)
        } else {
            null
        }
    }

    internal fun getLocalExtraParameters(): Map<String, Any>? {
        return if (params.containsKey(PARAM_LOCAL_EXTRA_PARAMETERS)) {
            (params[PARAM_LOCAL_EXTRA_PARAMETERS] as? Map<*, *>)?.filterIsInstance(String::class.java, Any::class.java)
        } else {
            null
        }
    }

    internal fun getCustomData(): String? = params[PARAM_CUSTOM_DATA] as? String

    internal fun getPlacement(): String? = params[PARAM_PLACEMENT] as? String

}