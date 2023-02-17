package io.bidon.mediation.sdk.adobject

data class Reward(val currency: String, val value: Int) {

    constructor() : this("", 0)

}