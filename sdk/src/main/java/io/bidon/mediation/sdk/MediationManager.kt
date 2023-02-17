package io.bidon.mediation.sdk

import android.content.Context
import io.bidon.mediation.sdk.network.NetworkAdUnit
import io.bidon.mediation.sdk.network.NetworkAdapter
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicBoolean

object MediationManager {

    private val isInitializing = AtomicBoolean(false)
    private val isInitialized = AtomicBoolean(false)
    private val listenerSet: MutableSet<InitializeListener> = CopyOnWriteArraySet()

    /**
     * Initializes MediationManager.
     *
     * @param context Your context.
     */
    @JvmStatic
    @Synchronized
    fun initialize(context: Context) {
        initialize(context, null)
    }

    /**
     * Initializes MediationManager.
     *
     * @param context Your context.
     * @param listener Instance of [InitializeListener] to notify about the end of initialization.
     */
    @JvmStatic
    @Synchronized
    fun initialize(context: Context, listener: InitializeListener?) {
        if (isInitialized()) {
            listener?.onInitialized()
            return
        }
        if (listener != null) {
            listenerSet.add(listener)
        }
        if (!isInitializing.compareAndSet(false, true)) {
            return
        }
        MediationLogger.log("initialize")

        Utils.onBackgroundThread {
            NetworkManager.initializeAdNetworks(context, object : InitializeListener {
                override fun onInitialized() {
                    MediationManager.onInitialized()
                }
            })
        }
    }

    /**
     * Returns True if MediationManager was already initialized.
     */
    @JvmStatic
    fun isInitializing(): Boolean {
        return isInitializing.get()
    }

    /**
     * Returns True if MediationManager is in initialization state.
     */
    @JvmStatic
    fun isInitialized(): Boolean {
        return isInitialized.get()
    }

    /**
     * Registers ad network adapter for mediation.
     *
     * @param context        Your context.
     * @param networkAdapter Ad network adapter.
     */
    @JvmStatic
    fun <T : NetworkAdUnit> registerAdNetwork(context: Context,
                                              networkAdapter: NetworkAdapter<T>) {
        NetworkManager.registerAdNetwork(context, networkAdapter)
    }

    /**
     * Sets MediationManager logs enabled.
     *
     * @param isEnabled If `true` MediationManager will print all information.
     */
    @JvmStatic
    fun setLoggingEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            MediationLogger.isEnabled = true
            MediationLogger.log("setLoggingEnabled - true")
        } else {
            MediationLogger.log("setLoggingEnabled - false")
            MediationLogger.isEnabled = false
        }
    }

    private fun onInitialized() {
        MediationLogger.log("onInitialized")

        isInitialized.set(true)
        isInitializing.set(false)
        for (initializeListener in listenerSet) {
            initializeListener.onInitialized()
        }
        listenerSet.clear()
    }

}