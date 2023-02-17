package io.bidon.mediation.sdk.adobject

import io.bidon.mediation.sdk.ContextProvider

abstract class FullscreenAdObject<ListenerType : Any> : AdObject<ListenerType>() {

    /**
     * Shows loaded ad.
     */
    abstract fun show(contextProvider: ContextProvider, listener: ListenerType)

}