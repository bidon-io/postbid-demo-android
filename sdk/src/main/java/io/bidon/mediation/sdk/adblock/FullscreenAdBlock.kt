package io.bidon.mediation.sdk.adblock

import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.adobject.FullscreenAdObject
import io.bidon.mediation.sdk.adobject.FullscreenAdObjectListener

abstract class FullscreenAdBlock<
        SelfType : FullscreenAdBlock<SelfType, AdBlockListenerType, AdObjectType, AdObjectListenerType>,
        AdBlockListenerType : FullscreenAdBlockListener<AdObjectType, SelfType>,
        AdObjectType : FullscreenAdObject<AdObjectListenerType>,
        AdObjectListenerType : FullscreenAdObjectListener<AdObjectType>> :
        BaseAdBlock<SelfType, AdBlockListenerType, AdObjectType, AdObjectListenerType>() {

    protected open inner class FullscreenListener : BaseAdObjectListener(), FullscreenAdObjectListener<AdObjectType> {

        override fun onAdClosed(adObject: AdObjectType) {
            MediationLogger.log(adObject.tag, "onAdClosed ($adObject)")

            listener?.onAdClosed(this@FullscreenAdBlock as SelfType, adObject.adInfo)
        }

    }

}