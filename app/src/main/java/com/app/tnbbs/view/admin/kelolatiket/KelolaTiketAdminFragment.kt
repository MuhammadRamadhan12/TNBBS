package com.app.tnbbs.view.admin.kelolatiket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tnbbs.data.datastore.SharedPref
import com.app.tnbbs.data.model.Histori
import com.app.tnbbs.databinding.FragmentKelolaTiketAdminBinding
import com.app.tnbbs.view.admin.kelolatiket.adapter.KelolaTiketAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class KelolaTiketAdminFragment : Fragment() {
    private lateinit var binding: FragmentKelolaTiketAdminBinding
    private lateinit var sharedPref: SharedPref
    private val pendingHistoriList: MutableList<Histori> = mutableListOf()
    private lateinit var pendingTiketAdapter: KelolaTiketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKelolaTiketAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = SharedPref(requireContext())
        pendingTiketAdapter = KelolaTiketAdapter(requireContext(), pendingHistoriList)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.rvTiket.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pendingTiketAdapter
        }

        loadPendingHistoriData()
    }

    private fun loadPendingHistoriData() {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("histori")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pendingHistoriList.clear()

                for (userSnapshot in dataSnapshot.children) {
                    for (historiSnapshot in userSnapshot.children) {
                        val histori = historiSnapshot.getValue(Histori::class.java)
                        histori?.let {
                            it.idBooking = historiSnapshot.key
                            if (it.statusPesanan == "pending") {
                                pendingHistoriList.add(it)
                            }
                        }
                    }
                }
                pendingTiketAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // TODO
            }
        })
    }

}
