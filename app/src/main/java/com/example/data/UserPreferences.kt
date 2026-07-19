package com.example.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UserPreferences {
    private const val PREFS_NAME = "puzzle_settings"
    private const val KEY_PREFERRED_CATEGORY = "preferred_category"
    private const val KEY_DISMISSED_ANNOUNCEMENT = "dismissed_announcement"

    var preferredCategoryId by mutableStateOf<String?>(null)
        private set

    var dismissedAnnouncement by mutableStateOf("")
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferredCategoryId = prefs.getString(KEY_PREFERRED_CATEGORY, null)
        dismissedAnnouncement = prefs.getString(KEY_DISMISSED_ANNOUNCEMENT, "") ?: ""
    }

    fun setPreferredCategory(context: Context, categoryId: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            if (categoryId == null) {
                remove(KEY_PREFERRED_CATEGORY)
            } else {
                putString(KEY_PREFERRED_CATEGORY, categoryId)
            }
        }.apply()
        preferredCategoryId = categoryId
    }

    fun dismissAnnouncement(context: Context, text: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_DISMISSED_ANNOUNCEMENT, text).apply()
        dismissedAnnouncement = text
    }
}
