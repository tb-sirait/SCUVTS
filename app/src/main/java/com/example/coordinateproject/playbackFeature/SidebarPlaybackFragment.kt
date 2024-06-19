package com.example.coordinateproject.playbackFeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.coordinateproject.R

class SidebarPlaybackFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sidebar_playback, container, false)
    }

    private fun testingAPI(){
//        Toast.makeText(, "", Toast.LENGTH_SHORT).show()
    }

}