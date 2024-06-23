package com.app.tnbbs.view.authentication

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.tnbbs.R
import com.app.tnbbs.databinding.ActivityLupaSandiBinding
import com.app.tnbbs.view.admin.AdminActivity
import com.google.firebase.auth.FirebaseAuth

class LupaSandiActivity : AppCompatActivity() {
    lateinit var binding : ActivityLupaSandiBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLupaSandiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnBack.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        binding.btnLupa.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Masukkan email terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "Email reset password telah dikirim.")
                        Toast.makeText(this, "Email reset password telah dikirim.", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(ContentValues.TAG, "Gagal mengirim email reset password.", task.exception)
                        Toast.makeText(this, "Gagal mengirim email reset password.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }
}