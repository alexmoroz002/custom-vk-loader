package com.example.vkloader

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKAuthException


class MainActivity : AppCompatActivity() {
    private lateinit var authLauncher: ActivityResultLauncher<Collection<VKScope>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VK.isLoggedIn()) {
            onLogon()
            return
        }
        setContentView(R.layout.activity_main)

        authLauncher = VK.login(this) { result : VKAuthenticationResult ->
            when (result) {
                is VKAuthenticationResult.Success -> onLogon()
                is VKAuthenticationResult.Failed -> onFailure(result.exception)
            }
        }
        findViewById<Button>(R.id.login_button).setOnClickListener {
            authLauncher.launch(arrayListOf(VKScope.PHOTOS))
        }
    }

    private fun onLogon() {
        val newIntent = Intent(this@MainActivity, GalleryActivity::class.java)
        startActivity(newIntent)
        finish()
    }

    private fun onFailure(exception: VKAuthException) {
        Log.e("login", exception.message.toString())
        Snackbar.make(findViewById(R.id.login_button),
            getString(R.string.auth_failed), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.retry)) {
                authLauncher.launch(arrayListOf(VKScope.PHOTOS))
            }.show()
    }
}