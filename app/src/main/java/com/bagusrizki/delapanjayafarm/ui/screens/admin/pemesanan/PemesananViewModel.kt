package com.bagusrizki.delapanjayafarm.ui.screens.admin.pemesanan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.data.PemesananDetail
import com.bagusrizki.delapanjayafarm.data.PemesananPakan
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PemesananViewModel:ViewModel() {
    private val databasePemesanan: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("pemesanan")
    private val databaseMitra: DatabaseReference = FirebaseDatabase.getInstance().reference.child("mitra")


    // Mutable state to hold the list of pemesanan
    private val _pemesananList = MutableStateFlow<List<PemesananPakan>>(emptyList())
    val pemesananList: StateFlow<List<PemesananPakan>> = _pemesananList

    private val _pemesananDetailList = MutableStateFlow<List<PemesananDetail>>(emptyList())
    val pemesananDetailList: StateFlow<List<PemesananDetail>> = _pemesananDetailList

    private val _mitraList = MutableStateFlow<List<Mitra>>(emptyList())
    val mitraList: StateFlow<List<Mitra>> = _mitraList



    init {
        viewModelScope.launch {
            readPemesanan()
            readMitra()
        }

        // Kombinasikan data secara otomatis setiap kali salah satu berubah
        viewModelScope.launch {
            combine(_pemesananList, _mitraList) { pemesananList, mitraList ->
                pemesananList.map { pemesanan ->
                    val mitra = mitraList.find { it.id == pemesanan.idMitra } ?: Mitra()

                    PemesananDetail(
                        idPemesanan = pemesanan.idPemesanan,
                        jenisPemesanan = pemesanan.jenisPemesanan,
                        keteranganPemesanan = pemesanan.keteranganPemesanan,
                        idMitra = pemesanan.idMitra,
                        tanggalPemesanan = pemesanan.tanggalPemesanan,
                        statusPemesanan = pemesanan.statusPemesanan,
                        estimasiPemesanan = pemesanan.estimasiPemesanan,
                        mitra = mitra
                    )
                }
            }.collect {
                _pemesananDetailList.value = it
            }
        }

    }

    private fun readPemesanan() {
        databasePemesanan.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _pemesanan = mutableListOf<PemesananPakan>()
                for (data in snapshot.children) {
                    val pemesanan = data.getValue(PemesananPakan::class.java)
                    if (pemesanan != null) {
                        pemesanan.idPemesanan = data.key ?: ""
                        _pemesanan.add(pemesanan)
                    }
                }
                _pemesananList.value = _pemesanan
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun readMitra() {
        databaseMitra.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _mitra = mutableListOf<Mitra>()
                for (data in snapshot.children) {
                    val mitra = data.getValue(Mitra::class.java)
                    if (mitra != null) {
                        mitra.id = data.key ?: ""
                        _mitra.add(mitra)
                    }
                }
                _mitraList.value = _mitra
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Delete from Firebase
    fun deletePemesanan(pemesananPakan: PemesananPakan) {
        databasePemesanan.child(pemesananPakan.idPemesanan).removeValue()
    }

    // Update  in Firebase
    fun updatePemesanan(pemesananPakan: PemesananPakan) {
        val pemesananId = pemesananPakan.idPemesanan
        if (pemesananId.isNotEmpty()) {
            databasePemesanan.child(pemesananId).setValue(pemesananPakan)
        }
    }
}