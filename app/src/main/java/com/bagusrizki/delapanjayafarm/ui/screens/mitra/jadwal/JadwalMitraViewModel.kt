package com.bagusrizki.delapanjayafarm.ui.screens.mitra.jadwal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bagusrizki.delapanjayafarm.data.Jadwal
import com.bagusrizki.delapanjayafarm.data.LogDetail
import com.bagusrizki.delapanjayafarm.data.LogJadwal
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.notification.JadwalReaderWorker
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
import java.util.concurrent.TimeUnit

class JadwalMitraViewModel : ViewModel() {

    private val databaseJadwal: DatabaseReference = FirebaseDatabase.getInstance().reference.child("jadwal")
    private val databaseMitra: DatabaseReference = FirebaseDatabase.getInstance().reference.child("mitra")
    private val databaseLog: DatabaseReference = FirebaseDatabase.getInstance().reference.child("log")

    private val _jadwalList = MutableStateFlow<List<Jadwal>>(emptyList())
    val jadwalList: StateFlow<List<Jadwal>> = _jadwalList

    private val _mitraList = MutableStateFlow<List<Mitra>>(emptyList())
    val mitraList: StateFlow<List<Mitra>> = _mitraList

    private val _logList = MutableStateFlow<List<LogJadwal>>(emptyList())
    val logList: StateFlow<List<LogJadwal>> = _logList

    private val _logDetailList = MutableStateFlow<List<LogDetail>>(emptyList())
    val logDetailList: StateFlow<List<LogDetail>> = _logDetailList

    private var jadwalListener: ValueEventListener? = null
    private var mitraListener: ValueEventListener? = null
    private var logListener: ValueEventListener? = null

    init {
        viewModelScope.launch {
            readJadwal()
            readMitra()
            scheduleJadwalReading()
            readLog()
        }

        viewModelScope.launch {
            mitraList.collect { mitraData ->
                if (mitraData.isNotEmpty() && jadwalList.value.isNotEmpty()) {
                    addLogsForToday()
                }
            }
        }
    }

    private fun readJadwal() {
        jadwalListener?.let { databaseJadwal.removeEventListener(it) } // Hapus listener sebelumnya jika ada
        jadwalListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newJadwals = snapshot.children.mapNotNull { data ->
                    data.getValue(Jadwal::class.java)?.apply { id = data.key.orEmpty() }
                }
                if (_jadwalList.value != newJadwals) {  // Hanya update jika ada perubahan
                    _jadwalList.value = newJadwals
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        databaseJadwal.addValueEventListener(jadwalListener!!)
    }

    private fun readMitra() {
        mitraListener?.let { databaseMitra.removeEventListener(it) }
        mitraListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newMitras = snapshot.children.mapNotNull { data ->
                    data.getValue(Mitra::class.java)?.apply { id = data.key.orEmpty() }
                }
                if (_mitraList.value != newMitras) {
                    _mitraList.value = newMitras
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        databaseMitra.addValueEventListener(mitraListener!!)
    }

    private fun readLog() {
        logListener?.let { databaseLog.removeEventListener(it) }
        logListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newLogs = snapshot.children.mapNotNull { data ->
                    data.getValue(LogJadwal::class.java)?.apply { id = data.key.orEmpty() }
                }
                if (_logList.value != newLogs) {
                    _logList.value = newLogs
                    combineDataLog()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        databaseLog.addValueEventListener(logListener!!)
    }

    // Schedule periodic reading of jadwal
    private fun scheduleJadwalReading() {
        val workRequest = PeriodicWorkRequestBuilder<JadwalReaderWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance().enqueue(workRequest)
    }

    // Add logs for today's schedule
    private fun addLogsForToday() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        mitraList.value.forEach { mitra ->
            jadwalList.value.forEach { jadwal ->
                val logId = "${mitra.id}-${jadwal.id}-$currentDate"
                databaseLog.child(logId).get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        val log = LogJadwal(
                            id = logId,
                            idMitra = mitra.id,
                            idJadwal = jadwal.id,
                            tanggal = currentDate,
                            jam = "--:--",
                            status = "Belum"
                        )
                        databaseLog.child(logId).setValue(log)
                    }
                }
            }
        }
    }

    private fun combineDataLog() {
        viewModelScope.launch {
            val combinedData = _logList.value.map { log ->
                val mitra = _mitraList.value.find { it.id == log.idMitra } ?: Mitra()
                val jadwal = _jadwalList.value.find { it.id == log.idJadwal } ?: Jadwal()

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
            if (_logDetailList.value != combinedData) {
                _logDetailList.value = combinedData
            }
        }
    }

    // Update jadwal in Firebase
    fun updateLog(log: LogJadwal) {
        if (log.id.isNotEmpty()) {
            databaseLog.child(log.id).setValue(log)
        }
    }

    override fun onCleared() {
        super.onCleared()
        jadwalListener?.let { databaseJadwal.removeEventListener(it) }
        mitraListener?.let { databaseMitra.removeEventListener(it) }
        logListener?.let { databaseLog.removeEventListener(it) }
    }
}

