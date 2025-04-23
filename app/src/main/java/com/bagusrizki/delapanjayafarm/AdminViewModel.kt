package com.bagusrizki.delapanjayafarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagusrizki.delapanjayafarm.data.Admin
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val databaseAdmin: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("admin")

    // Mutable state to hold the list of admin
    private val _adminList = MutableStateFlow<List<Admin>>(emptyList())
    val adminList: StateFlow<List<Admin>> = _adminList

    init {
        // Automatically read data and detect new users
        viewModelScope.launch {
            readAdmin()
        }
    }

    // Read Admin from Firebase
    private fun readAdmin() {
        databaseAdmin.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<Admin>()
                for (data in snapshot.children) {
                    val admin = data.getValue(Admin::class.java)
                    if (admin != null) {
                        admin.id = data.key ?: ""
                        users.add(admin)
                    }
                }
                _adminList.value = users

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


}