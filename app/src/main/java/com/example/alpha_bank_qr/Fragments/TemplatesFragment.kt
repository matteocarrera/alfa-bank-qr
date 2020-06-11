package com.example.alpha_bank_qr.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alpha_bank_qr.R

class TemplatesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_templates, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TemplatesFragment()
    }
}