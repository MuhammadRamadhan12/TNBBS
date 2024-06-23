package com.app.tnbbs.view.admin.kelolawisata

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.tnbbs.R
import com.app.tnbbs.data.datastore.SharedPref
import com.app.tnbbs.data.model.Wisata
import com.app.tnbbs.databinding.FragmentInformasiWisataAdminBinding
import com.app.tnbbs.view.admin.kelolawisata.adapter.WisataAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InformasiWisataAdminFragment : Fragment() {
    lateinit var binding: FragmentInformasiWisataAdminBinding
    private lateinit var wisataAdapter: WisataAdapter
    private lateinit var sharedPref: SharedPref
    private lateinit var databaseReference: DatabaseReference
    private var originalWisataList: MutableList<Wisata> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInformasiWisataAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPref(requireContext())
        databaseReference = FirebaseDatabase.getInstance().getReference("wisata")

        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        wisataAdapter = WisataAdapter(requireContext(), mutableListOf())
        binding.recyclerView.adapter = wisataAdapter

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchWisataFromFirebase()
        }

        binding.btnTambahWisata.setOnClickListener {
            findNavController().navigate(R.id.action_informasiWisataAdminFragment_to_tambahWisataFragment)
        }

        binding.swipeRefreshLayout.isRefreshing = true
        fetchWisataFromFirebase()
    }

    private fun fetchWisataFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                originalWisataList.clear()
                for (dataSnapshot in snapshot.children) {
                    val wisata = dataSnapshot.getValue(Wisata::class.java)
                    wisata?.let { originalWisataList.add(it) }
                }
                wisataAdapter.updateData(originalWisataList)
                binding.swipeRefreshLayout.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }
}
