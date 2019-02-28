package com.vlogonappv1.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.vlogonappv1.contactlist.GroupFragment
import com.vlogonappv1.contactlist.RegisterFragment
import com.vlogonappv1.contactlist.UnRegisterFragment

class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                RegisterFragment()
            }
            else -> {
                return UnRegisterFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Register"
            else ->
             "Unregister"
        }
    }
}