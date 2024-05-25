package com.underoid.ecogreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.underoid.ecogreen.GlobalVars
import com.underoid.ecogreen.R

class AccountFragment : Fragment() {

    private lateinit var tvSpotsMarked: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_account, container, false)

        tvSpotsMarked = view.findViewById(R.id.tv_spotsMarked)

        tvSpotsMarked.text = "SpotsMarked: ${GlobalVars.getLatLngListSizee()}"

        return view
    }

    override fun onResume() {
        super.onResume()
        tvSpotsMarked.text = "SpotsMarked: ${GlobalVars.getLatLngListSizee()}"
    }

}