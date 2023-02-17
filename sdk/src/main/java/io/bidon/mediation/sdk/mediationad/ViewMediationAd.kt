package io.bidon.mediation.sdk.mediationad

import android.content.Context
import android.view.View
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.adblock.BaseAdBlock
import io.bidon.mediation.sdk.adobject.AdObjectListener
import io.bidon.mediation.sdk.adobject.ViewAdObject

abstract class ViewMediationAd<
        SelfType : BaseMediationAd<SelfType, MediationAdListenerType, AdBlockType, AdObjectType>,
        MediationAdListenerType : MediationAdListener<SelfType>,
        AdBlockType : BaseAdBlock<AdBlockType, *, AdObjectType, AdObjectListenerType>,
        AdObjectType : ViewAdObject<AdObjectListenerType>,
        AdObjectListenerType : AdObjectListener<AdObjectType>>(context: Context) :
        BaseMediationAd<SelfType, MediationAdListenerType, AdBlockType, AdObjectType>(context) {

    /**
     * Gets ad object with highest price.
     */
    fun getView(): View? {
        return getMostExpensiveAd()?.let {
            MediationLogger.log(tag, "getView ($it)")

            it.getView()
        }
    }

}