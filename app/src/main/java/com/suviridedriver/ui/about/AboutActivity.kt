package com.suviridedriver.ui.about

import android.content.Intent
import android.os.Bundle
import com.suviridedriver.databinding.ActivityAboutBinding
import com.suviridedriver.ui.terms_conditions.TermsConditionsActivity
import com.suviridedriver.utils.BaseActivity

class AboutActivity : BaseActivity() {

lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTC.setOnClickListener {
            startActivity(Intent(this, TermsConditionsActivity::class.java))
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}