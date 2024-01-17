package com.example.vkloader

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val img = findViewById<ImageView>(R.id.testImg)
        val uri = Uri.parse(intent.getStringExtra("uri"))
        val path = intent.getStringExtra("path")

        Glide.with(this).load(path?.let { File(it) }).into(img)
    }
}