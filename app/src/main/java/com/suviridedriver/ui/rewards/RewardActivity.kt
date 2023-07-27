package com.suviridedriver.ui.rewards

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import com.suviridedriver.databinding.ActivityRewardBinding
import com.suviridedriver.utils.BaseActivity

class RewardActivity : BaseActivity() {
    lateinit var binding: ActivityRewardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivCopy.setOnClickListener {
            copyToClipboard(binding.RewardId.text.toString())
        }
    }

    private fun copyToClipboard(copiedText: String?) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", copiedText)
        clipboard.setPrimaryClip(clipData)
        showToast("Copied to clipboard")
    }
}