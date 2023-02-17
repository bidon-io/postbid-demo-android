package io.bidon.mediation.sdk.adobject

import android.view.View

abstract class ViewAdObject<ListenerType : Any> : AdObject<ListenerType>() {

    /**
     * Gets loaded ad view.
     */
    abstract fun getView(): View?

}