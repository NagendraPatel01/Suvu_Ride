package com.suviridedriver.utils

import android.content.Context
import android.content.SharedPreferences
import com.suviridedriver.utils.Constants.PREFS_TOKEN_FILE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    companion object {
        fun saveToken(token: String, context: Context) {
            val editor: SharedPreferences.Editor
            val settings: SharedPreferences =
                context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)
            editor = settings.edit()
            editor.putString(Constants.ACCESS_TOKEN, token)
            editor.apply()
        }

        fun getToken(@ApplicationContext context: Context): String? {
            val settings: SharedPreferences =
                context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)
            return settings.getString(Constants.ACCESS_TOKEN, null)
        }
    }
}