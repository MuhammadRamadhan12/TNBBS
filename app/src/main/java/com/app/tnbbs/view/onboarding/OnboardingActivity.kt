package com.app.tnbbs.view.onboarding

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.app.tnbbs.databinding.ActivityOnboardingBinding
import com.app.tnbbs.view.authentication.LoginActivity
import com.app.tnbbs.view.onboarding.adapter.OnboardingAdapter

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = OnboardingAdapter(this)

        binding.btnNextFirst.setOnClickListener {
            if (binding.viewPager.currentItem < 2) {
                binding.viewPager.currentItem += 1
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                binding.btnNextFirst.text = if (position == 2) "Mulai" else "Selanjutnya"

                if (position < 2) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.viewPager.currentItem = binding.viewPager.currentItem + 1
                    }, 2000)
                }
            }
        })
    }
}
