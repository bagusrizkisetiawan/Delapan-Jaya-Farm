package com.bagusrizki.delapanjayafarm.ui.screens.mitra.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagusrizki.delapanjayafarm.data.BobotSapi
import com.bagusrizki.delapanjayafarm.data.Jadwal
import com.bagusrizki.delapanjayafarm.data.LogDetail
import com.bagusrizki.delapanjayafarm.data.LogJadwal
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeMitraViewModel : ViewModel() {
    private val databaseMitra: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("mitra")

    private val databaseLog: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("log")
    private val databaseJadwal: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("jadwal")


    // Mutable state to hold the list of mitra
    private val _mitraList = MutableStateFlow<List<Mitra>>(emptyList())
    val mitraList: StateFlow<List<Mitra>> = _mitraList

    // Mutable state to hold the list of jadwal
    private val _jadwalList = MutableStateFlow<List<Jadwal>>(emptyList())
    val jadwalList: StateFlow<List<Jadwal>> = _jadwalList

    // Mutable state to hold the list of log
    private val _logList = MutableStateFlow<List<LogJadwal>>(emptyList())
    val logList: StateFlow<List<LogJadwal>> = _logList
    private val _logDayList = MutableStateFlow<List<LogDetail>>(emptyList())
    val logDayList: StateFlow<List<LogDetail>> = _logDayList
    private val _logDetailList = MutableStateFlow<List<LogDetail>>(emptyList())
    val logDetailList: StateFlow<List<LogDetail>> = _logDetailList

    private val _loadingList = MutableStateFlow<Boolean>(true)
    val loadingList: StateFlow<Boolean> = _loadingList



    init {
        // Automatically read data and detect new users
        viewModelScope.launch {
            _loadingList.value = true // Set loading to true when starting data fetch
            readMitra()
            readJadwal()
            readLog()
        }
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
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }



    private fun readLog() {
        databaseLog.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _log = mutableListOf<LogJadwal>()
                for (data in snapshot.children) {
                    val log = data.getValue(LogJadwal::class.java)
                    if (log != null) {
                        log.id = data.key ?: ""
                        _log.add(log)
                    }
                }
                _logList.value = _log
                combineDataLog()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Read jadwal from Firebase
    private fun readJadwal() {
        databaseJadwal.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jadwals = mutableListOf<Jadwal>()
                for (data in snapshot.children) {
                    val jadwal = data.getValue(Jadwal::class.java)
                    if (jadwal != null) {
                        jadwal.id = data.key ?: ""
                        jadwals.add(jadwal)
                    }
                }
                _jadwalList.value = jadwals
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun readLogDayList() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            _logDayList.value = logDetailList.value.filter { it.tanggal ==  currentDate}
        }
    }

    private fun combineDataLog(){
        viewModelScope.launch {
            val logList = _logList.value
            val mitraList = _mitraList.value
            val jadwalList = _jadwalList.value

            val combinedData = logList.map { log ->
                val mitra = mitraList.find { it.id == log.idMitra } ?: Mitra()
                val jadwal = jadwalList.find { it.id == log.idJadwal } ?: Jadwal()

                LogDetail(
                    id = log.id,
                    idMitra = log.idMitra,
                    idJadwal = log.idJadwal,
                    tanggal = log.tanggal,
                    jam = log.jam,
                    status = log.status,
                    mitra = mitra,
                    jadwal = jadwal
                )
            }
            _logDetailList.value = combinedData
            readLogDayList()
        }
    }


}
