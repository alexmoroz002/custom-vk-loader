package com.example.vkloader

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class GalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val picker = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            if (uris.isNotEmpty()) {
                Log.d("PhotoPicker", "Item count: ${uris.size}")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.albums_list)
        val adapter = AlbumListAdapter(object : AlbumCallback {
            override fun onClick(album: Album) {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        })
        recyclerView.adapter = adapter
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.SPACE_BETWEEN
        recyclerView.layoutManager = layoutManager

        val a1 = Album("Assa", 3, "12", "https://lh3.googleusercontent.com/zAp7WO22UXyLv1kkiQQLY0_hmfO94RnVt8JI1_SZbA92tX6VwC56aWOSUECaIbXx6Ak0ioGq-9Ej4OtIiRBoFHcp9Gl8SFKJakjho4yhfyTT=s0")
        val a2 = Album("Asdad", 1, "12", "https://lh3.googleusercontent.com/zAp7WO22UXyLv1kkiQQLY0_hmfO94RnVt8JI1_SZbA92tX6VwC56aWOSUECaIbXx6Ak0ioGq-9Ej4OtIiRBoFHcp9Gl8SFKJakjho4yhfyTT=s0")
        val a3 = Album("Bcbvb", 12, "12", "https://lh3.googleusercontent.com/zAp7WO22UXyLv1kkiQQLY0_hmfO94RnVt8JI1_SZbA92tX6VwC56aWOSUECaIbXx6Ak0ioGq-9Ej4OtIiRBoFHcp9Gl8SFKJakjho4yhfyTT=s0")
        val a4 = Album("Jsdfdm asdnk askdj sadknjs sdasdsad", 888, "12", "https://lh3.googleusercontent.com/zAp7WO22UXyLv1kkiQQLY0_hmfO94RnVt8JI1_SZbA92tX6VwC56aWOSUECaIbXx6Ak0ioGq-9Ej4OtIiRBoFHcp9Gl8SFKJakjho4yhfyTT=s0")
        val a5 = Album("Ofjsnsj asdfsdf Ladssd", 888, "12", "https://lh3.googleusercontent.com/zAp7WO22UXyLv1kkiQQLY0_hmfO94RnVt8JI1_SZbA92tX6VwC56aWOSUECaIbXx6Ak0ioGq-9Ej4OtIiRBoFHcp9Gl8SFKJakjho4yhfyTT=s0")

        adapter.submitList(listOf(a1, a2, a3, a4, a5))
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null)
        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndex(projection[0])
        val filePath = cursor.getString(columnIndex)
        cursor.close()
        return filePath
    }
}