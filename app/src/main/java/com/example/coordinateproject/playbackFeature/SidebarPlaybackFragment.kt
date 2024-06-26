package com.example.coordinateproject.playbackFeature

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coordinateproject.R
import com.example.coordinateproject.playbackFeature.adapter.PlaybackDataAdapter
import com.example.coordinateproject.response.PBVTS
import com.example.coordinateproject.responseBypass.PlaybackAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SidebarPlaybackFragment : Fragment() {

    private lateinit var mmsi: String
    private lateinit var fromDate: String
    private lateinit var toDate: String
    private var tokenAPI: String = "73ob73y64nt3n653k4l1"
    private lateinit var adapter: PlaybackDataAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PBVTSCallAPI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sidebar_playback, container, false)
    }

    private fun PBVTSCallAPI(){
        val call = PlaybackAPI.PBRetrofit.apiService.getPBVTSData(mmsi, fromDate, toDate, tokenAPI)
        call.enqueue(object : Callback<PBVTS> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<PBVTS>, response: Response<PBVTS>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        val yourDataListOnPlayback = data.data
                        adapter = PlaybackDataAdapter(yourDataListOnPlayback)
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        recyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(context, "Can't Call API Data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<PBVTS>, t: Throwable) {
                Toast.makeText(context, "Can't Call API Data", Toast.LENGTH_SHORT).show()
            }
        })
    }

}