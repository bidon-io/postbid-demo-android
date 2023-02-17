package io.bidon.mediation.sdk.mediationad

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.IntRange
import io.bidon.mediation.sdk.MediationLogger
import io.bidon.mediation.sdk.Utils
import io.bidon.mediation.sdk.Utils.generateTag
import io.bidon.mediation.sdk.Utils.safeAddView
import io.bidon.mediation.sdk.adobject.AdInfo
import io.bidon.mediation.sdk.adobject.MediationError
import io.bidon.mediation.sdk.network.NetworkAdUnit
import java.lang.ref.WeakReference

class BannerMediationAdView : FrameLayout, MediationAd<BannerMediationAdViewListener> {

    companion object {
        private const val DEFAULT_REFRESH_TIME_MS: Long = 15000
        private const val DEFAULT_RETRY_ON_FAIL_TIME_MS: Long = 2000
    }

    private val tag = generateTag("MediationAdView")
    private val preBidAdUnitList: MutableList<NetworkAdUnit> = mutableListOf()
    private val postBidAdUnitList: MutableList<NetworkAdUnit> = mutableListOf()
    private val showRunnable = ShowRunnable(this)
    private val retryLoadRunnable = RetryLoadRunnable(this)
    private val internalLoadListener = InternalListenerMediation(this)

    private var pendingBannerMediationAd: BannerMediationAd? = null
    private var currentBannerMediationAd: BannerMediationAd? = null
    private var refreshTimeMs: Long = DEFAULT_REFRESH_TIME_MS
    private var retryOnFailTimeMs: Long = DEFAULT_RETRY_ON_FAIL_TIME_MS
    private var isAdLoaded = false
    private var isShowPending = false
    private var isRefreshStarted = true

    override var listener: BannerMediationAdViewListener? = null
    override var preBidTimeOutMs: Long = 0
    override var postBidTimeOutMs: Long = 0
    override var postBidPriceFloor: Double? = null

