package com.roblobsta.lobstachat.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

private const val PREFS_NAME = "LOBSTACHAT_SETTINGS"
private const val KEY_TTS_ENABLED = "tts_enabled"
private const val KEY_STT_ENABLED = "stt_enabled"
private const val KEY_RAM_USAGE_ENABLED = "ram_usage_enabled"

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setTtsEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(KEY_TTS_ENABLED, enabled)
        }
    }

    fun isTtsEnabled(): Boolean {
        return prefs.getBoolean(KEY_TTS_ENABLED, true)
    }

    fun setSttEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(KEY_STT_ENABLED, enabled)
        }
    }

    fun isSttEnabled(): Boolean {
        return prefs.getBoolean(KEY_STT_ENABLED, true)
    }

    fun setRamUsageEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(KEY_RAM_USAGE_ENABLED, enabled)
        }
    }

    fun isRamUsageEnabled(): Boolean {
        return prefs.getBoolean(KEY_RAM_USAGE_ENABLED, true)
    }
}
