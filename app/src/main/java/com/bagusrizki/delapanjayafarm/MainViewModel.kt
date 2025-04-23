package com.bagusrizki.delapanjayafarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {
    private val databaseMitra: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("mitra")

    // Mutable state to hold the list of admin
    private val _mitraList = MutableStateFlow<List<Mitra>>(emptyList())
    val mitraList: StateFlow<List<Mitra>> = _mitraList

    init {
        // Automatically read data and detect new users
        viewModelScope.launch {
            readMitra()
        }
    }

    // Read Admin from Firebase
    private fun readMitra() {
        databaseMitra.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<Mitra>()
                for (data in snapshot.children) {
                    val mitra = data.getValue(Mitra::class.java)
                    if (mitra != null) {
                        mitra.id = data.key ?: ""
                        users.add(mitra)
                    }
                }
                _mitraList.value = users

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}