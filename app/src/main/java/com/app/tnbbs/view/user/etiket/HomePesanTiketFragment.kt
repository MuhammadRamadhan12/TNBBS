package com.app.tnbbs.view.user.etiket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.tnbbs.R
import com.app.tnbbs.databinding.FragmentHomePesanTiketBinding

class HomePesanTiketFragment : Fragment() {
    lateinit var binding : FragmentHomePesanTiketBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomePesanTiketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener{
            requireActivity().onBackPressed()
        }
        binding.btnAlur.setOnClickListener{
            findNavController().navigate(R.id.action_homePesanTiketFragment_to_alurPenggunaFragment)
        }

        binding.btnTiketWisata.setOnClickListener{
            findNavController().navigate(R.id.action_homePesanTiketFragment_to_daftarWisataFragment)
        }

        binding.btnKuotaWisata.setOnClickListener{
            findNavController().navigate(R.id.action_homePesanTiketFragment_to_laporanDanAnalisaFragment)
        }
    }

}