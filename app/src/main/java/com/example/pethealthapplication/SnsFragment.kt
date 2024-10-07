package com.example.pethealthapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

class SnsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sns, container, false)

        //게시물 작성 페이지로 이동
        val writeButton = view.findViewById<RelativeLayout>(R.id.writeBtn)
        writeButton.setOnClickListener{
            val intent = Intent(requireContext(), SnsWriteActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}