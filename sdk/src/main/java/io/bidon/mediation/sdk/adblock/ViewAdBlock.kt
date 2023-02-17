package io.bidon.mediation.sdk.adblock

import io.bidon.mediation.sdk.adobject.AdObjectListener
import io.bidon.mediation.sdk.adobject.ViewAdObject

abstract class ViewAdBlock<
        SelfType : BaseAdBlock<SelfType, AdBlockListenerType, AdObjectType, AdObjectListenerType>,
        AdBlockListenerType : AdBlockListener<AdObjectType, SelfType>,
        AdObjectType : ViewAdObject<AdObjectListenerType>,
        AdObjectListenerType : AdObjectListener<AdObjectType>> :
        BaseAdBlock<SelfType, AdBlockListenerType, AdObjectType, AdObjectListenerType>()