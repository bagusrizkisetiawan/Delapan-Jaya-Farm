package com.bagusrizki.delapanjayafarm.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "user_schedule_notifications"
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)

    override fun doWork(): Result {
        val jam = inputData.getString("jam") ?: "jam"
        val namaJadwal = inputData.getString("jadwal") ?: "Jadwal"
        val keterangan = inputData.getString("keterangan") ?: "Keterangan"

        // Format tanggal hari ini
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val notificationKey = "$currentDate-$jam-$namaJadwal"
        val notificationId = (jam.hashCode() + namaJadwal.hashCode()).absoluteValue // ID unik

        // Cek apakah notifikasi sudah dikirim hari ini
        if (sharedPreferences.getBoolean(notificationKey, false)) {
            return Result.success()
        }

        runBlocking(Dispatchers.IO) {
            delay(300) // Delay agar tidak memberatkan sistem

            // Hapus notifikasi lama jika ada
            notificationManager.cancel(notificationId)

            // Tampilkan notifikasi baru
            showNotification(namaJadwal, keterangan, notificationId)

            // Tandai notifikasi sebagai telah dikirim
            sharedPreferences.edit().putBoolean(notificationKey, true).apply()
        }

        return Result.success()
    }

    private fun showNotification(namaJadwal: String, keterangan: String, notificationId: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "User Schedule Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Jadwal: $namaJadwal")
            .setContentText(keterangan)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("Jadwal: $namaJadwal")
                    .bigText("Keterangan: $keterangan")
            )
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
