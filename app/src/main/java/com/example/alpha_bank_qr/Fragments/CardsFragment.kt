package com.example.alpha_bank_qr.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.alpha_bank_qr.Adapters.SectionsPagerAdapter
import com.example.alpha_bank_qr.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_cards.*

class CardsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_cards, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val sectionsPagerAdapter =
            SectionsPagerAdapter(
                requireActivity().applicationContext,
                childFragmentManager
            )
        val viewPager: ViewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs : TabLayout = view.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }
}