package com.app.tnbbs.view.authentication

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.tnbbs.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        binding.btnDaftar.setOnClickListener {
            register()
        }

        binding.btnMasuk.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun register(){
        val role ="user"
        val email = binding.etEmail.text.toString()
        val password = binding.editPassword.text.toString()
        val username = binding.etUsername.text.toString()
        val firstName = binding.etNamaDepan.text.toString()
        val lastName = binding.etNamaBelakang.text.toString()
        val noTelepon = binding.etPhone.text.toString()
        val profilePicture = "https://firebasestorage.googleapis.com/v0/b/my-palawi.appspot.com/o/person.png?alt=media&token=44b1ce33-50e3-4441-b79f-497fb73572ec"

        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || noTelepon.isEmpty()) {
            Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                    val userId = user?.uid ?: ""
                    val userData = HashMap<String, Any>()
                    userData["username"] = username
                    userData["firstName"] = firstName
                    userData["lastName"] = lastName
                    userData["noTelepon"] = noTelepon
                    userData["email"] = email
                    userData["password"] = password
                    userData["userId"] = userId
                    userData["profilePicture"] = profilePicture
                    userData["role"] = role

                    database.child("users").child(userId).setValue(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                            Log.d(ContentValues.TAG, "User information saved to database")
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.e(ContentValues.TAG, "Error saving user information to database", e)
                        }
                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Registrasi Gagal", Toast.LENGTH_SHORT).show()

                }
            }
    }
}
