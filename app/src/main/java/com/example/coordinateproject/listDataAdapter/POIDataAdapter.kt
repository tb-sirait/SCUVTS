package com.example.coordinateproject.listDataAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coordinateproject.R
import com.example.coordinateproject.response.DataXXX

class POIDataAdapter(private val dataList: List<DataXXX>) : RecyclerView.Adapter<POIDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_poi, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //default override function to initiate ViewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val item = dataList[position]

        // Bind data to the ViewHolder's views here
        holder.typeTextView.text = item.type.toString()
        holder.nameTextView.text = item.name
        holder.typeNameTextView.text = item.type_name

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeTextView: TextView = itemView.findViewById(R.id.mmsi_kapal)
        val nameTextView: TextView = itemView.findViewById(R.id.nama_kapal)
        val typeNameTextView: TextView = itemView.findViewById(R.id.kecepatan_kapal)
        // Add other views as needed
    }


}