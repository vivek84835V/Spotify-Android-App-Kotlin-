package com.example.spotify.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotify.model.Track
import com.example.spotify.repository.TrackRepository
import io.ktor.utils.io.printStack
import kotlinx.coroutines.launch

class TrackViewModel : ViewModel() {

    private val repository = TrackRepository()

    private val _Track = MutableLiveData<List<Track>>()
    val Tracks: LiveData<List<Track>> = _Track


    fun getTracks() {
        viewModelScope.launch {
            try {
                _Track.value = repository.getsongs()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}