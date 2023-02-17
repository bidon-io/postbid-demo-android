package io.bidon.mediation.sdk.mediationad

import android.content.Context
import androidx.annotation.CallSuper
import io.bidon.mediation.sdk.AdHolder
import io.bidon.mediation.sdk.BaseContextProvider
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.Utils
import io.bidon.mediation.sdk.Utils.generateTag
import io.bidon.mediation.sdk.adblock.AdBlockListener
import io.bidon.mediation.sdk.adblock.AdBlockParameters
import io.bidon.mediation.sdk.adblock.BaseAdBlock
import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.AdObject
import io.bidon.mediation.sdk.adobject.MediationError
import io.bidon.mediation.sdk.network.NetworkAdUnit
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseMediationAd<
        SelfType : BaseMediationAd<SelfType, MediationAdListenerType, AdBlockType, AdObjectType>,
        MediationAdListenerType : MediationAdListener<SelfType>,
        AdBlockType : BaseAdBlock<AdBlockType, *, AdObjectType, *>,
        AdObjectType : AdObject<*>>(context: Context) :
        MediationAd<MediationAdListenerType>, AdHolder<AdObjectType> {

    private val isAdLoadCallbackReached = AtomicBoolean(false)
    private val preBidAdUnitList: MutableList<NetworkAdUnit> = mutableListOf()
    private val postBidAdUnitList: MutableList<NetworkAdUnit> = mutableListOf()

    protected val contextProvider = BaseContextProvider(context)

    val tag = generateTag("MediationAd")

    private var preBidAdBlock: AdBlockType? = null
    private var postBidAdBlock: AdBlockType? = null

    override var listener: MediationAdListenerType? = null
    override var preBidTimeOutMs: Long = 0
    override var postBidTimeOutMs: Long = 0
    override var postBidPriceFloor: Double? = null

    override fun setPreBidNetworkAdUnit(networkAdUnitList: List<NetworkAdUnit>) {
        preBidAdUnitList.clear()
        preBidAdUnitList.addAll(networkAdUnitList)
    }

    override fun setPostBidNetworkAdUnit(networkAdUnitList: List<NetworkAdUnit>) {
        postBidAdUnitList.clear()
        postBidAdUnitList.addAll(networkAdUnitList)
    }

    override fun getLoadedAdObjectList(): List<AdObjectType> {
        return preBidAdBlock?.getLoadedAdObjectList().orEmpty() + postBidAdBlock?.getLoadedAdObjectList().orEmpty()
    }

    override fun getLoadedAdObjectSize(): Int {
        val preBidSize = preBidAdBlock?.getLoadedAdObjectSize() ?: 0
        val postBidSize = postBidAdBlock?.getLoadedAdObjectSize() ?: 0
        return preBidSize + postBidSize
    }

    override fun getMostExpensiveAd(): AdObjectType? {
        return listOf(preBidAdBlock?.getMostExpensiveAd(),
                      postBidAdBlock?.getMostExpensiveAd())
                .maxByOrNull {
                    it?.adInfo?.price ?: 0.0
                }
    }

    override fun loadAd() {
        MediationLogger.log(tag, "loadAd")

        clearLoadedAdObject()
        this.preBidAdBlock = createPreBidAdBlock().apply {
            timeOutMs = preBidTimeOutMs
        }
        this.postBidAdBlock = createPostBidAdBlock().apply {
            timeOutMs = postBidTimeOutMs
        }
        loadPreBidAdBlock(contextProvider.context)
    }

    override fun isLoadingCompleted(): Boolean = isAdLoadCallbackReached.get()

    override fun canShowAd(): Boolean = getMostExpensiveAd() != null

    @CallSuper
    override fun destroy() {
        MediationLogger.log(tag, "destroy")

        listener = null
        preBidAdUnitList.clear()
        postBidAdUnitList.clear()
        clearLoadedAdObject()
    }

    protected abstract fun createPreBidAdBlock(): AdBlockType

    protected abstract fun createPostBidAdBlock(): AdBlockType

    private fun clearLoadedAdObject() {
        MediationLogger.log(tag, "Clear loaded ad objects")

        isAdLoadCallbackReached.set(false)
        preBidAdBlock?.destroy()
        preBidAdBlock = null
        postBidAdBlock?.destroy()
        postBidAdBlock = null
    }

    private fun loadPreBidAdBlock(context: Context) {
        MediationLogger.log(tag, "Load PreBid ad block")

        preBidAdBlock?.apply {
            load(context, AdBlockParameters(null), preBidAdUnitList)
        } ?: loadPostBidAdBlock(context, null)
    }

    private fun loadPostBidAdBlock(context: Context, priceFloor: Double?) {
        MediationLogger.log(tag, "Load PostBid ad block")

        postBidAdBlock?.apply {
            load(context,
                 AdBlockParameters(postBidPriceFloor ?: priceFloor),
                 postBidAdUnitList)
        } ?: onMediationAdLoadingCompleted()
    }

    /**
     * Indicates that loading of all ad object is completed.
     */
    private fun onMediationAdLoadingCompleted() {
        if (!isAdLoadCallbackReached.compareAndSet(false, true)) {
            return
        }
        val loadedCount = getLoadedAdObjectSize()
        if (loadedCount == 0) {
            MediationLogger.log(tag, "onAdFailToLoad")

            Utils.onUiThread {
                listener?.onAdFailToLoad(this as SelfType)
            }
        } else {
            MediationLogger.log(tag, "onAdLoaded, count of loaded object - $loadedCount")

            Utils.onUiThread {
                listener?.onAdLoaded(this as SelfType)
            }
        }
    }


    protected open inner class PreBidAdBlockListener : BaseAdBlockListener() {

        override fun onAdBlockLoaded(adBlock: AdBlockType) {
            loadPostBidAdBlock(contextProvider.context, adBlock.getMostExpensiveAd()?.adInfo?.price)
        }

        override fun onAdBlockFailToLoad(adBlock: AdBlockType) {
            loadPostBidAdBlock(contextProvider.context, null)
        }

    }

    protected open inner class PostBidAdBlockListener : BaseAdBlockListener() {

        override fun onAdBlockLoaded(adBlock: AdBlockType) {
            onMediationAdLoadingCompleted()
        }

        override fun onAdBlockFailToLoad(adBlock: AdBlockType) {
            onMediationAdLoadingCompleted()
        }

    }

    protected abstract inner class BaseAdBlockListener : AdBlockListener<AdObjectType, AdBlockType> {

        override fun onAdShown(adBlock: AdBlockType, adInfo: AdInfo) {
            MediationLogger.log(adBlock.tag, "onAdShown ($adInfo)")

            Utils.onUiThread {
                listener?.onAdShown(this@BaseMediationAd as SelfType, adInfo)
            }
        }

        override fun onAdFailToShow(adBlock: AdBlockType, adInfo: AdInfo, adError: MediationError) {
            MediationLogger.error(adBlock.tag, "onAdFailToShow (${adInfo.networkAdUnit.networkKey}), error - $adError")

            Utils.onUiThread {
                listener?.onAdFailToShow(this@BaseMediationAd as SelfType, adError)
            }
        }

        override fun onAdClicked(adBlock: AdBlockType, adInfo: AdInfo) {
            MediationLogger.log(adBlock.tag, "onAdClicked ($adInfo)")

            Utils.onUiThread {
                listener?.onAdClicked(this@BaseMediationAd as SelfType, adInfo)
            }
        }

        override fun onAdExpired(adBlock: AdBlockType, adInfo: AdInfo) {
            MediationLogger.log(adBlock.tag, "onAdExpired ($adInfo)")

            Utils.onUiThread {
                listener?.onAdExpired(this@BaseMediationAd as SelfType, adInfo)
            }
        }

    }

}