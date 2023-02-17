package io.bidon.mediation.sdk.network

import io.bidon.mediation.sdk.adobject.MediationError

interface NetworkInitializeListener {

    fun onInitialized()

    fun onFailToInitialize(error: MediationError)

}