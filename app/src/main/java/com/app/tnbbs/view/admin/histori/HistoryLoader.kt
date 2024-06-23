package com.app.tnbbs.view.admin.histori

import android.util.Log
import com.app.tnbbs.data.model.Histori
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryLoader {
    fun loadAllHistoryData(callback: (List<Histori>) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("histori")
        val historiList: MutableList<Histori> = mutableListOf()

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    for (historiSnapshot in userSnapshot.children) {
                        val histori = historiSnapshot.getValue(Histori::class.java)
                        histori?.let {
                            historiList.add(it)
                        }
                    }
                }
                callback(historiList)

                historiList.forEach { histori ->
                    Log.d("HistoryLoader", "Histori: $histori")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("HistoryLoader", "Failed to load histori data: $databaseError")
                // TODO
            }
        })
    }
}


