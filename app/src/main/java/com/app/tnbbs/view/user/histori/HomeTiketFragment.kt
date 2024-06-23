package com.app.tnbbs.view.user.histori

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tnbbs.data.datastore.SharedPref
import com.app.tnbbs.data.model.Histori
import com.app.tnbbs.databinding.FragmentHomeTiketBinding
import com.app.tnbbs.view.user.histori.adapter.TiketAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class HomeTiketFragment : Fragment() {
    private lateinit var binding: FragmentHomeTiketBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPref: SharedPref
    private var userId = ""
    private var role = ""
    private val historiList: MutableList<Histori> = mutableListOf()
    private lateinit var historiAdapter: TiketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeTiketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = SharedPref(requireContext())
        databaseReference = FirebaseDatabase.getInstance().reference
        historiAdapter = TiketAdapter(requireContext(), historiList)

        binding.rvHistoriTiket.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historiAdapter
        }

        lifecycleScope.launch {
            sharedPref.getAllUserData.collect {
                userId = it.userId
                role = it.role

                fetchAndDisplayHistori(userId, role)
            }
        }
    }

    private fun fetchAndDisplayHistori(userId: String, role: String) {
        historiList.clear()
        if (role == "admin") {
            val historiRef = databaseReference.child("histori")
            historiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val histori = childSnapshot.getValue(Histori::class.java)
                        histori?.let { historiList.add(it) }
                    }
                    historiAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // TODO
                }
            })
        } else {
            val historiRef = databaseReference.child("histori").child(userId)
            historiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val histori = childSnapshot.getValue(Histori::class.java)
                        histori?.let { historiList.add(it) }
                    }
                    historiAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // TODO
                }
            })
        }
    }
}
