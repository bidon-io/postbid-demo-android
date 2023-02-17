package io.bidon.mediation.adapter.bidmachine

import io.bidmachine.PriceFloorParams
import io.bidmachine.models.RequestBuilder
import io.bidmachine.utils.BMError
import io.bidon.mediation.sdk.adobject.MediationError

internal object BidMachineUtils {

    fun Double.toPriceFloorParams(): PriceFloorParams? = PriceFloorParams().addPriceFloor(this)

    fun BMError.toMediationAdError(): MediationError = MediationError(code, message)

    fun <T : RequestBuilder<*, *>> fillAdRequest(requestBuilder: T, networkAdUnit: BidMachineNetworkAdUnit): T {
        requestBuilder.setTargetingParams(networkAdUnit.getTargetingParams())
        networkAdUnit.getNetworks()?.also {
            requestBuilder.setNetworks(it)
        }
        requestBuilder.setCustomParams(networkAdUnit.getCustomParams())
        requestBuilder.setPlacementId(networkAdUnit.getPlacementId())
        return requestBuilder
    }

}