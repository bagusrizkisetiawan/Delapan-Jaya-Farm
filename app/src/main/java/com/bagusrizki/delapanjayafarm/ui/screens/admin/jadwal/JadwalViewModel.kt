package com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal

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

class JadwalViewModel : ViewModel() {

    // Firebase database references
    private val databaseJadwal: DatabaseReference = FirebaseDatabase.getInstance().reference.child("jadwal")
    private val databaseMitra: DatabaseReference = FirebaseDatabase.getInstance().reference.child("mitra")
    private val databaseLog: DatabaseReference = FirebaseDatabase.getInstance().reference.child("log")

    // State flows for data
    private val _jadwalList = MutableStateFlow<List<Jadwal>>(emptyList())
    val jadwalList: StateFlow<List<Jadwal>> = _jadwalList

    private val _mitraList = MutableStateFlow<List<Mitra>>(emptyList())
    val mitraList: StateFlow<List<Mitra>> = _mitraList

    private val _logList = MutableStateFlow<List<LogJadwal>>(emptyList())
    val logList: StateFlow<List<LogJadwal>> = _logList

    private val _logDetailList = MutableStateFlow<List<LogDetail>>(emptyList())
    val logDetailList: StateFlow<List<LogDetail>> = _logDetailList

    init {
        // Initialize data loading and scheduling
        viewModelScope.launch {
            readJadwal()
            readMitra()
            scheduleJadwalReading()
            readLog()
        }

        // Add logs for today when data changes
        viewModelScope.launch {
            mitraList.collect { mitraData ->
                if (mitraData.isNotEmpty() && jadwalList.value.isNotEmpty()) {
                    addLogsForToday()
                }
            }
        }
    }

    // Read jadwal data from Firebase
    private fun readJadwal() {
        databaseJadwal.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jadwals = snapshot.children.mapNotNull { data ->
                    data.getValue(Jadwal::class.java)?.apply { id = data.key.orEmpty() }
                }
                _jadwalList.value = jadwals
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Add new jadwal to Firebase
    fun addJadwal(jadwal: Jadwal) {
        val jadwalId = databaseJadwal.push().key ?: return
        jadwal.id = jadwalId
        databaseJadwal.child(jadwalId).setValue(jadwal)
    }

    // Delete jadwal from Firebase
    fun deleteJadwal(jadwal: Jadwal) {
        databaseJadwal.child(jadwal.id).removeValue()
    }

    // Delete jadwal from Firebase
    fun deleteLog(log: LogJadwal) {
        databaseLog.child(log.idJadwal).removeValue()
    }

    // Update jadwal in Firebase
    fun updateJadwal(jadwal: Jadwal) {
        if (jadwal.id.isNotEmpty()) {
            databaseJadwal.child(jadwal.id).setValue(jadwal)
        }
    }

    // Schedule periodic reading of jadwal
    private fun scheduleJadwalReading() {
        val workRequest = PeriodicWorkRequestBuilder<JadwalReaderWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance().enqueue(workRequest)
    }

    // Read mitra data from Firebase
    private fun readMitra() {
        databaseMitra.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mitras = snapshot.children.mapNotNull { data ->
                    data.getValue(Mitra::class.java)?.apply { id = data.key.orEmpty() }
                }
                _mitraList.value = mitras
            }

            override fun onCancelled(error: DatabaseError) {}
        })
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

    // Read log data from Firebase
    private fun readLog() {
        databaseLog.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val logs = snapshot.children.mapNotNull { data ->
                    data.getValue(LogJadwal::class.java)?.apply { id = data.key.orEmpty() }
                }
                _logList.value = logs
                combineDataLog()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Combine log data with related mitra and jadwal data
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
            _logDetailList.value = combinedData
        }
    }
}
