package com.suviridedriver.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppSession @Inject constructor(@ApplicationContext context1: Context) {

    fun removeValue(context: Context, key: String?) {
        val editor: SharedPreferences.Editor
        val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = settings.edit()
        editor.remove(key)
        editor.commit()
    }

    companion object {
        const val PREFS_NAME = "SUVI"
        fun save(context: Context, key: String?, text: String?) {
            val editor: SharedPreferences.Editor
            val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) //1
            editor = settings.edit() //2
            editor.putString(key, text) //3
            editor.commit() //4
        }

        @JvmStatic
        fun getValue(context: Context, key: String?): String? {
            var text: String? = ""
            val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            text = settings.getString(key, null)
            return text
        }

        fun clearSharedPreference(context: Context) {
            val settings: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = settings.edit()
            editor.clear()
            editor.commit()
        }
    }
}