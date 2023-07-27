package com.suviridedriver.ui.suvi_wallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.suviridedriver.R

class SuviWalletActivity : AppCompatActivity() {
    private val ivBack: ImageView get() = findViewById(R.id.ivBack)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suvi_wallet)

        ivBack.setOnClickListener {
            finish()
        }

    }
}