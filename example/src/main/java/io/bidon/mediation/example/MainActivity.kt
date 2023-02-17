package io.bidon.mediation.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAdFormat
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import io.bidmachine.AdContentType
import io.bidmachine.BidMachine
import io.bidmachine.banner.BannerSize
import io.bidon.mediation.adapter.admob.AdMobNetworkAdUnit
import io.bidon.mediation.adapter.admob.AdMobNetworkAdapter
import io.bidon.mediation.adapter.bidmachine.BidMachineNetworkAdUnit
import io.bidon.mediation.adapter.bidmachine.BidMachineNetworkAdapter
import io.bidon.mediation.adapter.max.MaxNetworkAdUnit
import io.bidon.mediation.adapter.max.MaxNetworkAdapter
import io.bidon.mediation.example.databinding.ActivityMainBinding
import io.bidon.mediation.sdk.InitializeListener
import io.bidon.mediation.sdk.MediationManager
import io.bidon.mediation.sdk.Utils
import io.bidon.mediation.sdk.Utils.safeAddView
import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.MediationError
import io.bidon.mediation.sdk.adobject.Reward
import io.bidon.mediation.sdk.mediationad.*
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    private var initializingNetworkCount: AtomicInteger = AtomicInteger(0)

    private lateinit var binding: ActivityMainBinding

    private var bannerMediationAdView: BannerMediationAdView? = null
    private var interstitialMediationAd: InterstitialMediationAd? = null
    private var rewardedMediationAd: RewardedMediationAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)

            bInitialize.setOnClickListener {
                initializeSdk()
            }
            bOpenAutoRefreshActivity.setOnClickListener {
                BannerActivity.createIntent(this@MainActivity).also { intent ->
                    startActivity(intent)
                }
            }
            bLoadBanner.setOnClickListener {
                loadBanner()
            }
            bShowBanner.setOnClickListener {
                showBanner()
            }
            bLoadInterstitial.setOnClickListener {
                loadInterstitial()
            }
            bShowInterstitial.setOnClickListener {
                showInterstitial()
            }
            bLoadRewarded.setOnClickListener {
                loadRewarded()
            }
            bShowRewarded.setOnClickListener {
                showRewarded()
            }
        }
    }

    private fun enableButtons() {
        binding.bOpenAutoRefreshActivity.isEnabled = true
        binding.bLoadBanner.isEnabled = true
        binding.bLoadInterstitial.isEnabled = true
        binding.bLoadRewarded.isEnabled = true
    }

    private fun initializeSdk() {
        binding.bInitialize.isEnabled = false

        collectNetworks().also { collection ->
            initializingNetworkCount.set(collection.size)

            collection.forEach {
                it.invoke()
            }
        }
    }

    private fun collectNetworks(): List<() -> Unit> {
        return listOf({ initializeAdMob() },
                      { initializeBidMachine() },
                      { initializeMax() })
    }

    private fun initializeAdMob() {
        MobileAds.initialize(this) {
            // Register Mediation AdMob adapter
            MediationManager.registerAdNetwork(this, AdMobNetworkAdapter())

            onNetworkInitialized()
        }
    }

    private fun initializeBidMachine() {
        BidMachine.setLoggingEnabled(true)
        BidMachine.setTestMode(true)
        BidMachine.initialize(this, getString(R.string.bid_machine_source_id)) {
            // Register Mediation BidMachine adapter
            MediationManager.registerAdNetwork(this, BidMachineNetworkAdapter())

            onNetworkInitialized()
        }
    }

    private fun initializeMax() {
        AppLovinSdk.getInstance(this).apply {
            mediationProvider = AppLovinMediationProvider.MAX
        }.initializeSdk {
            // Register Mediation MAX adapter
            MediationManager.registerAdNetwork(this, MaxNetworkAdapter())

            onNetworkInitialized()
        }
    }

    private fun onNetworkInitialized() {
        if (initializingNetworkCount.decrementAndGet() > 0) {
            return
        }

        MediationManager.setLoggingEnabled(true)
        MediationManager.initialize(this, object : InitializeListener {
            override fun onInitialized() {
                Utils.onUiThread {
                    enableButtons()
                }
            }
        })
    }

    private fun loadBanner() {
        binding.bShowBanner.isEnabled = false

        destroyBanner()
        bannerMediationAdView = BannerMediationAdView(this).apply {
            listener = BannerMediationListener()
            setPreBidNetworkAdUnit(listOf(MaxNetworkAdUnit(getString(R.string.max_banner_ad_unit_id))
                                                  .setMaxAdFormat(MaxAdFormat.BANNER)))
            setPostBidNetworkAdUnit(listOf(AdMobNetworkAdUnit(Params.AdMob.ADMOB_BANNER_LINE_ITEMS)
                                                   .setAdSize(AdSize.BANNER),
                                           BidMachineNetworkAdUnit()
                                                   .setBannerSize(BannerSize.Size_320x50)))
            loadAd()
        }
    }

    private fun showBanner() {
        binding.bShowBanner.isEnabled = false

        bannerMediationAdView?.takeIf {
            it.isLoadingCompleted() && it.canShowAd()
        }?.also {
            binding.adContainer.safeAddView(it)
        }
    }

    private fun destroyBanner() {
        bannerMediationAdView?.destroy()
        bannerMediationAdView = null
    }

    private fun loadInterstitial() {
        binding.bShowInterstitial.isEnabled = false

        destroyInterstitial()
        interstitialMediationAd = InterstitialMediationAd(this).apply {
            listener = InterstitialListener()
            setPreBidNetworkAdUnit(listOf(MaxNetworkAdUnit(getString(R.string.max_interstitial_ad_unit_id))))
            setPostBidNetworkAdUnit(listOf(AdMobNetworkAdUnit(Params.AdMob.ADMOB_INTERSTITIAL_LINE_ITEMS),
                                           BidMachineNetworkAdUnit()
                                                   .setAdContentType(AdContentType.All)))
            loadAd()
        }
    }

    private fun showInterstitial() {
        binding.bShowInterstitial.isEnabled = false

        interstitialMediationAd?.takeIf {
            it.isLoadingCompleted() && it.canShowAd()
        }?.showAd()
    }

    private fun destroyInterstitial() {
        interstitialMediationAd?.destroy()
        interstitialMediationAd = null
    }

    private fun loadRewarded() {
        binding.bShowRewarded.isEnabled = false

        destroyRewarded()
        rewardedMediationAd = RewardedMediationAd(this).apply {
            listener = RewardedMediationListener()
            setPreBidNetworkAdUnit(listOf(MaxNetworkAdUnit(getString(R.string.max_rewarded_ad_unit_id))))
            setPostBidNetworkAdUnit(listOf(AdMobNetworkAdUnit(Params.AdMob.ADMOB_REWARDED_LINE_ITEMS),
                                           BidMachineNetworkAdUnit()))
            loadAd()
        }
    }

    private fun showRewarded() {
        binding.bShowRewarded.isEnabled = false

        rewardedMediationAd?.takeIf {
            it.isLoadingCompleted() && it.canShowAd()
        }?.showAd()
    }

    private fun destroyRewarded() {
        rewardedMediationAd?.destroy()
        rewardedMediationAd = null
    }


    private inner class BannerMediationListener : BannerMediationAdViewListener {

        override fun onAdLoaded(mediationAd: BannerMediationAdView) {
            binding.bShowBanner.isEnabled = true

            Toast.makeText(this@MainActivity, "Banner onAdWrapperLoaded", Toast.LENGTH_SHORT).show()
        }

        override fun onAdFailToLoad(mediationAd: BannerMediationAdView) {
            Toast.makeText(this@MainActivity, "Banner onAdWrapperFailToLoad", Toast.LENGTH_SHORT).show()
        }

        override fun onAdFailToShow(mediationAd: BannerMediationAdView, adError: MediationError) {
            binding.bShowBanner.isEnabled = false

            Toast.makeText(this@MainActivity, "Banner onAdFailToShow", Toast.LENGTH_SHORT).show()
        }

        override fun onAdShown(mediationAd: BannerMediationAdView, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Banner onAdShown", Toast.LENGTH_SHORT).show()
        }

        override fun onAdClicked(mediationAd: BannerMediationAdView, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Banner onAdClicked", Toast.LENGTH_SHORT).show()
        }

        override fun onAdExpired(mediationAd: BannerMediationAdView, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Banner onAdExpired", Toast.LENGTH_SHORT).show()
        }

        override fun onAdExpanded(mediationAd: BannerMediationAdView, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Banner expanded", Toast.LENGTH_SHORT).show()
        }

        override fun onAdCollapsed(mediationAd: BannerMediationAdView, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Banner collapsed", Toast.LENGTH_SHORT).show()
        }

    }

    private inner class InterstitialListener : InterstitialMediationAdListener {

        override fun onAdLoaded(mediationAd: InterstitialMediationAd) {
            binding.bShowInterstitial.isEnabled = true

            Toast.makeText(this@MainActivity, "Interstitial onAdWrapperLoaded", Toast.LENGTH_SHORT).show()
        }

        override fun onAdFailToLoad(mediationAd: InterstitialMediationAd) {
            Toast.makeText(this@MainActivity, "Interstitial onAdWrapperFailToLoad", Toast.LENGTH_SHORT).show()
        }

        override fun onAdFailToShow(mediationAd: InterstitialMediationAd, adError: MediationError) {
            binding.bShowInterstitial.isEnabled = false

            Toast.makeText(this@MainActivity, "Interstitial onAdFailToShow", Toast.LENGTH_SHORT).show()
        }

        override fun onAdShown(mediationAd: InterstitialMediationAd, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Interstitial onAdShown", Toast.LENGTH_SHORT).show()
        }

        override fun onAdClicked(mediationAd: InterstitialMediationAd, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Interstitial onAdClicked", Toast.LENGTH_SHORT).show()
        }

        override fun onAdClosed(mediationAd: InterstitialMediationAd, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Interstitial onAdClosed", Toast.LENGTH_SHORT).show()
        }

        override fun onAdExpired(mediationAd: InterstitialMediationAd, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Interstitial onAdExpired", Toast.LENGTH_SHORT).show()
        }

    }

    private inner class RewardedMediationListener : RewardedMediationAdListener {

        override fun onAdLoaded(mediationAd: RewardedMediationAd) {
            binding.bShowRewarded.isEnabled = true

            Toast.makeText(this@MainActivity, "Rewarded onAdWrapperLoaded", Toast.LENGTH_SHORT).show()
        }

        override fun onAdFailToLoad(mediationAd: RewardedMediationAd) {
            Toast.makeText(this@MainActivity, "Rewarded onAdWrapperFailToLoad", Toast.LENGTH_SHORT).show()
        }

        override fun onAdFailToShow(mediationAd: RewardedMediationAd, adError: MediationError) {
            binding.bShowRewarded.isEnabled = false

            Toast.makeText(this@MainActivity, "Rewarded onAdFailToShow", Toast.LENGTH_SHORT).show()
        }

        override fun onAdShown(mediationAd: RewardedMediationAd, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Rewarded onAdShown", Toast.LENGTH_SHORT).show()
        }

        override fun onAdClicked(mediationAd: RewardedMediationAd, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Rewarded onAdClicked", Toast.LENGTH_SHORT).show()
        }

        override fun onAdClosed(mediationAd: RewardedMediationAd, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Rewarded onAdClosed", Toast.LENGTH_SHORT).show()
        }

        override fun onAdExpired(mediationAd: RewardedMediationAd, adInfo: AdInfo) {
            Toast.makeText(this@MainActivity, "Rewarded onAdExpired", Toast.LENGTH_SHORT).show()
        }

        override fun onAdRewarded(mediationAd: RewardedMediationAd, adInfo: AdInfo, reward: Reward) {
            Toast.makeText(this@MainActivity, "Rewarded onAdRewarded", Toast.LENGTH_SHORT).show()
        }

    }

}