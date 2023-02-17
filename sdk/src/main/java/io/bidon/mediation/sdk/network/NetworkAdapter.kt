package io.bidon.mediation.sdk.network

import android.os.Build
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.Utils
import io.bidon.mediation.sdk.adobject.BannerAdObject
import io.bidon.mediation.sdk.adobject.InterstitialAdObject
import io.bidon.mediation.sdk.adobject.MediationError
import io.bidon.mediation.sdk.adobject.RewardedAdObject
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Ad network adapter for mediation.
 *
 * @param key of ad network. Must match [NetworkAdUnit.networkKey] from the corresponding network.
 */
abstract class NetworkAdapter<NetworkAdUnitType : NetworkAdUnit>(val key: String,
                                                                 private val networkVersion: String,
                                                                 private val adapterVersion: String,
                                                                 private val adapterMinSdkVersion: Int) {

    private val isInitializing: AtomicBoolean = AtomicBoolean(false)
    private val isInitialized: AtomicBoolean = AtomicBoolean(false)
    private val listenerSet: MutableSet<NetworkInitializeListener> = CopyOnWriteArraySet()

    /**
     * Returns True if ad network was already initialized.
     */
    open fun isNetworkInitialized(contextProvider: ContextProvider): Boolean {
        return false
    }

    /**
     * Creates banner ad object.
     */
    open fun createBannerAdObject(networkAdUnit: @UnsafeVariance NetworkAdUnitType): BannerAdObject? {
        return null
    }

    /**
     * Creates interstitial ad object.
     */
    open fun createInterstitialAdObject(networkAdUnit: @UnsafeVariance NetworkAdUnitType): InterstitialAdObject? {
        return null
    }

    /**
     * Creates rewarded ad object.
     */
    open fun createRewardedAdObject(networkAdUnit: @UnsafeVariance NetworkAdUnitType): RewardedAdObject? {
        return null
    }

    fun initialize(contextProvider: ContextProvider, listener: NetworkInitializeListener? = null) {
        if (Build.VERSION.SDK_INT < adapterMinSdkVersion) {
            listener?.onFailToInitialize(MediationError.internal("minSdkVersion is $adapterMinSdkVersion"))
            return
        }
        if (isInitialized(contextProvider)) {
            MediationLogger.log("Initialization result: Network - $key ($networkVersion, $adapterVersion), status - onInitialized")

            isInitialized.compareAndSet(false, true)
            listener?.onInitialized()
            return
        }
        if (listener != null) {
            listenerSet.add(listener)
        }
        if (!isInitializing.compareAndSet(false, true)) {
            return
        }
        MediationLogger.log("Initialize network for $key")

        Utils.onBackgroundThread {
            initializeNetwork(contextProvider, object : NetworkInitializeListener {
                override fun onInitialized() {
                    MediationLogger.log("Initialization result: Network - $key ($networkVersion, $adapterVersion), status - onInitialized")

                    sendOnInitialized()
                }

                override fun onFailToInitialize(error: MediationError) {
                    MediationLogger.error("Initialization result: Network - $key ($networkVersion, $adapterVersion), status - onFailToInitialize, error - $error")

                    sendOnInitialized(error)
                }
            })
        }
    }

    fun isInitialized(contextProvider: ContextProvider): Boolean =
        isInitialized.get() || isNetworkInitialized(contextProvider)

    protected abstract fun initializeNetwork(contextProvider: ContextProvider, listener: NetworkInitializeListener)

    private fun sendOnInitialized() {
        isInitialized.set(true)
        isInitializing.set(false)
        listenerSet.forEach {
            it.onInitialized()
        }
        listenerSet.clear()
    }

    private fun sendOnInitialized(error: MediationError) {
        isInitialized.set(false)
        isInitializing.set(false)
        listenerSet.forEach {
            it.onFailToInitialize(error)
        }
        listenerSet.clear()
    }

}