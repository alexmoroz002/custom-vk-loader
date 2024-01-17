package com.example.vkloader

import android.app.Application
import com.vk.api.sdk.VK

class GalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        VK.initialize(applicationContext)
    }
}