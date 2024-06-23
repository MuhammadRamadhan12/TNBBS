package com.app.tnbbs.view.admin.kelolawisata

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.tnbbs.R
import com.app.tnbbs.data.model.Wisata
import com.app.tnbbs.databinding.FragmentDetailInformasiWisataAdminBinding
import com.app.tnbbs.utils.RupiahConverter
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class DetailInformasiWisataAdminFragment : Fragment() {
    lateinit var binding: FragmentDetailInformasiWisataAdminBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailInformasiWisataAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idWisata = arguments?.getString("idWisata")

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("wisata")

        binding.btnHapus.setOnClickListener {
            idWisata?.let { id ->
                val wisataRef = databaseReference.child(id)
                wisataRef.removeValue()
                    .addOnSuccessListener {
                        activity?.onBackPressed()
                    }
                    .addOnFailureListener { e ->
                        Log.e("DeleteError", "Error deleting tourist attraction: ${e.message}")
                    }
            }
        }

        binding.btnEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("idWisata", idWisata)
            findNavController().navigate(R.id.action_detailInformasiWisataAdminFragment_to_updateWisataFragment, bundle)
        }

        idWisata?.let { fetchWisata(it) }
    }

    private fun fetchWisata(idWisata: String) {
        databaseReference.child(idWisata).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val wisata = snapshot.getValue(Wisata::class.java)
                    wisata?.let {
                        displayWisata(it)
                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error fetching document", exception)
            }
    }

    private fun displayWisata(wisata: Wisata) {
        binding.apply {
            Glide.with(requireContext())
                .load(wisata.imageUrl)
                .into(ivWisata)
            tvNamaWisata.text = wisata.namaWisata
            tvDeskripsi.text = wisata.deskripsi
            tvHarga.text = RupiahConverter.rupiah(wisata.hargaTiket)
            tvLokasi.text = wisata.lokasi
            tvTiketMasukWNI.text = "WNI : ${RupiahConverter.rupiah(wisata.tiketMasukWniWeekDays)} - ${RupiahConverter.rupiah(wisata.tiketMasukWniWeekend)}"
            tvTiketMasukWNA.text = "WNA : ${RupiahConverter.rupiah(wisata.tiketMasukWnaWeekDays)} - ${RupiahConverter.rupiah(wisata.tiketMasukWnaWeekend)}"
        }
    }
}
