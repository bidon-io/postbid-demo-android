package io.bidon.mediation.sdk

import android.util.Log

object MediationLogger {

    private const val TAG = "MediationManager"

    @JvmStatic
    var isEnabled = false

    @JvmStatic
    fun log(message: String) {
        log(TAG, message)
    }

    @JvmStatic
    fun log(subTag: String, message: String) {
        log(subTag, message, false)
    }

    @JvmStatic
    fun error(message: String) {
        error(TAG, message)
    }

    @JvmStatic
    fun error(subTag: String, message: String) {
        log(subTag, message, true)
    }

    @JvmStatic
    fun log(subTag: String, message: String, isError: Boolean) {
        if (!isEnabled) {
            return
        }
        val result = "[$subTag] $message"
        if (isError) {
            Log.e(TAG, result)
        } else {
            Log.d(TAG, result)
        }
    }

    fun throwable(throwable: Throwable) {
        if (!isEnabled) {
            return
        }
        Log.w(TAG, throwable)
    }

}