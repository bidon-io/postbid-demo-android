package io.bidon.mediation.sdk.adobject

import io.bidon.mediation.sdk.network.NetworkAdUnit

data class AdInfo(val networkAdUnit: NetworkAdUnit,
                  val adObjectParameters: AdObjectParameters,
                  var price: Double?) {

    override fun toString(): String {
        return "${networkAdUnit.networkKey}, price - $price"
    }

}