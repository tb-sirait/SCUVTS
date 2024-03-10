package com.example.coordinateproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlin.system.exitProcess

class ErrorServer : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.okButton)?.setOnClickListener {
            onImageViewClick(it)
        }
    }

    fun onImageViewClick(view: View) {
        // Lakukan tindakan yang diinginkan saat ImageView di klik
        activity?.finish()
        // Akhiri aplikasi
        exitProcess(0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_error_server, container, false)
    }
}