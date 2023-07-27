package com.suviridedriver.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.suviridedriver.ui.bottom_navigation.MainActivity
import com.suviridedriver.ui.registration.RegistrationActivity
import com.suviridedriver.ui.welcome.WelcomeActivity

class FlowManager() {
    var intent = Intent()
    fun navigate(context: Context, code: String?) {
        AppSession.save(context, Constants.SCREEN_TYPE, code)
        Log.i("SCREEN_TYPE", "SCREEN_TYPE - $code")
        when (code) {
            "welcome" -> {
                intent = Intent(context, WelcomeActivity::class.java)
                nextActivity(context, intent)
            }
            "registration" -> {
                intent = Intent(context, RegistrationActivity::class.java)
                nextActivity(context, intent)
            }
            "home_screen" -> {
                intent = Intent(context, MainActivity::class.java)
                nextActivity(context, intent)
            }
            "verification" -> {
                intent = Intent(context, RegistrationActivity::class.java)
                nextActivity(context, intent)
            }

            else -> {
                if (code == null) {
                    /* intent = Intent(context, AssessmentCompleteActivity::class.java)
                     nextActivity(context, intent, nameOfClass)*/
                } else {
                    Toast.makeText(context, "Under Production - ($code)", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun nextActivity(context: Context, intent: Intent) {
        context.startActivity(intent)
        val activity = context as Activity
        activity.overridePendingTransition(
            androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out
        )
        activity.finishAffinity()
    }
}