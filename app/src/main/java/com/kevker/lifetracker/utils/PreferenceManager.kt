package com.kevker.lifetracker.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREFS_NAME = "sleep_prefs"
    private const val KEY_BUFFER_TIME = "buffer_time"
    private const val KEY_SLEEP_STATE = "sleep_state"
    private const val KEY_COUNTDOWN_TIME = "countdown_time"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveBufferTime(context: Context, bufferTime: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(KEY_BUFFER_TIME, bufferTime)
        editor.apply()
    }

    fun getBufferTime(context: Context): Int {
        return getPreferences(context).getInt(KEY_BUFFER_TIME, 0)
    }

    fun saveSleepState(context: Context, sleepState: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_SLEEP_STATE, sleepState)
        editor.apply()
    }

    fun getSleepState(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_SLEEP_STATE, false)
    }

    fun saveCountdownTime(context: Context, countdownTime: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(KEY_COUNTDOWN_TIME, countdownTime)
        editor.apply()
    }

    fun getCountdownTime(context: Context): Int {
        return getPreferences(context).getInt(KEY_COUNTDOWN_TIME, 0)
    }
}
