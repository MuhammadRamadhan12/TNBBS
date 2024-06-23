package com.app.tnbbs.view.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.app.tnbbs.R
import com.app.tnbbs.data.datastore.SharedPref
import com.app.tnbbs.databinding.ActivitySplashScreenBinding
import com.app.tnbbs.view.admin.AdminActivity
import com.app.tnbbs.view.authentication.LoginActivity
import com.app.tnbbs.view.onboarding.OnboardingActivity
import com.app.tnbbs.view.user.UserActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    lateinit var binding : ActivitySplashScreenBinding
    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = SharedPref(this)

        startSplashScreen()
    }

    private fun startSplashScreen() {
        lifecycleScope.launch(Dispatchers.IO) {
            sharedPref.getAllUserData.collect {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (it.userId == "Undefined"){
                        val intent = Intent(this@SplashScreenActivity, OnboardingActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()                    }else{
                        if (it.role == "admin") {
                            val intent = Intent(this@SplashScreenActivity, AdminActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this@SplashScreenActivity, UserActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
                }, 1000)
            }
        }
    }
}