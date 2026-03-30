package com.example.finalproject.utils

import android.util.Log

/**
 * AppLogger — logging tập trung cho toàn bộ app.
 *
 * Cách dùng:
 *   AppLogger.d("TAG", "debug message")
 *   AppLogger.e("TAG", "error message", exception)
 *   AppLogger.network("TAG", "request/response info")
 *
 * Trong production build, tắt debug log bằng cách set DEBUG = false.
 */
object AppLogger {

    private const val DEBUG = true   // Đổi thành false khi build release
    private const val APP_TAG = "MaSci"

    fun d(tag: String = APP_TAG, message: String) {
        if (DEBUG) Log.d(tag, message)
    }

    fun i(tag: String = APP_TAG, message: String) {
        Log.i(tag, message)
    }

    fun w(tag: String = APP_TAG, message: String) {
        Log.w(tag, message)
    }

    fun e(tag: String = APP_TAG, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    /** Dành riêng cho log các request/response mạng */
    fun network(tag: String = "Network", message: String) {
        if (DEBUG) Log.d("[$APP_TAG-Net] $tag", message)
    }

    /** Dành riêng cho log các sự kiện UI / Navigation */
    fun ui(tag: String = "UI", message: String) {
        if (DEBUG) Log.d("[$APP_TAG-UI] $tag", message)
    }

    /** Dành riêng cho log ViewModel state changes */
    fun state(tag: String = "State", message: String) {
        if (DEBUG) Log.d("[$APP_TAG-VM] $tag", message)
    }
}
