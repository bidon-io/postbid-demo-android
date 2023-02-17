package io.bidon.mediation.adapter.admob

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.rewarded.RewardItem
import io.bidon.mediation.sdk.adobject.MediationError
import io.bidon.mediation.sdk.adobject.Reward

internal object AdMobUtils {

    /**
     * Finds the first [AdMobLineItem] whose price is greater than the price floor.
     */
    fun Collection<AdMobLineItem>.findLineItem(priceFloor: Double?): AdMobLineItem? {
        if (isEmpty()) {
            return null
        }
        return sortedBy {
            it.price
        }.firstOrNull {
            priceFloor == null || it.price > priceFloor
        }
    }

    fun AdError.toMediationAdError(): MediationError = MediationError(code, message)

    fun RewardItem.toReward(): Reward = Reward(type, amount)

}