package com.example.coordinateproject.playbackFeature.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coordinateproject.R
import com.example.coordinateproject.response.PlayBackData

class PlaybackDataAdapter (private val dataList: List<PlayBackData>) : RecyclerView.Adapter<PlaybackDataAdapter.ViewHolderPlayback>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPlayback {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_playback, parent, false)
        return ViewHolderPlayback(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderPlayback, position: Int) {
        val item = dataList[position]

        holder.timeTextView.text = item.stamp
        holder.latTextView.text = item.lat.toString()
        holder.lonTextView.text = item.lon.toString()
        holder.headingTextView.text = item.hdg.toString()

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolderPlayback(itemViewPlayback: View) : RecyclerView.ViewHolder(itemViewPlayback) {
        val timeTextView: TextView = itemViewPlayback.findViewById(R.id.time)
        val latTextView: TextView = itemViewPlayback.findViewById(R.id.lat)
        val lonTextView: TextView = itemViewPlayback.findViewById(R.id.lon)
        val headingTextView: TextView = itemViewPlayback.findViewById(R.id.hdg)
    }
}