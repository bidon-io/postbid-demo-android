package io.bidon.mediation.sdk

import io.bidon.mediation.sdk.network.NetworkAdUnit

data class AdUnitTransformResult<T>(val networkAdUnit: NetworkAdUnit, val result: T)