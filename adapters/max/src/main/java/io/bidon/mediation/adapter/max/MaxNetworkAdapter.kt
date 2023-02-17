package io.bidon.mediation.adapter.max

import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.adobject.BannerAdObject
import io.bidon.mediation.sdk.adobject.InterstitialAdObject
import io.bidon.mediation.sdk.adobject.RewardedAdObject
import io.bidon.mediation.sdk.network.NetworkAdapter
import io.bidon.mediation.sdk.network.NetworkInitializeListener

class MaxNetworkAdapter : NetworkAdapter<MaxNetworkAdUnit>(KEY,
                                                           BuildConfig.ADAPTER_NETWORK_VERSION_NAME,
                                                           BuildConfig.ADAPTER_VERSION_NAME,
                                                           BuildConfig.ADAPTER_MIN_SDK_VERSION) {

    companion object {
        const val KEY = "Max"
    }

    override fun isNetworkInitialized(contextProvider: ContextProvider): Boolean =
        AppLovinSdk.getInstance(contextProvider.context).isInitialized

    override fun initializeNetwork(contextProvider: ContextProvider, listener: NetworkInitializeListener) {
        AppLovinSdk.getInstance(contextProvider.context).apply {
            mediationProvider = AppLovinMediationProvider.MAX
        }.initializeSdk {
            listener.onInitialized()
        }
    }

    override fun createBannerAdObject(networkAdUnit: MaxNetworkAdUnit): BannerAdObject =
        MaxBannerAdObject(networkAdUnit)

    override fun createInterstitialAdObject(networkAdUnit: MaxNetworkAdUnit): InterstitialAdObject =
        MaxInterstitialAdObject(networkAdUnit)

    override fun createRewardedAdObject(networkAdUnit: MaxNetworkAdUnit): RewardedAdObject =
        MaxRewardedAdObject(networkAdUnit)

}