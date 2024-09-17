package com.example.pethealthapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MyPageFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_my_page, container, false)

        val nicknameChange = view.findViewById<TextView>(R.id.nicknameChange)
        nicknameChange.setOnClickListener {
            val intent = Intent(requireActivity(), NicknameChangeActivity::class.java)
            startActivity(intent)
        }

        val passwordChange = view.findViewById<TextView>(R.id.passwordChange)
        passwordChange.setOnClickListener {
            val intent = Intent(requireActivity(), PasswordChangeActivity::class.java)
            startActivity(intent)
        }

        val userDelete = view.findViewById<TextView>(R.id.userDelete)
        userDelete.setOnClickListener{
            val intent = Intent(requireActivity(), UserDeleteActivity::class.java)
            startActivity(intent)
        }

        val nextHospitalVisit = view.findViewById<TextView>(R.id.nextHospitalVisit)
        nextHospitalVisit.setOnClickListener {
            val intent = Intent(requireActivity(), NextHospitalVisitActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}