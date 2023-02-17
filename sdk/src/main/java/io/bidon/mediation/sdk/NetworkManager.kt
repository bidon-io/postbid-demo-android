package io.bidon.mediation.sdk

import android.content.Context
import io.bidon.mediation.sdk.adobject.*
import io.bidon.mediation.sdk.network.NetworkAdUnit
import io.bidon.mediation.sdk.network.NetworkAdapter
import io.bidon.mediation.sdk.network.NetworkInitializeListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

internal object NetworkManager {

    private val networkAdapterMap: MutableMap<String, NetworkAdapter<out NetworkAdUnit>> = ConcurrentHashMap()
    private val initializedNetworkCount: AtomicInteger = AtomicInteger(0)

    @JvmStatic
    fun registerAdNetwork(context: Context,
                          networkAdapter: NetworkAdapter<out NetworkAdUnit>) {
        networkAdapterMap[networkAdapter.key] = networkAdapter
        if (MediationManager.isInitializing() || MediationManager.isInitialized()) {
            networkAdapter.initialize(BaseContextProvider(context.applicationContext))
        }
    }

    @JvmStatic
    fun initializeAdNetworks(context: Context, listener: InitializeListener) {
        val contextProvider = BaseContextProvider(context.applicationContext)
        val size = networkAdapterMap.size
        networkAdapterMap.values.forEach { networkAdapter ->
            networkAdapter.initialize(contextProvider, object : NetworkInitializeListener {
                override fun onInitialized() {
                    if (initializedNetworkCount.incrementAndGet() == size) {
                        listener.onInitialized()
                    }
                }

                override fun onFailToInitialize(error: MediationError) {
                    if (initializedNetworkCount.incrementAndGet() == size) {
                        listener.onInitialized()
                    }
                }
            })
        }
    }

    internal fun createBannerAdObjectList(contextProvider: ContextProvider,
                                          adUnitList: List<NetworkAdUnit>): List<AdUnitTransformResult<BannerAdObject>> {
        return adUnitList.toNetworkList(contextProvider) { adapter, adUnit ->
            adapter.createBannerAdObject(adUnit)
        }
    }

    internal fun createInterstitialAdObjectList(contextProvider: ContextProvider,
                                                adUnitList: List<NetworkAdUnit>): List<AdUnitTransformResult<InterstitialAdObject>> {
        return adUnitList.toNetworkList(contextProvider) { adapter, adUnit ->
            adapter.createInterstitialAdObject(adUnit)
        }
    }

    internal fun createRewardedAdObjectList(contextProvider: ContextProvider,
                                            adUnitList: List<NetworkAdUnit>): List<AdUnitTransformResult<RewardedAdObject>> {
        return adUnitList.toNetworkList(contextProvider) { adapter, adUnit ->
            adapter.createRewardedAdObject(adUnit)
        }
    }

    private fun <T : AdObject<*>> List<NetworkAdUnit>.toNetworkList(contextProvider: ContextProvider,
                                                                    transform: (NetworkAdapter<out NetworkAdUnit>, NetworkAdUnit) -> T?): List<AdUnitTransformResult<T>> {
        return mapNotNull {
            val networkAdapter = networkAdapterMap[it.networkKey]
            if (networkAdapter == null) {
                MediationLogger.error("${it.networkKey} ad unit exclude from mediation because of: Not found registered network")
                null
            } else if (!networkAdapter.isInitialized(contextProvider)) {
                MediationLogger.error("${it.networkKey} ad unit exclude from mediation because of: Network not initialized")
                null
            } else {
                try {
                    transform.invoke(networkAdapter, it)?.run {
                        AdUnitTransformResult(it, this)
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                    null
                }
            }
        }
    }

}