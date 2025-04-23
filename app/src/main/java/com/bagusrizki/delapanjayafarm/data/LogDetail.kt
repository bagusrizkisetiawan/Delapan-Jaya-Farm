package com.bagusrizki.delapanjayafarm.data

data class LogDetail(
    var id: String = "",
    val idMitra: String = "",
    val idJadwal: String = "",
    val tanggal: String = "",
    val jam: String = "",
    val status: String = "",
    val mitra: Mitra = Mitra(),
    val jadwal: Jadwal = Jadwal()
)