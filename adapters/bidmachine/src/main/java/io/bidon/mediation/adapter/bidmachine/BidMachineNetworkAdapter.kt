package io.bidon.mediation.adapter.bidmachine

import io.bidmachine.BidMachine
import io.bidon.mediation.sdk.ContextProvider
import io.bidon.mediation.sdk.adobject.BannerAdObject
import io.bidon.mediation.sdk.adobject.InterstitialAdObject
import io.bidon.mediation.sdk.adobject.MediationError
import io.bidon.mediation.sdk.adobject.RewardedAdObject
import io.bidon.mediation.sdk.network.NetworkAdapter
import io.bidon.mediation.sdk.network.NetworkInitializeListener

class BidMachineNetworkAdapter(private val sellerId: String? = null) :
        NetworkAdapter<BidMachineNetworkAdUnit>(KEY,
                                                BuildConfig.ADAPTER_NETWORK_VERSION_NAME,
                                                BuildConfig.ADAPTER_VERSION_NAME,
                                                BuildConfig.ADAPTER_MIN_SDK_VERSION) {

    companion object {
        const val KEY = "BidMachine"
    }

    override fun isNetworkInitialized(contextProvider: ContextProvider): Boolean = BidMachine.isInitialized()

    override fun initializeNetwork(contextProvider: ContextProvider, listener: NetworkInitializeListener) {
        if (sellerId.isNullOrEmpty()) {
            listener.onFailToInitialize(MediationError.invalidParameter("SellerId is null or empty"))
            return
        }
        BidMachine.initialize(contextProvider.context, sellerId) {
            listener.onInitialized()
        }
    }

    override fun createBannerAdObject(networkAdUnit: BidMachineNetworkAdUnit): BannerAdObject =
        BidMachineBannerAdObject(networkAdUnit)

    override fun createInterstitialAdObject(networkAdUnit: BidMachineNetworkAdUnit): InterstitialAdObject =
        BidMachineInterstitialAdObject(networkAdUnit)

    override fun createRewardedAdObject(networkAdUnit: BidMachineNetworkAdUnit): RewardedAdObject =
        BidMachineRewardedAdObject(networkAdUnit)

}