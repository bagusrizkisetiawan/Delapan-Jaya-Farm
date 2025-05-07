package com.bagusrizki.delapanjayafarm.ui.screens.admin.home

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SapiViewModel : ViewModel() {
    private val databaseMitra = FirebaseDatabase.getInstance().reference.child("mitra")
    private val databaseSapi = FirebaseDatabase.getInstance().reference.child("sapi")
    private val databaseBobotSapi = FirebaseDatabase.getInstance().reference.child("bobotSapi")
    private val databaseHargaSapi = FirebaseDatabase.getInstance().reference.child("hargaSapi")

    private val _mitraList = MutableStateFlow<List<Mitra>>(emptyList())
    val mitraList: StateFlow<List<Mitra>> = _mitraList

    private val _bobotSapiList = MutableStateFlow<List<BobotSapi>>(emptyList())
    val bobotSapiList: StateFlow<List<BobotSapi>> = _bobotSapiList

    private val _sapiList = MutableStateFlow<List<Sapi>>(emptyList())
    val sapiList: StateFlow<List<Sapi>> = _sapiList

    private val _sapiDetailList = MutableStateFlow<List<SapiDetail>>(emptyList())
    val sapiDetailList: StateFlow<List<SapiDetail>> = _sapiDetailList

    private val _listHargaSapi = MutableStateFlow<List<HargaSapi>>(emptyList())
    val listHargaSapi: StateFlow<List<HargaSapi>> = _listHargaSapi

    private val _openDialogSapi = MutableStateFlow<Boolean>(false)
    val openDialogSapi: StateFlow<Boolean> = _openDialogSapi

    init {
        viewModelScope.launch {
            readMitra()
            readSapi()
            readBobotSapi()
            readHargaSapi()
            observeDataChanges()
        }
    }

    private fun readMitra() {
        databaseMitra.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _mitraList.value = snapshot.children.mapNotNull { data ->
                    data.getValue(Mitra::class.java)?.apply { id = data.key ?: "" }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun readSapi() {
        databaseSapi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _sapiList.value = snapshot.children.mapNotNull { data ->
                    data.getValue(Sapi::class.java)?.apply { idSapi = data.key ?: "" }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun readBobotSapi() {
        databaseBobotSapi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _bobotSapiList.value = snapshot.children.mapNotNull { data ->
                    data.getValue(BobotSapi::class.java)?.apply { idBobotSapi = data.key ?: "" }
                }
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

    private fun observeDataChanges() {
        viewModelScope.launch {
            combine(_sapiList, _mitraList, _bobotSapiList) { sapiList, mitraList, bobotSapiList ->
                sapiList.map { sapi ->
                    SapiDetail(
                        idSapi = sapi.idSapi,
                        idMitra = sapi.idMitra,
                        namaSapi = sapi.namaSapi,
                        jenisSapi = sapi.jenisSapi,
                        tanggalPemeliharaan = sapi.tanggalPemeliharaan,
                        imageSapi = sapi.imageSapi,
                        statusSapi = sapi.statusSapi,
                        keteranganSapi = sapi.keteranganSapi,
                        mitra = mitraList.find { it.id == sapi.idMitra } ?: Mitra(),
                        bobot = bobotSapiList.filter { it.idSapi == sapi.idSapi }
                    )
                }
            }.collect { combinedData ->
                _sapiDetailList.value = combinedData
            }
        }
    }

    fun addSapi(sapi: Sapi, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        val sapiId = databaseSapi.push().key ?: return onError(Exception("Gagal membuat ID"))
        sapi.idSapi = sapiId
        databaseSapi.child(sapiId).setValue(sapi)
            .addOnSuccessListener { onSuccess(sapiId) }
            .addOnFailureListener { onError(it) }
    }

    fun addBobot(bobotSapi: BobotSapi) {
        val bobotSapiId = databaseBobotSapi.push().key ?: return
        bobotSapi.idBobotSapi = bobotSapiId
        databaseBobotSapi.child(bobotSapiId).setValue(bobotSapi)
    }

    fun updateHargaSapi(hargaSapi: HargaSapi) {
        databaseHargaSapi.child("-OJLwqbTep0JAw7RsRmc").setValue(hargaSapi)
    }

    fun updateSapi(sapi: Sapi) {
        if (sapi.idSapi.isNotEmpty()) {
            databaseSapi.child(sapi.idSapi).setValue(sapi)
        }
    }

    fun deleteSapi(sapi: Sapi) {
        databaseSapi.child(sapi.idSapi).removeValue()
    }

    fun deleteBobot(bobotSapi: BobotSapi) {
        databaseBobotSapi.child(bobotSapi.idBobotSapi).removeValue()
    }
}