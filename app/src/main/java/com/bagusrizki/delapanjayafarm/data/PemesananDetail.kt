package com.bagusrizki.delapanjayafarm.data

data class PemesananDetail(
    var idPemesanan: String = "",
    var idMitra: String = "",
    var tanggalPemesanan: String = "",
    var jenisPemesanan: String = "",
    var keteranganPemesanan: String = "",
    var estimasiPemesanan: String = "",
    var statusPemesanan: String = "",
    var mitra : Mitra = Mitra()
)
