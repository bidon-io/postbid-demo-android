# BidOn Android Mediation Sdk

* [Overview](#overview)
* [Working with networks](#working-with-networks)
  * [Get network CPM](#get-network-cpm)
    * [Applovin](#applovin)
    * [BidMachine](#bidmachine)
  * [PostBid request](#postbid-request)
    * [AdMob](#admob)
    * [BidMachine](#bidmachine-1)
  * [Select ads to show](#select-ads-to-show)
* [Workflow](#workflow)
* [Configure 3rd party ad network](#configure-3rd-party-ad-network)
* [Banner](#banner)
* [Interstitial](#interstitial)
* [Rewarded](#rewarded)

# Overview

Postbid is a type of an integration that allows publishers to boost revenue by adding extra-layer of an auction after
usual meditation has finished its work. This auction is fully controlled by the publisher and is called postbid.
Whenever mediation provides an ad with a price - publisher is asking postbid partners if they are willing to pay more
for an impression opportunity.

Flow is the following:

1) Publisher requests an ad from mediation
2) Mediations provide an ad with some price
3) Publisher creates postbid auction across all postbid partners
4) All partners should be requested simultaneously within postbid auction
5) Publisher chooses the winner of the auction and winner’s ad should be shown as it’s the most expensive
6) If no postbid partners were able to provide an ad - publisher should show the ad that was provided by mediation

# Working with networks

## Get network CPM

### Applovin

```kotlin
override fun onAdLoaded(maxAd: MaxAd) {
    val price = maxAd.revenue * 1000
}
```

[*Example*](adapters/max/src/main/java/io/bidon/mediation/adapter/max/MaxInterstitialAdObject.kt#L57)

### BidMachine

```kotlin
override fun onAdLoaded(interstitialAd: InterstitialAd) {
    val price = interstitialAd.auctionResult?.price
}
```

[*Example*](adapters/bidmachine/src/main/java/io/bidon/mediation/adapter/bidmachine/BidMachineInterstitialAdObject.kt#L51)

## Postbid request

First you need to select the maximum price from the previously downloaded ads. Then make a request with this price.

### AdMob

1) Need to have ad units. Each ad unit is configured in the AdMob dashboard. For each ad unit, you need to set up an
   eCPM floor.
2) Find among all ad units that you use the one with a price that is greater or equal to final mediation price.
3) Make a request with this ad unit.
4) If this adUnit returned fill - it should be used in postbid auction

[*Example*](adapters/admob/src/main/java/io/bidon/mediation/adapter/admob/AdMobInterstitialAdObject.kt#L32)

### BidMachine

1) Request with price
2) If this adUnit returned fill - it should be used in postbid auction

[*Example*](adapters/bidmachine/src/main/java/io/bidon/mediation/adapter/bidmachine/BidMachineInterstitialAdObject.kt#L20)

## Select ads to show

After prebid and postbid auctions, you need to select the ad with the maximum price and show it.

```kotlin
override fun getMostExpensiveAd(): AdObjectType? { 
   loadedAdObjectList.sortByDescending {
      it.adInfo.price
   }
   return loadedAdObjectList.firstOrNull {
      it.canBeShown()
   }
}
```

[*Example*](sdk/src/main/java/io/bidon/mediation/sdk/adblock/BaseAdBlock.kt#L81)

# Workflow

1) Obtain configuration from own source.
2) Configure and initialize a 3rd party ad network. After the ad network has been initialized, crete mediation ad
   network adapter and registered it. Repeat if there are multiple networks.
3) Initialize the Mediation SDK.
4) Create and configure the ```MediationAd``` object.
5) Load the ```MediationAd``` object.
6) Show the ```MediationAd``` object when needed.

# Configure 3rd party ad network

```kotlin
private fun initializeBidMachine() {
    BidMachine.setLoggingEnabled(true)
    BidMachine.setTestMode(true)
    BidMachine.initialize(this, "YOUR_SOURCE_ID") {
        // Register Mediation BidMachine adapter
        MediationManager.registerAdNetwork(this, BidMachineNetworkAdapter())

        onNetworkInitialized()
    }
}

// Initialize MediationManager after initialize all 3rd party ad networks
private fun onNetworkInitialized() {
    MediationManager.setLoggingEnabled(true)
    MediationManager.initialize(this, object : InitializeListener {
        override fun onInitialized() {
            // Mediation SDK ready for work
        }
    })
}
```

# Banner

```kotlin
private var bannerMediationAdView: BannerMediationAdView? = null

private fun loadBanner() {
    bannerMediationAdView = BannerMediationAdView(this).apply {
        listener = BannerMediationListener()
        preBidTimeOutMs = 3000
        postBidTimeOutMs = 3000
        postBidPriceFloor = 1.0
        setPreBidNetworkAdUnit(listOf(MaxNetworkAdUnit("YOUR_MAX_AD_UNIT_ID")
                                              .setMaxAdFormat(MaxAdFormat.BANNER)))
        setPostBidNetworkAdUnit(listOf(AdMobNetworkAdUnit(YOUR_ADMOB_LINE_ITEMS)
                                               .setAdSize(AdSize.BANNER),
                                       BidMachineNetworkAdUnit()
                                               .setBannerSize(BannerSize.Size_320x50)))
        // Default - 15000 ms
        setAutoRefreshTime(10000)
        // Default - 2000 ms
        setRetryOnFailTime(1000)

        loadAd()
    }
}

private fun showBanner() {
    bannerMediationAdView?.takeIf {
        // Checking the possibility of showing mediation ad
        it.isLoadingCompleted() && it.canShowAd()
    }?.also {
        // Add bannerMediationAdView to container
        adContainer.addView(it)
    }
}

private fun destroyBanner() {
    // Destroy banner instance if not needed
    bannerMediationAdView?.destroy()
    bannerMediationAdView = null
}
```

# Interstitial

```kotlin
private var interstitialMediationAd: InterstitialMediationAd? = null

private fun loadInterstitial() {
    interstitialMediationAd = InterstitialMediationAd(this).apply {
        listener = InterstitialListener()
        preBidTimeOutMs = 3000
        postBidTimeOutMs = 3000
        postBidPriceFloor = 1.0
        setPreBidNetworkAdUnit(listOf(MaxNetworkAdUnit("YOUR_MAX_AD_UNIT_ID")))
        setPostBidNetworkAdUnit(listOf(AdMobNetworkAdUnit(YOUR_ADMOB_LINE_ITEMS),
                                       BidMachineNetworkAdUnit()
                                               .setAdContentType(AdContentType.All)))
        loadAd()
    }
}

private fun showInterstitial() {
    interstitialMediationAd?.takeIf {
        // Checking the possibility of showing mediation ad
        it.isLoadingCompleted() && it.canShowAd()
    }?.showAd()
}

private fun destroyInterstitial() {
    // Destroy interstitial instance if not needed
    interstitialMediationAd?.destroy()
    interstitialMediationAd = null
}
```

# Rewarded

```kotlin
private var rewardedMediationAd: RewardedMediationAd? = null

private fun loadRewarded() {
    rewardedMediationAd = RewardedMediationAd(this).apply {
        listener = RewardedMediationListener()
        preBidTimeOutMs = 3000
        postBidTimeOutMs = 3000
        postBidPriceFloor = 1.0
        setPreBidNetworkAdUnit(listOf(MaxNetworkAdUnit("YOUR_MAX_AD_UNIT_ID")))
        setPostBidNetworkAdUnit(listOf(AdMobNetworkAdUnit(YOUR_ADMOB_LINE_ITEMS),
                                       BidMachineNetworkAdUnit()))
        loadAd()
    }
}

private fun showRewarded() {
    rewardedMediationAd?.takeIf {
        // Checking the possibility of showing mediation ad
        it.isLoadingCompleted() && it.canShowAd()
    }?.showAd()
}

private fun destroyRewarded() {
    // Destroy rewarded instance if not needed
    rewardedMediationAd?.destroy()
    rewardedMediationAd = null
}
```
