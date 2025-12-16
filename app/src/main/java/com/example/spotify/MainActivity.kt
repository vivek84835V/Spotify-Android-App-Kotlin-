package com.example.spotify

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotify.adapter.TrackAdapter
import com.example.spotify.model.Track
import com.example.spotify.utils.Networkutlis
import com.example.spotify.viewmodel.TrackViewModel
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.materialswitch.MaterialSwitch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TrackViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: Track? = null
    private var isPlaying = false

    private var trackList: List<Track> = emptyList()

    private lateinit var miniPlayer: View
    private lateinit var miniTitle: TextView
    private lateinit var miniImg: ImageView
    private lateinit var btnPlayPause: ImageView
    private lateinit var switchsort: MaterialSwitch
    private lateinit var loadingBar: ProgressBar
    private lateinit var loadingContainer: FrameLayout
    private lateinit var tvoffline: TextView
    private lateinit var networkutlis: Networkutlis



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.RV)
        miniPlayer = findViewById(R.id.miniPlayer)
        miniTitle = findViewById(R.id.miniTitle)
        miniImg = findViewById(R.id.miniImg)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        switchsort = findViewById(R.id.switchSort)
        loadingBar = findViewById(R.id.loadingBar)
        loadingContainer = findViewById(R.id.loadingContainer)
        tvoffline = findViewById(R.id.tvOffline)

        loadingContainer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        miniPlayer.visibility = View.GONE

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TrackAdapter(this, emptyList())
        recyclerView.adapter = adapter


        adapter.setOnclickListener(object : TrackAdapter.onclick {
            override fun OnItemclick(track: Track) {
                playSong(track)
            }
        })

        switchsort.setOnCheckedChangeListener { _, isChecked ->
            val sorted = if (isChecked) {
                trackList.sortedBy { it.duration }
            } else {
                trackList.sortedBy { it.name.lowercase() }
            }
            adapter.updateList(sorted)
        }

        btnPlayPause.setOnClickListener {
            if (currentTrack == null) return@setOnClickListener

            loadingBar.visibility = View.GONE

            if (isPlaying) {
                mediaPlayer?.pause()
                isPlaying = false
            } else {
                mediaPlayer?.start()
                isPlaying = true
            }
            updateMiniPlayer(isPlaying)
        }

        viewModel = ViewModelProvider(this)[TrackViewModel::class.java]

        viewModel.Tracks.observe(this) { datalist ->
            if (datalist.isNullOrEmpty()) {
                loadingContainer.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                trackList = datalist
                adapter.updateList(datalist)
                loadingContainer.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
        if (!Networkutlis.isOnline(this)) {
            tvoffline.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            loadingContainer.visibility = View.GONE
        } else {

            viewModel.getTracks()
        }
        networkutlis = Networkutlis(
            this,
            OnConnect = {
                runOnUiThread {
                    tvoffline.visibility = View.GONE
                    loadingContainer.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    viewModel.getTracks()
                }
            },
            OnDisconnet = {
                runOnUiThread {
                    loadingContainer.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    tvoffline.visibility = View.VISIBLE
                }
            }
        )
        networkutlis.register()
    }

    private fun playSong(track: Track) {
        if (currentTrack?.audio == track.audio && isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            updateMiniPlayer(false)
            return
        }

        mediaPlayer?.release()
        miniPlayer.visibility = View.VISIBLE
        loadingBar.visibility = View.VISIBLE
        btnPlayPause.isEnabled = false

        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.audio)
            prepareAsync()
            setOnPreparedListener {
                loadingBar.visibility = View.GONE
                btnPlayPause.isEnabled = true
                start()
                this@MainActivity.isPlaying = true
                updateMiniPlayer(true)
            }
            setOnCompletionListener {
                this@MainActivity.isPlaying = false
                updateMiniPlayer(false)
            }
        }

        currentTrack = track
        adapter.setPlayingTrack(track)
    }

    private fun updateMiniPlayer(playing: Boolean) {
        miniPlayer.visibility = View.VISIBLE
        miniTitle.text = currentTrack?.name

        Glide.with(this)
            .load(currentTrack?.image)
            .into(miniImg)

        btnPlayPause.setImageResource(
            if(playing) R.drawable.btnpause else R.drawable.btnplay
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        networkutlis.unregister()
    }
}



