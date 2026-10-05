package com.alkewallet.data.local

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("alke_wallet_prefs", Context.MODE_PRIVATE)

    fun saveSession(token: String, userId: Int) {
        prefs.edit()
            .putString("JWT_TOKEN", token)
            .putInt("USER_ID", userId)
            .apply()
    }

    fun getToken(): String? = prefs.getString("JWT_TOKEN", null)

    fun getUserId(): Int = prefs.getInt("USER_ID", -1)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}