package com.app.tnbbs.view.profil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tnbbs.databinding.FragmentBantuanBinding

class BantuanFragment : Fragment() {
    lateinit var binding: FragmentBantuanBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBantuanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener{
            requireActivity().onBackPressed()
        }

        binding.btnHubungiKami.setOnClickListener {
            val phoneNumber = "+6281310111423"
            val uri = Uri.parse("https://wa.me/$phoneNumber")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "WhatsApp tidak terpasang di perangkat ini", Toast.LENGTH_SHORT).show()
            }
        }
//            if (intent.resolveActivity(requireActivity().packageManager) != null) {
//                startActivity(intent)
//            } else {
//                Toast.makeText(requireContext(), "WhatsApp tidak terpasang di perangkat ini", Toast.LENGTH_SHORT).show()
//            }
        }
    }

