package com.example.vkloader

import android.app.Application
import android.content.Intent
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler

class GalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
    }

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            val newIntent = Intent(this@GalleryApplication, MainActivity::class.java)
            newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(newIntent)
        }
    }
}