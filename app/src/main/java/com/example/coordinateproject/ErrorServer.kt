package com.example.coordinateproject

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlin.system.exitProcess

class ErrorServer : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view?.findViewById<ImageView>(R.id.okButton)?.setOnClickListener {
            activity?.finish()
            // Akhiri aplikasi
            exitProcess(0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_error_server, container, false)
    }


}