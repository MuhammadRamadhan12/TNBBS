package com.app.tnbbs.view.laporandananalisa

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.tnbbs.data.model.Laporan
import com.app.tnbbs.databinding.FragmentLaporanDanAnalisaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LaporanDanAnalisaFragment : Fragment() {
    lateinit var binding: FragmentLaporanDanAnalisaBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLaporanDanAnalisaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference = FirebaseDatabase.getInstance().reference.child("laporan")

        fetchDataFromFirebase()

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun fetchDataFromFirebase(dateString: String = getCurrentDate()) {
        databaseReference.child(dateString).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val laporan = dataSnapshot.getValue(Laporan::class.java)
                if (laporan != null) {
                    binding.etDate.setText(dateString)

                    binding.quotaAvailable.text = "${laporan.sisaTiketTersedia.toString()} sisa kuota"
                    binding.quotaCircleText.text = laporan.tiketTerjual.toString()
                } else {
                    binding.etDate.setText(dateString)

                    binding.quotaAvailable.text = "0 sisa kuota"
                    binding.quotaCircleText.text = "0"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // TODO
            }
        })
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(calendar.time)

            fetchDataFromFirebase(dateString)
        }

        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
