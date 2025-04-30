package com.bagusrizki.delapanjayafarm.ui.screens.mitra.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagusrizki.delapanjayafarm.data.BobotSapi
import com.bagusrizki.delapanjayafarm.data.HargaSapi
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.data.Sapi
import com.bagusrizki.delapanjayafarm.data.SapiDetail
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SapiMitraViewModel:ViewModel(){
    private val databaseSapi: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("sapi")
    private val databaseBobotSapi: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("bobotSapi")
    private val databaseMitra: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("mitra")
    private val databaseHargaSapi = FirebaseDatabase.getInstance().reference.child("hargaSapi")


    // Mutable state to hold the list of sapi
    private val _sapiList = MutableStateFlow<List<Sapi>>(emptyList())
    val sapiList: StateFlow<List<Sapi>> = _sapiList

    // Mutable state to hold the list of mitra
    private val _mitraList = MutableStateFlow<List<Mitra>>(emptyList())
    val mitraList: StateFlow<List<Mitra>> = _mitraList

    // Mutable state to hold the list of bobot sapi
    private val _bobotSapiList = MutableStateFlow<List<BobotSapi>>(emptyList())
    val bobotSapiList: StateFlow<List<BobotSapi>> = _bobotSapiList

    // Mutable state to hold the combined data of SapiDetail
    private val _sapiDetailList = MutableStateFlow<List<SapiDetail>>(emptyList())
    val sapiDetailList: StateFlow<List<SapiDetail>> = _sapiDetailList

    private val _listHargaSapi = MutableStateFlow<List<HargaSapi>>(emptyList())
    val listHargaSapi: StateFlow<List<HargaSapi>> = _listHargaSapi

    init {
        // Automatically read data and detect new users
        viewModelScope.launch {
            readSapi()
            readBobotSapi()
            readMitra()
            readHargaSapi()
        }
    }


    private fun readSapi() {
        databaseSapi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _sapi = mutableListOf<Sapi>()
                for (data in snapshot.children) {
                    val sapi = data.getValue(Sapi::class.java)
                    if (sapi != null) {
                        sapi.idSapi = data.key ?: ""
                        _sapi.add(sapi)
                    }
                }
                _sapiList.value = _sapi
                combineData() // After reading Sapi data, combine the data
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun readBobotSapi() {
        databaseBobotSapi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _bobotSapi = mutableListOf<BobotSapi>()
                for (data in snapshot.children) {
                    val bobotSsapi = data.getValue(BobotSapi::class.java)
                    if (bobotSsapi != null) {
                        bobotSsapi.idBobotSapi = data.key ?: ""
                        _bobotSapi.add(bobotSsapi)
                    }
                }
                _bobotSapiList.value = _bobotSapi
                combineData() // After reading BobotSapi data, combine the data
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

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
                combineData() // After reading Mitra data, combine the data
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun readHargaSapi() {
        databaseHargaSapi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _listHargaSapi.value =
                    snapshot.children.mapNotNull { data -> data.getValue(HargaSapi::class.java) }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    // Combine all data once all values are fetched
    private fun combineData() {
        viewModelScope.launch {
            val sapiList = _sapiList.value
            val mitraList = _mitraList.value
            val bobotSapiList = _bobotSapiList.value

            // Ensure all necessary data is loaded before combining
            if (sapiList.isNotEmpty() && mitraList.isNotEmpty() && bobotSapiList.isNotEmpty()) {
                val combinedData = sapiList.map { sapi ->
                    val mitra = mitraList.find { it.id == sapi.idMitra } ?: Mitra()
                    val bobot = bobotSapiList.filter { it.idSapi == sapi.idSapi }
                    SapiDetail(
                        idSapi = sapi.idSapi,
                        idMitra = sapi.idMitra,
                        namaSapi = sapi.namaSapi,
                        jenisSapi = sapi.jenisSapi,
                        tanggalPemeliharaan = sapi.tanggalPemeliharaan,
                        imageSapi = sapi.imageSapi,
                        statusSapi = sapi.statusSapi,
                        keteranganSapi = sapi.keteranganSapi,
                        mitra = mitra,
                        bobot = bobot
                    )
                }
                _sapiDetailList.value = combinedData
            }
        }
    }

    // Add sapi to Firebase
    fun addSapi(sapi: Sapi, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        val sapiId = databaseSapi.push().key // Buat ID unik untuk sapi
        if (sapiId != null) {
            sapi.idSapi = sapiId // Set ID ke objek sapi
            databaseSapi.child(sapiId).setValue(sapi)
                .addOnSuccessListener {
                    onSuccess(sapiId) // Kembalikan idSapi setelah sukses menambahkan
                }
                .addOnFailureListener { exception ->
                    onError(exception) // Tangani error
                }
        } else {
            onError(Exception("Gagal membuat ID untuk sapi")) // Tangani kasus null ID
        }
    }

    // Add bobot to Firebase
    fun addBobot(bobotSapi: BobotSapi) {
        val BobotSapiId = databaseBobotSapi.push().key
        BobotSapiId?.let {
            bobotSapi.idBobotSapi = it // Set the ID here before saving
            databaseBobotSapi.child(it).setValue(bobotSapi)
        }
    }
}
