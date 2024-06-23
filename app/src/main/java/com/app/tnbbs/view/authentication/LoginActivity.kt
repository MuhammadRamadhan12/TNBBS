package com.app.tnbbs.view.authentication

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.tnbbs.data.datastore.SharedPref
import com.app.tnbbs.databinding.ActivityLoginBinding
import com.app.tnbbs.view.admin.AdminActivity
import com.app.tnbbs.view.user.UserActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPref
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        sharedPref = SharedPref(this)
        database = FirebaseDatabase.getInstance().reference

        binding.btnMasuk.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()
            login(email, password)
        }

        binding.btnDaftar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLupas.setOnClickListener {
            val intent = Intent(this, LupaSandiActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this@LoginActivity, "Email dan kata sandi harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser!!.uid
                    getUserData(userId)
                } else {
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@LoginActivity, "Email atau kata sandi salah", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getUserData(userId: String) {
        database.child("users").child(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val firstName = dataSnapshot.child("firstName").getValue(String::class.java) ?: ""
                    val lastName = dataSnapshot.child("lastName").getValue(String::class.java) ?: ""
                    val email = dataSnapshot.child("email").getValue(String::class.java) ?: ""
                    val phoneNumber = dataSnapshot.child("noTelepon").getValue(String::class.java) ?: ""
                    val password = dataSnapshot.child("password").getValue(String::class.java) ?: ""
                    val profilePicture = dataSnapshot.child("profilePicture").getValue(String::class.java) ?: ""
                    val role = dataSnapshot.child("role").getValue(String::class.java) ?: ""
                    val username = dataSnapshot.child("username").getValue(String::class.java) ?: ""

                    saveData(userId, username, firstName, lastName, email, phoneNumber, password, profilePicture, role)
                    Log.d(ContentValues.TAG, "User data retrieved successfully")

                    if (role == "admin") {
                        val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@LoginActivity, UserActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }

                    Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w(ContentValues.TAG, "User data does not exist")
                    Toast.makeText(this@LoginActivity, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "getUserData:onCancelled", databaseError.toException())
                Toast.makeText(this@LoginActivity, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveData(userId: String, username : String ,firstName: String, lastName: String, email: String, phoneNumber: String, password: String, profilePicture: String, role: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            sharedPref.saveUserData(userId, username ,firstName, lastName, email, phoneNumber, password, profilePicture, role)
        }
    }
}