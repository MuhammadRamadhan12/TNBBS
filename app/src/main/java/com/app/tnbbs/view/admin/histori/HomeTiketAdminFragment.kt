package com.app.tnbbs.view.admin.histori

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tnbbs.data.datastore.SharedPref
import com.app.tnbbs.data.model.Histori
import com.app.tnbbs.databinding.FragmentHomeTiketAdminBinding
import com.app.tnbbs.view.admin.histori.adapter.TiketAdapterAdmin
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeTiketAdminFragment : Fragment() {
    private lateinit var binding: FragmentHomeTiketAdminBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPref: SharedPref
    private var userId = ""
    private var role = ""
    private val historiList: MutableList<Histori> = mutableListOf()
    private lateinit var historiAdapter: TiketAdapterAdmin
    private lateinit var historyLoader: HistoryLoader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeTiketAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = SharedPref(requireContext())
        databaseReference = FirebaseDatabase.getInstance().reference
        historyLoader = HistoryLoader()

        historiAdapter = TiketAdapterAdmin(requireContext(), historiList)

        binding.rvHistoriTiket.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historiAdapter
        }

        fetchAndDisplayHistori()
    }

    private fun fetchAndDisplayHistori() {
        historyLoader.loadAllHistoryData { history ->
            historiList.clear()
            historiList.addAll(history)
            historiAdapter.notifyDataSetChanged()
        }
    }
}
