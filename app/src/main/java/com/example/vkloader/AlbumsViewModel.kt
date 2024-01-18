package com.example.vkloader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vk.sdk.api.photos.dto.PhotosGetAlbumsResponseDto

class AlbumsViewModel : ViewModel() {
    fun parseAlbumsLoad(result: PhotosGetAlbumsResponseDto): List<Album> {
        val responseAlbums = result.items
        val albums = responseAlbums.map { album ->
            Album(
                id = album.id,
                count = album.size,
                coverUrl = album.thumbSrc,
                title = album.title,
            )
        }
        return albums
    }
}

class AlbumsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}