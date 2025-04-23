package com.bagusrizki.delapanjayafarm.data

data class SapiDetail(
    var idSapi: String = "",
    var idMitra: String = "",
    var namaSapi: String = "",
    var jenisSapi: String = "",
    var tanggalPemeliharaan: String = "",
    var imageSapi: String = "",
    var statusSapi: String = "",
    var keteranganSapi: String = "",
    var mitra: Mitra = Mitra(),
    var bobot: List<BobotSapi> = emptyList()
)
