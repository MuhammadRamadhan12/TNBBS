package com.app.tnbbs.view.profil

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.tnbbs.R
import com.app.tnbbs.data.datastore.SharedPref
import com.app.tnbbs.databinding.FragmentProfilBinding
import com.app.tnbbs.view.authentication.LoginActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfilFragment : Fragment() {
    lateinit var binding: FragmentProfilBinding
    private lateinit var sharedPref: SharedPref
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPref(requireContext())
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        getProfile()

        binding.btnEditProfil.setOnClickListener {
            findNavController().navigate(R.id.action_profilFragment_to_editProfilFragment)
        }

        binding.btnBantuan.setOnClickListener {
            findNavController().navigate(R.id.action_profilFragment_to_bantuanFragment)
        }

        binding.btnKeluar.setOnClickListener {
              logout()
        }
    }

    private fun logout() {
        lifecycleScope.launch(Dispatchers.IO) {
            sharedPref.removeSession()
        }

        auth.signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show()

    }

    private fun getProfile() {
        lifecycleScope.launch(Dispatchers.IO) {
            sharedPref.getAllUserData.collect { userData ->
                withContext(Dispatchers.Main) {
                    binding.tvUserEmail.text = userData.email ?: ""
                    binding.tvNama.text = "${userData.firstName ?: ""} ${userData.lastName ?: ""}"
                    binding.tvPhoneNumber.text = userData.phoneNumber ?: ""
                    Glide.with(requireContext())
                        .load(userData.profilePicture ?: "")
                        .into(binding.ivProfil)
                }
            }
        }
    }
}
