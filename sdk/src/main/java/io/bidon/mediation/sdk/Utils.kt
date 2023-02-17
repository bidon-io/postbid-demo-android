package io.bidon.mediation.sdk

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import kotlin.math.roundToInt

object Utils {

    private val UI_HANDLER: Handler = Handler(Looper.getMainLooper())
    private val BACKGROUND_HANDLER: Handler = HandlerThread("MediationThread").apply {
        start()
    }.run {
        Handler(looper)
    }

    @JvmStatic
    fun createLayoutParams(resources: Resources, height: Int): ViewGroup.LayoutParams {
        return ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                      dp2px(resources, height.toFloat()))
    }

    @JvmStatic
    fun dp2px(context: Context, dp: Float): Int {
        return dp2px(context.resources, dp)
    }

    @JvmStatic
    fun dp2px(resources: Resources, dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).roundToInt()
    }

    @JvmStatic
    fun ViewGroup.safeAddView(view: View) {
        safeAddView(view, null)
    }

    @JvmStatic
    fun ViewGroup.safeAddView(view: View, layoutParams: ViewGroup.LayoutParams?) {
        removeAllViews()
        view.removeViewFromParent()
        if (layoutParams != null) {
            addView(view, layoutParams)
        } else {
            addView(view)
        }
    }

    @JvmStatic
    fun isUiThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    @JvmStatic
    fun onUiThread(runnable: Runnable) {
        if (isUiThread()) {
            runnable.run()
        } else {
            UI_HANDLER.post(runnable)
        }
    }

    @JvmStatic
    fun onUiThread(delayMs: Long, runnable: Runnable) {
        UI_HANDLER.postDelayed(runnable, delayMs)
    }

    @JvmStatic
    fun cancelUiThreadTask(runnable: Runnable) {
        UI_HANDLER.removeCallbacks(runnable)
    }

    @JvmStatic
    fun onBackgroundThread(runnable: Runnable) {
        if (isUiThread()) {
            BACKGROUND_HANDLER.post(runnable)
        } else {
            runnable.run()
        }
    }

    @JvmStatic
    fun onBackgroundThread(runnable: Runnable, delay: Long) {
        BACKGROUND_HANDLER.postDelayed(runnable, delay)
    }

    @JvmStatic
    fun Any.generateTag(tag: String): String {
        return "$tag @${Integer.toHexString(hashCode())}"
    }

    @JvmStatic
    fun View?.removeViewFromParent() {
        this ?: return
        (parent as? ViewGroup)?.removeView(this)
    }

    fun <K, V> Map<*, *>.filterIsInstance(keyClass: Class<K>, valueClass: Class<V>): Map<K, V> {
        val result: MutableMap<K, V> = mutableMapOf()
        forEach {
            val key = it.key
            val value = it.value
            if (keyClass.isInstance(key) && valueClass.isInstance(value)) {
                @Suppress("UNCHECKED_CAST")
                result[key as K] = value as V
            }
        }
        return result
    }

}