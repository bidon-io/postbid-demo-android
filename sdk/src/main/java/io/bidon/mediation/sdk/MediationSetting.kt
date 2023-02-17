package io.bidon.mediation.sdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

object MediationSetting {

    private val isInitialized = AtomicBoolean(false)

    private var weakActivity: WeakReference<Activity>? = null

    fun initialize(context: Context) {
        if (!isInitialized.compareAndSet(false, true)) {
            return
        }

        try {
            val applicationContext = context.applicationContext
            if (applicationContext is Application) {
                applicationContext.registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks())
            }
        } catch (ignore: Throwable) {
        }
    }

    fun getActivity(): Activity? = weakActivity?.get()

    fun setActivity(activity: Activity) {
        weakActivity = WeakReference(activity)
    }


    class ActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            // ignore
        }

        override fun onActivityStarted(activity: Activity) {
            try {
                setActivity(activity)
            } catch (ignore: Throwable) {
            }
        }

        override fun onActivityResumed(activity: Activity) {
            try {
                setActivity(activity)
            } catch (ignore: Throwable) {
            }
        }

        override fun onActivityPaused(activity: Activity) {
            // ignore
        }

        override fun onActivityStopped(activity: Activity) {
            // ignore
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            // ignore
        }

        override fun onActivityDestroyed(activity: Activity) {
            // ignore
        }

    }

}