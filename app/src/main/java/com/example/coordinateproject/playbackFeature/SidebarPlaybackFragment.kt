package com.example.coordinateproject.playbackFeature

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coordinateproject.R
import com.example.coordinateproject.playbackFeature.adapter.PlaybackDataAdapter
import com.example.coordinateproject.responseBypass.PlaybackAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SidebarPlaybackFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var name: String? = null
    private var mmsi: String? = null
    private val fromDate: String = "2024-05-23 00:58:00"
    private val toDate: String = "2024-05-23 00:59:59"
    private var tokenAPI: String = "73ob73y64nt3n653k4l1"
    private lateinit var adapter: PlaybackDataAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Inisialisasi SharedPreferences
        sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        name = sharedPreferences.getString("NAME", null)
        mmsi = sharedPreferences.getString("MMSI", null)
        Toast.makeText(context, "call Data from API $mmsi with name $name", Toast.LENGTH_SHORT).show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sidebar_playback, container, false)

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.buatListPlayback)
        val imageView8 = view.findViewById<View>(R.id.imageView8)

        // Set listener untuk klik pada imageView8
        imageView8.setOnClickListener {
            // Panggil fungsi PBVTSCallAPI untuk mengambil data dari API
            PBVTSCallAPI(fromDate, toDate, tokenAPI)
        }

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun PBVTSCallAPI(fromDate: String, toDate: String, tokenAPI: String) {
        mmsi?.let { mmsi ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = PlaybackAPI.PBRetrofit.apiService.getPBVTSData(mmsi, fromDate, toDate, tokenAPI)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                val yourDataListOnPlayback = data.data
                                adapter = PlaybackDataAdapter(yourDataListOnPlayback)
                                recyclerView.layoutManager = LinearLayoutManager(context)
                                recyclerView.adapter = adapter
                                adapter.notifyDataSetChanged()
                                Log.d("PBVTSCallAPI", data.toString())
                                Toast.makeText(context, "Data berhasil dipanggil dengan MMSI $mmsi", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.d("PBVTSCallAPI", "Data dari API kosong")
                                Toast.makeText(context, "Data dari API kosong", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.d("PBVTSCallAPI", "API response gagal dengan kode: ${response.code()}")
                            Toast.makeText(context, "Can't Call API Data", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.d("PBVTSCallAPI", "API call gagal: ${e.message}")
                        Toast.makeText(context, "Can't Call API Data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } ?: run {
            Log.d("PBVTSCallAPI", "MMSI tidak ditemukan")
            Toast.makeText(context, "MMSI tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

}