package com.example.vkloader

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.sdk.api.photos.PhotosService
import com.vk.sdk.api.photos.dto.PhotosGetAlbumsResponseDto

class GalleryActivity : AppCompatActivity() {
    private val albumsVM: AlbumsViewModel by viewModels {
        AlbumsViewModelFactory()
    }
    private var selectedAlbum: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        findViewById<Button>(R.id.logout_button).setOnClickListener {
            VK.logout()
            val newIntent = Intent(this@GalleryActivity, MainActivity::class.java)
            newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(newIntent)
            finish()
        }

        val picker = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            if (uris.isNotEmpty()) {
                VK.execute(PhotosPostCommand(uris, selectedAlbum), object: VKApiCallback<Int> {
                    override fun fail(error: Exception) {
                        Log.e("upload", error.message.toString())
                        Log.e("upload", error.toString())
                    }

                    override fun success(result: Int) {
                        Log.d("upload", "Saved $result photo")
                    }
                })
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.albums_list)
        val adapter = AlbumListAdapter(object : AlbumCallback {
            override fun onClick(album: Album) {
                selectedAlbum = album.id
                if (selectedAlbum < 1) {
                    Snackbar.make(findViewById(R.id.retry_button), "Loading in system albums is prohibited", Snackbar.LENGTH_SHORT).show()
                    return
                }
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        })
        recyclerView.adapter = adapter
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.SPACE_BETWEEN
        recyclerView.layoutManager = layoutManager
        loadAlbums(adapter)
        findViewById<Button>(R.id.retry_button).setOnClickListener {
            loadAlbums(adapter)
        }
    }

    fun loadAlbums(adapter : AlbumListAdapter) {
        VK.execute(PhotosService().photosGetAlbums(needSystem=true, needCovers=true), object: VKApiCallback<PhotosGetAlbumsResponseDto> {
            override fun fail(error: Exception) {
                Snackbar.make(findViewById(R.id.retry_button), "Error occurred while loading albums", Snackbar.LENGTH_LONG)
                    .setAction("Retry") {
                        loadAlbums(adapter)
                    }
                    .show()
            }
            override fun success(result: PhotosGetAlbumsResponseDto) {
                val albums = albumsVM.parseAlbumsLoad(result)
                adapter.submitList(albums)
            }
        })
    }
}