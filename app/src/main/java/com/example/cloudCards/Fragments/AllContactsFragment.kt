package com.example.cloudCards.Fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.cloudCards.R

class AllContactsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragments_all_contacts, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.all_contacts_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}