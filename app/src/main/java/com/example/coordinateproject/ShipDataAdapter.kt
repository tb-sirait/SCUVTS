package com.example.coordinateproject

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coordinateproject.playbackFeature.PlaybackActivity
import com.example.coordinateproject.response.Data

class ShipDataAdapter(private val dataList: List<Data>) : RecyclerView.Adapter<ShipDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_kapal, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]

        holder.mmsiTextView.text = item.MMSI
        holder.nameTextView.text = item.name
        holder.calcspeedTextView.text = "${item.calcspeed} KTS"

        holder.playPlaybackImageView.setOnClickListener {
            val context = it.context
            val intent = Intent(context, PlaybackActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mmsiTextView: TextView = itemView.findViewById(R.id.mmsi_kapal)
        val nameTextView: TextView = itemView.findViewById(R.id.nama_kapal)
        val calcspeedTextView: TextView = itemView.findViewById(R.id.kecepatan_kapal)
        val playPlaybackImageView: ImageView = itemView.findViewById(R.id.play_playback)
    }
}