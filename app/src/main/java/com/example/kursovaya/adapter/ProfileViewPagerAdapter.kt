package com.example.kursovaya.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kursovaya.fragment.AboutFragment
import com.example.kursovaya.fragment.ReviewsFragment
import com.example.kursovaya.fragment.ScheduleFragment

class ProfileViewPagerAdapter(fragment: Fragment, private val doctorId: String?) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> AboutFragment()
            1 -> ScheduleFragment()
            2 -> ReviewsFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
        fragment.arguments = Bundle().apply {
            putString("DOCTOR_ID", doctorId)
        }
        return fragment
    }
}