package com.app.tnbbs.view.onboarding

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.tnbbs.databinding.FragmentSecondOnboardingBinding

class SecondOnboardingFragment : Fragment() {

    private lateinit var binding : FragmentSecondOnboardingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSecondOnboardingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.line2.max = 5000
        val currentProgress = 5000
        ObjectAnimator.ofInt(binding.line2, "progress", currentProgress).setDuration(5000).start()
    }
}