    constructor(context: Context) :
            super(context)

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)

        if (getVisibility() == VISIBLE) {
            showAd()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        showAd()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        isShowPending = true
    }

    override fun setPreBidNetworkAdUnit(networkAdUnitList: List<NetworkAdUnit>) {
        preBidAdUnitList.clear()
        preBidAdUnitList.addAll(networkAdUnitList)
    }

    override fun setPostBidNetworkAdUnit(networkAdUnitList: List<NetworkAdUnit>) {
        postBidAdUnitList.clear()
        postBidAdUnitList.addAll(networkAdUnitList)
    }

    override fun isLoadingCompleted(): Boolean = isAdLoaded

    override fun loadAd() {
        MediationLogger.log(tag, "loadAd")

        isShowPending = true
        stopRetryLoadRunnable()
        loadBannerAd()
    }

    override fun canShowAd(): Boolean = pendingBannerMediationAd?.canShowAd() == true

    override fun destroy() {
        MediationLogger.log(tag, "destroy")

        stopAutoRefresh()
        removeAllViews()
        setCurrentBannerAd(null)
        pendingBannerMediationAd?.destroy()
        pendingBannerMediationAd = null
    }

    /**
     * Sets auto-refresh time in milliseconds.
     */
    fun setAutoRefreshTime(@IntRange(from = 1) refreshTimeMs: Long) {
        if (refreshTimeMs > 0) {
            MediationLogger.log(tag, "setAutoRefreshTime - $refreshTimeMs ms")

            this.refreshTimeMs = refreshTimeMs
        }
    }

    fun setRetryOnFailTime(@IntRange(from = 1) retryOnFailTimeMs: Long) {
        if (retryOnFailTimeMs > 0) {
            MediationLogger.log(tag, "setRetryOnFailTime - $retryOnFailTimeMs ms")

            this.retryOnFailTimeMs = retryOnFailTimeMs
        }
    }

    /**
     * Stops auto-refreshing.
     */
    fun stopAutoRefresh() {
        if (!isRefreshStarted) {
            return
        }
        MediationLogger.log(tag, "stopAutoRefresh")

        isRefreshStarted = false
        Utils.cancelUiThreadTask(showRunnable)
    }

    /**
     * Starts auto-refreshing.
     */
    fun startAutoRefresh() {
        if (isRefreshStarted || refreshTimeMs <= 0) {
            return
        }
        MediationLogger.log(tag, "startAutoRefresh")

        isRefreshStarted = true
        startShowRunnable()
    }

    private fun startShowRunnable() {
        Utils.onUiThread(refreshTimeMs, showRunnable)
    }

    private fun startRetryLoadRunnable() {
        if (retryOnFailTimeMs <= 0) {
            return
        }
        MediationLogger.log(tag, "Retry load ad after - $retryOnFailTimeMs ms")

        Utils.onUiThread(retryOnFailTimeMs, retryLoadRunnable)
    }

    private fun stopRetryLoadRunnable() {
        Utils.cancelUiThreadTask(retryLoadRunnable)
    }

    private fun loadBannerAd() {
        isAdLoaded = false
        pendingBannerMediationAd?.destroy()
        pendingBannerMediationAd = BannerMediationAd(context).apply {
            listener = internalLoadListener
            preBidTimeOutMs = this@BannerMediationAdView.preBidTimeOutMs
            postBidTimeOutMs = this@BannerMediationAdView.postBidTimeOutMs
            postBidPriceFloor = this@BannerMediationAdView.postBidPriceFloor
            setPreBidNetworkAdUnit(preBidAdUnitList)
            setPostBidNetworkAdUnit(postBidAdUnitList)
            loadAd()
        }
    }

    private fun canAdPlacedOnView(): Boolean {
        return isAdLoaded && isViewAttachedToWindow() && isShowPending && visibility == VISIBLE
    }

    private fun isViewAttachedToWindow(): Boolean {
        return if (Build.VERSION.SDK_INT >= 19) {
            isAttachedToWindow
        } else {
            windowToken != null
        }
    }

    private fun showAd() {
        if (!canAdPlacedOnView()) {
            return
        }

        isShowPending = false
        pendingBannerMediationAd?.getView()?.also {
            safeAddView(it, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER))
        } ?: loadAfterFailToShow(MediationError.invalidState("Nothing to show"))

        setCurrentBannerAd(pendingBannerMediationAd)
        pendingBannerMediationAd = null
        loadBannerAd()

        if (isRefreshStarted && refreshTimeMs > 0) {
            startShowRunnable()
        }
    }

    private fun loadAfterFailToShow(adError: MediationError) {
        MediationLogger.log(tag, "Fail to show ad. Trying to load a new one.")

        listener?.onAdFailToShow(this, adError)
        loadBannerAd()
    }

    private fun setCurrentBannerAd(bannerMediationAd: BannerMediationAd?) {
        currentBannerMediationAd?.destroy()
        currentBannerMediationAd = bannerMediationAd
    }


    private class InternalListenerMediation(bannerMediationAdView: BannerMediationAdView) : BannerMediationAdListener {

        private val weakBannerAdView = WeakReference(bannerMediationAdView)

        override fun onAdLoaded(mediationAd: BannerMediationAd) {
            weakBannerAdView.get()?.apply {
                listener?.onAdLoaded(this)

                isAdLoaded = true
                showAd()
            }
        }

        override fun onAdFailToLoad(mediationAd: BannerMediationAd) {
            weakBannerAdView.get()?.apply {
                listener?.onAdFailToLoad(this)

                startRetryLoadRunnable()
            }
        }

        override fun onAdFailToShow(mediationAd: BannerMediationAd, adError: MediationError) {
            weakBannerAdView.get()?.apply {
                loadAfterFailToShow(adError)
            }
        }

        override fun onAdShown(mediationAd: BannerMediationAd, adInfo: AdInfo) {
            weakBannerAdView.get()?.apply {
                listener?.onAdShown(this, adInfo)
            }
        }

        override fun onAdClicked(mediationAd: BannerMediationAd, adInfo: AdInfo) {
            weakBannerAdView.get()?.apply {
                listener?.onAdClicked(this, adInfo)
            }
        }

        override fun onAdExpired(mediationAd: BannerMediationAd, adInfo: AdInfo) {
            weakBannerAdView.get()?.apply {
                listener?.onAdExpired(this, adInfo)
            }
        }

        override fun onAdExpanded(mediationAd: BannerMediationAd, adInfo: AdInfo) {
            weakBannerAdView.get()?.apply {
                listener?.onAdExpanded(this, adInfo)
            }
        }

        override fun onAdCollapsed(mediationAd: BannerMediationAd, adInfo: AdInfo) {
            weakBannerAdView.get()?.apply {
                listener?.onAdCollapsed(this, adInfo)
            }
        }

    }

    private class ShowRunnable(bannerMediationAdView: BannerMediationAdView) : Runnable {

        private val weakBannerAdView = WeakReference(bannerMediationAdView)

        override fun run() {
            weakBannerAdView.get()?.apply {
                isShowPending = true
                showAd()
            }
        }

    }

    private class RetryLoadRunnable(bannerMediationAdView: BannerMediationAdView) : Runnable {

        private val weakBannerAdView = WeakReference(bannerMediationAdView)

        override fun run() {
            weakBannerAdView.get()?.loadBannerAd()
        }

    }

}