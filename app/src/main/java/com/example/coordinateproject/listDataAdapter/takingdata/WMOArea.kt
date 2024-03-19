package com.example.coordinateproject.listDataAdapter.takingdata

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coordinateproject.R
import com.example.coordinateproject.listDataAdapter.WMOAreaDataAdapter
import com.example.coordinateproject.response.wmoarea
import com.example.coordinateproject.responseBypass.WMOArea
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WMOArea : Fragment() {

    private val REFRESH_INTERVAL: Long = 10 * 1000 // 10 seconds in milliseconds
    private lateinit var refreshHandler: Handler
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WMOAreaDataAdapter

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
        val call = WMOArea.WMOAREARetrofit.apiService.getAllDataKapal("73ob73y64nt3n653k4l1")
        call.enqueue(object : Callback<wmoarea> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<wmoarea>, response: Response<wmoarea>) {
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
                        val adapter = WMOAreaDataAdapter(yourDataList)
                        if (recyclerView != null) {
                            recyclerView.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    Toast.makeText(context, "Can't Call API Data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<wmoarea>, t: Throwable) {
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