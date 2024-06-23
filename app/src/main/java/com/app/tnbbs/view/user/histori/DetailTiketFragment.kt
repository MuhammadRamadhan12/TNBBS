package com.app.tnbbs.view.user.histori

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.tnbbs.databinding.FragmentDetailTiketBinding
import com.app.tnbbs.utils.RupiahConverter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DetailTiketFragment : Fragment() {
    private lateinit var binding : FragmentDetailTiketBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailTiketBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference = FirebaseDatabase.getInstance().reference

        val bundle = arguments

        binding.apply {
            tvNama.text = bundle?.getString("namaPemesan")
            tvPhone.text = bundle?.getString("noHp")
            tvKewarganegaraan.text = bundle?.getString("selectedNationality")
            tvJumlahOrang.text = "Jumlah Orang : ${bundle?.getString("jumlahOrang")}"
            tvHargaTiket.text = "Harga Tiket Wisata : ${RupiahConverter.rupiah(bundle?.getString("generalPrice")!!.toInt())}"
            tvHargaTiketMasuk.text = "Harga Tiket Masuk : ${RupiahConverter.rupiah(bundle?.getString("totalBiayaMasuk")!!.toInt())}"
            tvEstimasi.text = "Estimasi Hari : ${bundle?.getString("days")}"
            tvEstimasi.text = "Estimasi Hari : ${bundle?.getString("days")}"
            tvTotalHarga.text = RupiahConverter.rupiah(bundle?.getString("totalharga")!!.toInt())
        }

        binding.btnBack.setOnClickListener{
            requireActivity().onBackPressed()
        }
    }
}
