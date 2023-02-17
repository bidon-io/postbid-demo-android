package io.bidon.mediation.sdk.adblock

import android.content.Context
import androidx.annotation.CallSuper
import io.bidon.mediation.sdk.*
import io.bidon.mediation.sdk.Utils.generateTag
import io.bidon.mediation.sdk.adobject.*
import io.bidon.mediation.sdk.network.NetworkAdUnit
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseAdBlock<
        SelfType : BaseAdBlock<SelfType, AdBlockListenerType, AdObjectType, AdObjectListenerType>,
        AdBlockListenerType : AdBlockListener<AdObjectType, SelfType>,
        AdObjectType : AdObject<AdObjectListenerType>,
        AdObjectListenerType : AdObjectListener<AdObjectType>> :
        AdBlock<SelfType, AdBlockListenerType, AdObjectType> {

    private val isLoadCallbackReached = AtomicBoolean(false)
    private val adObjectInProgressCount = AtomicInteger(0)
    private val loadedAdObjectList: MutableList<AdObjectType> = CopyOnWriteArrayList()
    private val isTimeOutReached = AtomicBoolean(false)
    private val timeOutRunnable: Runnable by lazy {
        TimeOutRunnable(this)
    }

    val tag = generateTag("AdBlock")

    private var loadAdObjectList: List<AdObjectType>? = null

    override var listener: AdBlockListenerType? = null
    override var timeOutMs: Long = 0

    override fun load(context: Context,
                      adBlockParameters: AdBlockParameters,
                      networkAdUnitList: List<NetworkAdUnit>) {
        isLoadCallbackReached.set(false)

        val contextProvider = BaseContextProvider(context)
        loadAdObjectList = createAdObjectList(contextProvider, networkAdUnitList).also { transformResultList ->
            adObjectInProgressCount.set(transformResultList.size)
            if (transformResultList.isEmpty()) {
                onBlockLoadingCompleted()
                return
            }
            MediationLogger.log(tag,
                                "Load ad block, count - ${transformResultList.size}, with parameters: $adBlockParameters")

            startTimeOut()
            transformResultList.forEach { transformResult ->
                Utils.onBackgroundThread {
                    val adObject = transformResult.result
                    val adObjectParameters = AdObjectParameters(adBlockParameters.priceFloor)
                    val listener = createAdObjectListener()
                    adObject.adInfo = AdInfo(transformResult.networkAdUnit, adObjectParameters, null)
                    adObject.listener = listener

                    MediationLogger.log(adObject.tag, "Load ad (${adObject.getNetworkKey()})")

                    try {
                        adObject.load(contextProvider, adObjectParameters, listener)
                    } catch (t: Throwable) {
                        MediationLogger.throwable(t)

                        listener.onAdFailToLoad(adObject, MediationError.internal("Error during load call"))
                    }
                }
            }
        }.map {
            it.result
        }
    }

    override fun getLoadedAdObjectList(): List<AdObjectType> = loadedAdObjectList

    /**
     * Gets AdObject with highest price to show.
     */
    override fun getMostExpensiveAd(): AdObjectType? {
        loadedAdObjectList.sortByDescending {
            it.adInfo.price
        }
        return loadedAdObjectList.firstOrNull {
            it.canBeShown()
        }
    }

    override fun destroy() {
        MediationLogger.log(tag, "destroy")

        stopTimeOut()
        loadAdObjectList?.forEach {
            it.destroy()
        }
        loadAdObjectList = null
        loadedAdObjectList.forEach {
            it.destroy()
        }
        loadedAdObjectList.clear()
    }

    protected abstract fun createAdObjectList(contextProvider: ContextProvider,
                                              adUnitList: List<NetworkAdUnit>): List<AdUnitTransformResult<AdObjectType>>

    protected abstract fun createAdObjectListener(): AdObjectListenerType

    /**
     * Indicates that ad object is loaded.
     */
    private fun onAdObjectLoaded(adObject: AdObjectType, price: Double?) {
        adObject.setLoaded(true)
        adObject.adInfo.price = price

        MediationLogger.log(adObject.tag, "onAdLoaded ($adObject)")

        loadedAdObjectList.add(adObject)
        onAdObjectCompleted()
    }

    private fun onAdObjectFailToLoad(adObject: AdObjectType, error: MediationError) {
        MediationLogger.log(adObject.tag, "onAdFailToLoad (${adObject.getNetworkKey()}), with error - $error")

        onAdObjectCompleted()
    }

    private fun onAdObjectExpired(adObject: AdObjectType) {
        adObject.setLoaded(false)
        loadedAdObjectList.remove(adObject)

        MediationLogger.log(adObject.tag, "onAdExpired ($adObject)")

        listener?.onAdExpired(this@BaseAdBlock as SelfType, adObject.adInfo)
    }

    private fun onAdObjectCompleted() {
        if (isTimeOutReached.get()) {
            return
        }

        val adObjectInProgressCount = adObjectInProgressCount.decrementAndGet()
        if (adObjectInProgressCount <= 0) {
            onBlockLoadingCompleted()
        } else {
            MediationLogger.log(tag, "$adObjectInProgressCount ad object(s) left to load")
        }
    }

    private fun onBlockLoadingCompleted() {
        if (!isLoadCallbackReached.compareAndSet(false, true)) {
            return
        }
        stopTimeOut()
        val loadedCount = loadedAdObjectList.size
        if (loadedCount == 0) {
            MediationLogger.log(tag, "onAdFailToLoad")

            listener?.onAdBlockFailToLoad(this as SelfType)
        } else {
            MediationLogger.log(tag, "onAdLoaded, count of loaded object - $loadedCount")

            listener?.onAdBlockLoaded(this as SelfType)
        }
    }

    /**
     * Starts the time out task.
     */
    private fun startTimeOut() {
        isTimeOutReached.set(false)
        if (timeOutMs > 0) {
            Utils.onUiThread(timeOutMs, timeOutRunnable)
        }
    }

    /**
     * Stops the time out task.
     */
    private fun stopTimeOut() {
        Utils.cancelUiThreadTask(timeOutRunnable)
    }

    /**
     * Indicates that the task of time out is reached.
     */
    private fun onTimeOutReached() {
        MediationLogger.log(tag, "TimeOut is reached")

        isTimeOutReached.set(true)

        onBlockLoadingCompleted()
    }


    protected open inner class BaseAdObjectListener : AdObjectListener<AdObjectType> {

        @CallSuper
        override fun onAdLoaded(adObject: AdObjectType, price: Double?) {
            onAdObjectLoaded(adObject, price)
        }

        @CallSuper
        override fun onAdFailToLoad(adObject: AdObjectType, adError: MediationError) {
            onAdObjectFailToLoad(adObject, adError)
        }

        @CallSuper
        override fun onAdShown(adObject: AdObjectType) {
            adObject.setShown()

            MediationLogger.log(adObject.tag, "onAdShown ($adObject)")

            listener?.onAdShown(this@BaseAdBlock as SelfType, adObject.adInfo)
        }

        @CallSuper
        override fun onAdFailToShow(adObject: AdObjectType, adError: MediationError) {
            MediationLogger.error(adObject.tag, "onAdFailToShow (${adObject.getNetworkKey()}), error - $adError")

            listener?.onAdFailToShow(this@BaseAdBlock as SelfType, adObject.adInfo, adError)
        }

        @CallSuper
        override fun onAdClicked(adObject: AdObjectType) {
            MediationLogger.log(adObject.tag, "onAdClicked ($adObject)")

            listener?.onAdClicked(this@BaseAdBlock as SelfType, adObject.adInfo)
        }

        @CallSuper
        override fun onAdExpired(adObject: AdObjectType) {
            onAdObjectExpired(adObject)
        }

    }

    private class TimeOutRunnable(adBlock: BaseAdBlock<*, *, *, *>) : Runnable {

        private val weakAdBlock = WeakReference(adBlock)

        override fun run() {
            weakAdBlock.get()?.onTimeOutReached()
        }

    }

}