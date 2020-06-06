package com.example.alpha_bank_qr.Adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.alpha_bank_qr.Fragments.ContactsFragment
import com.example.alpha_bank_qr.R
import com.example.alpha_bank_qr.Fragments.TemplatesFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_templates,
    R.string.tab_contacts
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if (position == 1)
            return ContactsFragment.newInstance("", "")
        return TemplatesFragment.newInstance("", "")
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return TAB_TITLES.count()
    }
}