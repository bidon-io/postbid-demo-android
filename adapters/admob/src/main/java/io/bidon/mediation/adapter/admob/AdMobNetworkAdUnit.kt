package io.bidon.mediation.adapter.admob

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import io.bidon.mediation.sdk.network.NetworkAdUnit

class AdMobNetworkAdUnit : NetworkAdUnit {

    companion object {
        const val PARAM_LINE_ITEMS: String = "line_items"
        const val PARAM_AD_REQUEST: String = "ad_request"
        const val PARAM_AD_SIZE: String = "ad_size"

        val DEFAULT_AD_SIZE: AdSize = AdSize.BANNER
    }

    constructor(lineItems: Collection<AdMobLineItem>) : this() {
        params[PARAM_LINE_ITEMS] = lineItems
    }

    constructor(params: MutableMap<String, Any> = mutableMapOf()) : super(AdMobNetworkAdapter.KEY, params)

    fun setAdRequest(adRequest: AdRequest): AdMobNetworkAdUnit {
        params[PARAM_AD_REQUEST] = adRequest
        return this
    }

    fun setAdSize(adSize: AdSize): AdMobNetworkAdUnit {
        params[PARAM_AD_SIZE] = adSize
        return this
    }

    internal fun getLineItems(): Collection<AdMobLineItem>? {
        return if (params.containsKey(PARAM_LINE_ITEMS)) {
            (params[PARAM_LINE_ITEMS] as? Collection<*>)?.filterIsInstance(AdMobLineItem::class.java)
        } else {
            null
        }
    }

    internal fun obtainAdRequest(): AdRequest = params[PARAM_AD_REQUEST] as? AdRequest ?: AdRequest.Builder().build()

    internal fun obtainAdSize(): AdSize = params[PARAM_AD_SIZE] as? AdSize ?: DEFAULT_AD_SIZE

}