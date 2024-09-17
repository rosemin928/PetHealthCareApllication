package com.example.pethealthapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.pethealthapplication.diabetescheck.DiabetesCheck0Activity

class Analysis1Fragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analysis, container, false)

        val analysisButton = view.findViewById<Button>(R.id.analysisBtn)
        analysisButton.setOnClickListener {
            val intent = Intent(requireActivity(), DiabetesCheck0Activity::class.java)
            startActivity(intent)
        }

        return view
    }
}