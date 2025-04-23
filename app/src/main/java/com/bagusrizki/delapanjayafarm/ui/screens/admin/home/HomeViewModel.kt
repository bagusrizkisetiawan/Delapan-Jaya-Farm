package com.bagusrizki.delapanjayafarm.ui.screens.admin.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagusrizki.delapanjayafarm.data.*
import com.google.firebase.database.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val databaseMitra: DatabaseReference = FirebaseDatabase.getInstance().getReference("mitra")
    private val databaseLog: DatabaseReference = FirebaseDatabase.getInstance().getReference("log")
    private val databaseJadwal: DatabaseReference = FirebaseDatabase.getInstance().getReference("jadwal")

    private val _mitraList = MutableStateFlow<List<Mitra>>(emptyList())
    val mitraList: StateFlow<List<Mitra>> = _mitraList

    private val _jadwalList = MutableStateFlow<List<Jadwal>>(emptyList())
    val jadwalList: StateFlow<List<Jadwal>> = _jadwalList

    private val _logList = MutableStateFlow<List<LogJadwal>>(emptyList())
    val logList: StateFlow<List<LogJadwal>> = _logList

    private val _logDetailList = MutableStateFlow<List<LogDetail>>(emptyList())
    val logDetailList: StateFlow<List<LogDetail>> = _logDetailList

    private val _logDayList = MutableStateFlow<List<LogDetail>>(emptyList())
    val logDayList: StateFlow<List<LogDetail>> = _logDayList

    private val _loadingList = MutableStateFlow(true)
    val loadingList: StateFlow<Boolean> = _loadingList

    init {
        // Memulai proses pembacaan data
        viewModelScope.launch {
            _loadingList.value = true
            readMitra()
            readJadwal()
            readLog()
        }

        // Tambahkan log baru untuk hari ini ketika data mitra & jadwal tersedia
        viewModelScope.launch {
            mitraList.combine(jadwalList) { mitra, jadwal ->
                if (mitra.isNotEmpty() && jadwal.isNotEmpty()) {
                    addLogsForToday()
                }
            }.collect()
        }

        // Kombinasi data log dengan mitra & jadwal
        viewModelScope.launch {
            combine(logList, mitraList, jadwalList) { logs, mitras, jadwals ->
                logs.map { log ->
                    LogDetail(
                        id = log.id,
                        idMitra = log.idMitra,
                        idJadwal = log.idJadwal,
                        tanggal = log.tanggal,
                        jam = log.jam,
                        status = log.status,
                        mitra = mitras.find { it.id == log.idMitra } ?: Mitra(),
                        jadwal = jadwals.find { it.id == log.idJadwal } ?: Jadwal()
                    )
                }
            }.collect { combinedData ->
                _logDetailList.value = combinedData
                updateLogDayList()
            }
        }
    }

    // Fungsi untuk membaca data Mitra dari Firebase
    private fun readMitra() {
        databaseMitra.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _mitraList.value = snapshot.children.mapNotNull { data ->
                    data.getValue(Mitra::class.java)?.apply { id = data.key ?: "" }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading Mitra: ${error.message}")
            }
        })
    }

    // Fungsi untuk membaca data Jadwal dari Firebase
    private fun readJadwal() {
        databaseJadwal.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _jadwalList.value = snapshot.children.mapNotNull { data ->
                    data.getValue(Jadwal::class.java)?.apply { id = data.key ?: "" }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading Jadwal: ${error.message}")
            }
        })
    }

    // Fungsi untuk membaca data Log dari Firebase
    private fun readLog() {
        databaseLog.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _logList.value = snapshot.children.mapNotNull { data ->
                    data.getValue(LogJadwal::class.java)?.apply { id = data.key ?: "" }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading Log: ${error.message}")
            }
        })
    }

    // Memperbarui daftar log yang hanya untuk hari ini
    private fun updateLogDayList() {
        val currentDate = getCurrentDate()
        _logDayList.value = logDetailList.value.filter { it.tanggal == currentDate }
    }

    // Menambahkan log baru jika belum ada log untuk hari ini
    private fun addLogsForToday() {
        val currentDate = getCurrentDate()

        mitraList.value.forEach { mitra ->
            jadwalList.value.forEach { jadwal ->
                val logId = "${mitra.id}-${jadwal.id}-$currentDate"
                databaseLog.child(logId).get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        val newLog = LogJadwal(
                            id = logId,
                            idMitra = mitra.id,
                            idJadwal = jadwal.id,
                            tanggal = currentDate,
                            jam = "--:--",
                            status = "Belum"
                        )
                        databaseLog.child(logId).setValue(newLog)
                    }
                }
            }
        }
    }

    // Fungsi untuk mendapatkan tanggal hari ini dalam format yyyy-MM-dd
    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
