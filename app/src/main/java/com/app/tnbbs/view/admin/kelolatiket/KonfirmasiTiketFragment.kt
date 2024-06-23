package com.app.tnbbs.view.admin.kelolatiket

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.tnbbs.databinding.FragmentKonfirmasiTiketBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KonfirmasiTiketFragment : Fragment() {
    lateinit var binding: FragmentKonfirmasiTiketBinding
    private val TAG = "KonfirmasiTiketFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentKonfirmasiTiketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val idBooking = bundle?.getString("idBooking")
        val idWisata = bundle?.getString("idWisata")
        val namaPemesan = bundle?.getString("namaPemesan")
        val noHp = bundle?.getString("noHp")
        val namaWisata = bundle?.getString("namaWisata")
        val kewarganegaraan = bundle?.getString("kewarganegaraan")
        val jumlahTiket = bundle?.getString("jumlahTiket")
        val tanggalPemesanan = bundle?.getString("tanggalPemesanan")
        val statusPemesanan = bundle?.getString("statusPemesanan")
        val lihatBukti = bundle?.getString("lihatBukti")
        val day = bundle?.getString("days")

        Log.d(TAG, "Received bundle: $bundle")
        Log.d(TAG, "idBooking: $idBooking")


        binding.apply {
            etNamaPemesan.setText(namaPemesan)
            etNoTelepon.setText(noHp)
            tvTiket.text = namaWisata
            tvKewarganegaraan.text = kewarganegaraan
            etJumlahTiket.setText(jumlahTiket)
            etTanggalPesan.setText(tanggalPemesanan)
            etStatusPembayaran.setText(statusPemesanan)
            etLamaKunjungan.setText(day)
            btnLihatBukti.setOnClickListener {
                lihatBukti?.let { imageUrl ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(imageUrl)
                    startActivity(intent)
                }
            }

            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }

            btnKonfirmasi.setOnClickListener {
                Log.d(TAG, "Konfirmasi button clicked")
                Log.d("Booking ID : ", idBooking.toString())
                Log.d("Nama Pemesan : ", namaPemesan.toString())
                updateBookingStatus(idBooking, "sukses")
            }
        }
    }

    private fun updateBookingStatus(bookingId: String?, newStatus: String) {
        if (bookingId == null) {
            Log.e(TAG, "Booking ID is null")
            return
        }

        val database = FirebaseDatabase.getInstance()
        val bookingRef = database.getReference("histori")

        bookingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "DataSnapshot received: ${snapshot.key}")
                for (userSnapshot in snapshot.children) {
                    Log.d(TAG, "User ID: ${userSnapshot.key}")
                    for (bookingSnapshot in userSnapshot.children) {
                        Log.d(TAG, "Booking ID: ${bookingSnapshot.key}")
                        if (bookingSnapshot.key == bookingId) {
                            Log.d(TAG, "Booking ID matches")
                            val bookingStatusRef = bookingSnapshot.child("statusPesanan").ref
                            bookingStatusRef.setValue(newStatus)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Berhasil mengkonfirmasi pesanan", Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, "Status updated successfully")

                                    createOrUpdateReport(database, bookingSnapshot)

                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Failed to update status", e)
                                }
                            return
                        }
                    }
                }
                Log.e(TAG, "Booking ID not found")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error accessing data", error.toException())
            }
        })
    }

    private fun createOrUpdateReport(database: FirebaseDatabase, bookingSnapshot: DataSnapshot) {
        val idWisata = bookingSnapshot.child("idWisata").getValue(String::class.java)
        val jumlahTiket = bookingSnapshot.child("jumlahOrang").getValue(String::class.java)?.toInt()

        idWisata?.let { wisataId ->
            jumlahTiket?.let { jumlah ->
                val wisataRef = database.getReference("wisata").child(wisataId)
                wisataRef.child("kuota").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentKuota = snapshot.getValue(Int::class.java) ?: 0
                        val newKuota = currentKuota - jumlah
                        wisataRef.child("kuota").setValue(newKuota)
                            .addOnSuccessListener {
                                Log.d(TAG, "Kuota updated successfully")
                                val currentDate = getCurrentDate()
                                updateReport(database, currentDate, jumlah)
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Failed to update kuota", e)
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Error accessing data", error.toException())
                    }
                })
            }
        }
    }

    private fun updateReport(database: FirebaseDatabase, currentDate: String, jumlahTiket: Int) {
        val laporanRef = database.getReference("laporan").child(currentDate)
        laporanRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentTiketTerjual = dataSnapshot.child("tiketTerjual").getValue(Int::class.java) ?: 0
                val newTiketTerjual = currentTiketTerjual + jumlahTiket

                laporanRef.child("tiketTerjual").setValue(newTiketTerjual)
                    .addOnSuccessListener {
                        Log.d(TAG, "tiketTerjual updated successfully")

                        updateAvailableTickets(database, laporanRef, newTiketTerjual)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to update tiketTerjual: $e")
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Cancelled updating report: $databaseError")
            }
        })
    }

    private fun updateAvailableTickets(database: FirebaseDatabase, laporanRef: DatabaseReference, newTiketTerjual: Int) {
        val wisataRef = database.getReference("wisata")
        wisataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalKuotaWisata = 0
                for (wisataSnapshot in dataSnapshot.children) {
                    val kuota = wisataSnapshot.child("kuota").getValue(Int::class.java) ?: 0
                    totalKuotaWisata += kuota
                }

                val sisaTiketTersedia = totalKuotaWisata

                laporanRef.child("sisaTiketTersedia").setValue(sisaTiketTersedia)
                    .addOnSuccessListener {
                        Log.d(TAG, "sisaTiketTersedia updated successfully")
                        findNavController().navigateUp()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to update sisaTiketTersedia: $e")
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Cancelled updating available tickets: $databaseError")
            }
        })
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
