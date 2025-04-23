package com.bagusrizki.delapanjayafarm.ui.screens.admin.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagusrizki.delapanjayafarm.data.Admin
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {
    private val databaseAdmin: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("admin")
    private val databaseMitra: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("mitra")

    // Mutable state to hold the list of admin
    private val _adminList = MutableStateFlow<List<Admin>>(emptyList())
    val adminList: StateFlow<List<Admin>> = _adminList

    // Mutable state to hold the list of mitra
    private val _mitraList = MutableStateFlow<List<Mitra>>(emptyList())
    val mitraList: StateFlow<List<Mitra>> = _mitraList

    // Loading state
    val isLoading = MutableStateFlow(true)

    init {
        // Automatically read data and detect new users
        viewModelScope.launch {
            readAdmin()
            readMitra()
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
                isLoading.value = false

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Add admin to Firebase
    fun addAdmin(admin: Admin) {
        val adminId = databaseAdmin.push().key
        adminId?.let {
            admin.id = it // Set the ID here before saving
            databaseAdmin.child(it).setValue(admin)
        }
    }

    // Delete admin from Firebase
    fun deleteAdmin(admin: Admin) {
        databaseAdmin.child(admin.id).removeValue()
    }

    // Update admin in Firebase
    fun updateAdmin(admin: Admin) {
        val adminId = admin.id
        if (adminId.isNotEmpty()) {
            databaseAdmin.child(adminId).setValue(admin)
        }
    }


    ////// mitra

    // Read Mitra from Firebase
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
                isLoading.value = false

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Add mitra to Firebase
    fun addMitra(mitra: Mitra) {
        val adminId = databaseMitra.push().key
        adminId?.let {
            mitra.id = it // Set the ID here before saving
            databaseMitra.child(it).setValue(mitra)
        }
    }

    // Delete mitra from Firebase
    fun deleteMitra(mitra: Mitra) {
        databaseMitra.child(mitra.id).removeValue()
    }

    // Update mitra in Firebase
    fun updateMitra(mitra: Mitra) {
        val mitraId = mitra.id
        if (mitraId.isNotEmpty()) {
            databaseMitra.child(mitraId).setValue(mitra)
        }
    }

}