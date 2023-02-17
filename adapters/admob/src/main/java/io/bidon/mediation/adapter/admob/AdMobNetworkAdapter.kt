package io.bidon.mediation.adapter.admob

import com.google.android.gms.ads.MobileAds
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.Utils
import io.bidon.mediation.sdk.adobject.BannerAdObject
import io.bidon.mediation.sdk.adobject.InterstitialAdObject
import io.bidon.mediation.sdk.adobject.RewardedAdObject
import io.bidon.mediation.sdk.network.NetworkAdapter
import io.bidon.mediation.sdk.network.NetworkInitializeListener

class AdMobNetworkAdapter : NetworkAdapter<AdMobNetworkAdUnit>(KEY,
                                                               BuildConfig.ADAPTER_NETWORK_VERSION_NAME,
                                                               BuildConfig.ADAPTER_VERSION_NAME,
                                                               BuildConfig.ADAPTER_MIN_SDK_VERSION) {

    companion object {
        const val KEY = "AdMob"
    }

    override fun initializeNetwork(contextProvider: ContextProvider, listener: NetworkInitializeListener) {
        Utils.onUiThread {
            MobileAds.initialize(contextProvider.context) {
                listener.onInitialized()
            }
        }
    }

    override fun createBannerAdObject(networkAdUnit: AdMobNetworkAdUnit): BannerAdObject =
        AdMobBannerAdObject(networkAdUnit)

    override fun createInterstitialAdObject(networkAdUnit: AdMobNetworkAdUnit): InterstitialAdObject =
        AdMobInterstitialAdObject(networkAdUnit)

    override fun createRewardedAdObject(networkAdUnit: AdMobNetworkAdUnit): RewardedAdObject =
        AdMobRewardedAdObject(networkAdUnit)

}