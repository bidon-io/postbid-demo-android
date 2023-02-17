package io.bidon.mediation.sdk.adobject

import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.Utils.generateTag
import java.util.concurrent.atomic.AtomicBoolean

abstract class AdObject<ListenerType : Any> {

    private val isLoaded = AtomicBoolean(false)
    private val isShown = AtomicBoolean(false)

    val tag = generateTag("AdObject")

    internal lateinit var adInfo: AdInfo
    internal lateinit var listener: ListenerType

    /**
     * Loads ad.
     */
    abstract fun load(contextProvider: ContextProvider,
                      adObjectParameters: AdObjectParameters,
                      listener: ListenerType)

    protected abstract fun canShow(): Boolean

    protected abstract fun onDestroy()

    fun isLoaded(): Boolean = isLoaded.get()

    fun isShown(): Boolean = isShown.get()

    fun canBeShown(): Boolean = isLoaded() && safeCanShow()

    fun destroy() {
        setLoaded(false)

        try {
            onDestroy()
        } catch (t: Throwable) {
            MediationLogger.throwable(t)
        }
    }

    internal fun setLoaded(value: Boolean) = isLoaded.set(value)

    internal fun setShown() = isShown.set(true)

    internal fun getNetworkKey(): String = adInfo.networkAdUnit.networkKey

    private fun safeCanShow(): Boolean {
        return try {
            canShow()
        } catch (t: Throwable) {
            MediationLogger.throwable(t)
            false
        }
    }

    override fun toString(): String = "$adInfo"

}