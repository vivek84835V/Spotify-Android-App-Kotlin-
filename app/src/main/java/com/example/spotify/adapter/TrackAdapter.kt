package com.example.spotify.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotify.R
import com.example.spotify.model.Track
import io.ktor.websocket.Frame

class TrackAdapter(val context: Context,private var data:List<Track>): RecyclerView.Adapter<TrackAdapter.ViewHolder>() {

    private var playingTrack: Track? = null
    private var listener: onclick? = null

    interface onclick {
        fun OnItemclick(track: Track)
    }

    fun setOnclickListener(MyListener: onclick) {
        listener = MyListener
    }

    fun updateList(newList: List<Track>) {
        data = newList
        notifyDataSetChanged()
    }

    fun setPlayingTrack(track: Track) {
        playingTrack = track
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.each_tracks, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, ) {
        val item = data[position]

        holder.trackname.text = item.name
        holder.artistname.text = item.artist_name

        val minutes = item.duration / 60
        val seconds = item.duration % 60
        holder.duration.text = String.format("%02d:%02d", minutes, seconds)

        Glide.with(holder.img.context)
            .load(item.image)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            listener?.OnItemclick(item)
        }

        if (item == playingTrack) {
            holder.itemView.setBackgroundResource(R.drawable.playbg)
        } else {
            holder.itemView.setBackgroundResource(android.R.color.transparent)
        }
    }

    override fun getItemCount(): Int {
        return data.size;
    }

    class ViewHolder(View: View) : RecyclerView.ViewHolder(View) {
        val img = itemView.findViewById<ImageView>(R.id.imgAlbum)
        val trackname = itemView.findViewById<TextView>(R.id.tvSongName)
        val artistname = itemView.findViewById<TextView>(R.id.tvArtistName)
        val duration = itemView.findViewById<TextView>(R.id.tvDuration)
    }
}