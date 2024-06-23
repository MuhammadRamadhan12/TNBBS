package com.app.tnbbs.view.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.tnbbs.view.onboarding.FirstOnboardingFragment
import com.app.tnbbs.view.onboarding.SecondOnboardingFragment
import com.app.tnbbs.view.onboarding.ThirdOnboardingFragment

class OnboardingAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstOnboardingFragment()
            1 -> SecondOnboardingFragment()
            2 -> ThirdOnboardingFragment()
            else -> FirstOnboardingFragment()
        }
    }

    override fun getItemCount(): Int = 3
}
