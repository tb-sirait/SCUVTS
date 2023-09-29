package com.example.coordinateproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coordinateproject.response.APIService
import com.example.coordinateproject.response.ApiResponse
import com.example.coordinateproject.response.Data
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ShipFragment : Fragment() {

    private val REFRESH_INTERVAL: Long = 10 * 1000 // 10 seconds in milliseconds
    private lateinit var refreshHandler: Handler
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShipDataAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiCallData()
        refreshHandler = Handler()
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ship, container, false)


    }

    private fun apiCallData(){
        val call = MapsViewFragment.RetrofitClient.apiService.getData("73ob73y64nt3n653k4l1")
        call.enqueue(object : Callback<ApiResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        // Handle the response data
                        val yourDataList = data.data
                        val recyclerView = view?.findViewById<RecyclerView>(R.id.listKapal)
                        val layoutManager = LinearLayoutManager(context)
                        if (recyclerView != null) {
                            recyclerView.layoutManager = layoutManager
                        }
                        val adapter = ShipDataAdapter(yourDataList)
                        if (recyclerView != null) {
                            recyclerView.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    Toast.makeText(context, "Can't Call API Data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Handle failure

            }
        })
    }

    // Apply Refresh Runnable to call data from API automatically according to the specified time
    private val refreshRunnable = object : Runnable {
        override fun run() {
            apiCallData()
            refreshHandler.postDelayed(this, REFRESH_INTERVAL)
        }
    }

    override fun onPause() {
        super.onPause()
        refreshHandler.removeCallbacks(refreshRunnable)
    }

}