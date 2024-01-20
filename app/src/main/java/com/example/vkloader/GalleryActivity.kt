package com.example.vkloader

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.sdk.api.photos.PhotosService
import com.vk.sdk.api.photos.dto.PhotosGetAlbumsResponseDto

class GalleryActivity : AppCompatActivity() {
    private val albumsVM: AlbumsViewModel by viewModels {
        AlbumsViewModelFactory()
    }
    private lateinit var recyclerView: RecyclerView
    private var selectedAlbum: Int = 0
    private val picker = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
        if (uris.isNotEmpty()) {
            uploadPhotos(uris)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            Snackbar.make(findViewById(R.id.albums_list), "Permission granted", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(findViewById(R.id.albums_list), "Permission denied", Snackbar.LENGTH_SHORT).show()
        }
        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

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

        recyclerView = findViewById(R.id.albums_list)
        val adapter = AlbumListAdapter(object : AlbumCallback {
            override fun onClick(album: Album) {
                selectedAlbum = album.id
                if (selectedAlbum < 1) {
                    Snackbar.make(findViewById(R.id.retry_button), "Loading in system albums is prohibited", Snackbar.LENGTH_SHORT).show()
                    return
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissions.launch(READ_MEDIA_IMAGES)
                } else {
                    requestPermissions.launch(READ_EXTERNAL_STORAGE)
                }

            }
        })
        recyclerView.adapter = adapter
        val columns = resources.getInteger(R.integer.gallery_columns)
        val layoutManager = StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        loadAlbums(adapter)
        findViewById<Button>(R.id.retry_button).setOnClickListener {
            loadAlbums(adapter)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val columns = resources.getInteger(R.integer.gallery_columns)
        val layoutManager = StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
    }

    private fun loadAlbums(adapter : AlbumListAdapter) {
        VK.execute(PhotosService().photosGetAlbums(needSystem=true, needCovers=true), object: VKApiCallback<PhotosGetAlbumsResponseDto> {
            override fun fail(error: Exception) {
                Log.e("load", error.toString())
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

    private fun uploadPhotos(uris : List<Uri>) {
        VK.execute(PhotosPostCommand(uris, selectedAlbum), object: VKApiCallback<Int> {
            override fun fail(error: Exception) {
                Log.e("upload", error.toString())
                Snackbar.make(findViewById(R.id.albums_list), "Error occurred while uploading photos", Snackbar.LENGTH_LONG)
                    .setAction("Retry") {
                        uploadPhotos(uris)
                    }
                    .show()
            }
            override fun success(result: Int) {
                Log.d("upload", "Saved $result photo")
                Snackbar.make(findViewById(R.id.albums_list), "Uploaded $result photo", Snackbar.LENGTH_SHORT).show()
            }
        })
    }
}