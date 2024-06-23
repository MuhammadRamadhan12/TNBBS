package com.app.tnbbs.view.user.etiket

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.tnbbs.R
import com.app.tnbbs.data.model.Wisata
import com.app.tnbbs.databinding.FragmentDetailWisataBinding
import com.app.tnbbs.utils.RupiahConverter
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DetailPesanWisataFragment : Fragment() {
    lateinit var binding : FragmentDetailWisataBinding
    private lateinit var databaseReference: DatabaseReference
    private var wisata: Wisata? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailWisataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idWisata = arguments?.getString("idWisata")

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("wisata")

        idWisata?.let { fetchWisata(it) }

        binding.btnPesan.setOnClickListener {
            wisata?.let {
                val bundle = Bundle().apply {
                    putString("namaWisata", it.namaWisata)
                    putString("deskripsi", it.deskripsi)
                    putString("hargaTiket", it.hargaTiket.toString())
                    putString("lokasi", it.lokasi)
                    putString("kuota", it.kuota.toString())
                    putString("tiketMasukWnaWeekDays", it.tiketMasukWnaWeekDays.toString())
                    putString("tiketMasukWnaWeekend", it.tiketMasukWnaWeekend.toString())
                    putString("tiketMasukWniWeekDays", it.tiketMasukWniWeekDays.toString())
                    putString("tiketMasukWniWeekend", it.tiketMasukWniWeekend.toString())
                    putString("imageUrl", it.imageUrl)
                    putString("idWisata", it.idWisata)
                    putString("generalPrice", it.generalPrice.toString())
                    putString("groupPrice", it.groupPrice.toString())
                    putString("hargaKarcisRombonganWnaHariKerja", it.hargaKarcisRombonganWnaHariKerja.toString())
                    putString("hargaKarcisRombonganWnaHariLibur", it.hargaKarcisRombonganWnaHariLibur.toString())
                    putString("hargaKarcisRombonganWniHariKerja", it.hargaKarcisRombonganWniHariKerja.toString())
                    putString("hargaKarcisRombonganWniHariLibur", it.hargaKarcisRombonganWniHariLibur.toString())
                }
                findNavController().navigate(R.id.action_detailWisataFragment_to_inputTransaksiFragment, bundle)
            }

        }
    }

    private fun fetchWisata(idWisata: String) {
        databaseReference.child(idWisata).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    wisata = snapshot.getValue(Wisata::class.java)
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
                .into(imgWisata)
            tvNamaWisata.text = wisata.namaWisata
            tvDeskripsi.text = wisata.deskripsi
            tvHarga.text = RupiahConverter.rupiah(wisata.hargaTiket)
            tvLokasi.text = wisata.lokasi
            tvTiketMasukWNI.text = "WNI : ${RupiahConverter.rupiah(wisata.tiketMasukWniWeekDays)} - ${RupiahConverter.rupiah(wisata.tiketMasukWniWeekend)}"
            tvTiketMasukWNA.text = "WNA : ${RupiahConverter.rupiah(wisata.tiketMasukWnaWeekDays)} - ${RupiahConverter.rupiah(wisata.tiketMasukWnaWeekend)}"

            Log.d("FetchedData", "Nama Wisata: ${wisata.namaWisata}")
            Log.d("FetchedData", "Deskripsi: ${wisata.deskripsi}")
            Log.d("FetchedData", "Harga Tiket: ${wisata.hargaTiket}")
            Log.d("FetchedData", "Lokasi: ${wisata.lokasi}")
            Log.d("FetchedData", "Tiket Masuk WNI WeekDays: ${wisata.tiketMasukWniWeekDays}")
            Log.d("FetchedData", "Tiket Masuk WNI Weekend: ${wisata.tiketMasukWniWeekend}")
            Log.d("FetchedData", "Tiket Masuk WNA WeekDays: ${wisata.tiketMasukWnaWeekDays}")
            Log.d("FetchedData", "Tiket Masuk WNA Weekend: ${wisata.tiketMasukWnaWeekend}")
        }
    }

}
