package com.bagusrizki.delapanjayafarm.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bagusrizki.delapanjayafarm.data.Jadwal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class JadwalReaderWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {


    override fun doWork(): Result {
//        Log.d("UserReaderWorker", "Work started")
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("jadwal")

        // Baca data jadwal dari Firebase
        val jadwals = mutableListOf<Jadwal>()

        // Menggunakan CountDownLatch untuk menunggu callback
        val latch = CountDownLatch(1)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val jadwal = data.getValue(Jadwal::class.java)
                    if (jadwal != null && jadwal.jam.isNotEmpty()) {
                        jadwal.id = data.key ?: ""
                        scheduleNotification(jadwal)
                    }
                }
                latch.countDown()
            }

            override fun onCancelled(error: DatabaseError) {
                latch.countDown()
            }
        })


        // Tunggu hingga data diambil
        latch.await()

        // Proses data pengguna di sini (misalnya, menyimpan ke StateFlow)
        // (Anda perlu mengimplementasikan penyimpanan data ke dalam StateFlow dari sini)

        return Result.success()
    }

    fun scheduleNotification(jadwal: Jadwal) {
        val data = Data.Builder()
            .putString("jam", jadwal.jam)
            .putString("jadwal", jadwal.namaJadwal)
            .putString("keterangan", jadwal.keterangan)
            .build()

        val triggerTime = getTriggerTime(jadwal.jam)

        // Set constraints to ensure the worker runs in the background
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(false)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .setInitialDelay(triggerTime, TimeUnit.MILLISECONDS)
            .build()

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val workManager = WorkManager.getInstance()
        workManager.enqueueUniqueWork(
            "jadwal_${jadwal.id}_${currentDate}", // unique name per jadwal
            ExistingWorkPolicy.REPLACE, // REPLACE atau KEEP tergantung logikamu
            workRequest
        )

    }

    // Menghitung waktu tunda berdasarkan jadwal
    private fun getTriggerTime(jam: String): Long {
        val currentTime = Calendar.getInstance()
        val scheduleTime = jam.split(":").map { it.toInt() }
        val triggerTime = Calendar.getInstance()
        triggerTime.set(Calendar.HOUR_OF_DAY, scheduleTime[0])
        triggerTime.set(Calendar.MINUTE, scheduleTime[1])
        triggerTime.set(Calendar.SECOND, 0)

        // Jika waktu sudah lewat hari ini, jadwalkan untuk besok
        if (triggerTime.timeInMillis <= currentTime.timeInMillis) {
            triggerTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        return triggerTime.timeInMillis - currentTime.timeInMillis
    }
}