package com.example.appmussicapi.ui.main

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val THEME_KEY = "theme_mode"
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
        const val THEME_SYSTEM = 2
    }
    
    fun setTheme(themeMode: Int) {
        prefs.edit().putInt(THEME_KEY, themeMode).apply()
        applyTheme(themeMode)
    }
    
    fun getCurrentTheme(): Int {
        return prefs.getInt(THEME_KEY, THEME_SYSTEM)
    }
    
    fun initializeTheme() {
        applyTheme(getCurrentTheme())
    }
    
    private fun applyTheme(themeMode: Int) {
        when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}