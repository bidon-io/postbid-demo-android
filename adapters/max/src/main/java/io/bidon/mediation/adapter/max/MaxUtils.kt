package io.bidon.mediation.adapter.max

import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.ads.MaxRewardedAd
import io.bidon.mediation.sdk.adobject.MediationError
import io.bidon.mediation.sdk.adobject.Reward

internal object MaxUtils {

    /**
     * Obtains revenue and then converting it to CPM.
     */
    fun MaxAd.getCPM(): Double = revenue * 1000

    fun MaxError.toMediationAdError(): MediationError = MediationError(code, message)

    fun MaxReward.toReward(): Reward = Reward(label, amount)

    fun MaxAdView.setExtraParameters(extraParameters: Map<String, String>?) {
        if (extraParameters.isNullOrEmpty()) {
            return
        }
        extraParameters.forEach {
            setExtraParameter(it.key, it.value)
        }
    }

    fun MaxAdView.setLocalExtraParameters(localExtraParameters: Map<String, Any>?) {
        if (localExtraParameters.isNullOrEmpty()) {
            return
        }
        localExtraParameters.forEach {
            setLocalExtraParameter(it.key, it.value)
        }
    }

    fun MaxInterstitialAd.setExtraParameters(extraParameters: Map<String, String>?) {
        if (extraParameters.isNullOrEmpty()) {
            return
        }
        extraParameters.forEach {
            setExtraParameter(it.key, it.value)
        }
    }

    fun MaxInterstitialAd.setLocalExtraParameters(localExtraParameters: Map<String, Any>?) {
        if (localExtraParameters.isNullOrEmpty()) {
            return
        }
        localExtraParameters.forEach {
            setLocalExtraParameter(it.key, it.value)
        }
    }

    fun MaxRewardedAd.setExtraParameters(extraParameters: Map<String, String>?) {
        if (extraParameters.isNullOrEmpty()) {
            return
        }
        extraParameters.forEach {
            setExtraParameter(it.key, it.value)
        }
    }

    fun MaxRewardedAd.setLocalExtraParameters(localExtraParameters: Map<String, Any>?) {
        if (localExtraParameters.isNullOrEmpty()) {
            return
        }
        localExtraParameters.forEach {
            setLocalExtraParameter(it.key, it.value)
        }
    }

}