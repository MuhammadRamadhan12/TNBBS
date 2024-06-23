package com.app.tnbbs.view.user.alurpengguna

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tnbbs.R
import com.app.tnbbs.databinding.FragmentAlurPenggunaBinding

class AlurPenggunaFragment : Fragment() {
    lateinit var binding : FragmentAlurPenggunaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlurPenggunaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener{
            requireActivity().onBackPressed()
        }
    }
}