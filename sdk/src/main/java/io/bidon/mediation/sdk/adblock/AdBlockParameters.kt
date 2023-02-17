package io.bidon.mediation.sdk.adblock

data class AdBlockParameters(val priceFloor: Double?) {

    override fun toString(): String {
        return "priceFloor - $priceFloor"
    }

}