package com.bagusrizki.delapanjayafarm.ui.screens.admin.pemesanan

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.R
import com.bagusrizki.delapanjayafarm.data.PemesananDetail
import com.bagusrizki.delapanjayafarm.data.PemesananPakan
import com.bagusrizki.delapanjayafarm.ui.screens.admin.pemesanan.ui.theme.DelapanJayaFarmTheme
import java.util.Calendar

class KonfirmasiPemesananActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val pemesananId = intent.getStringExtra("PEMESANANID") ?: ""

        setContent {
            DelapanJayaFarmTheme {
                KonfirmasiPemesanan(onBackPressed = { finish() }, id = pemesananId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KonfirmasiPemesanan(
    onBackPressed: () -> Unit = {},
    id: String,
    pemesananViewModel: PemesananViewModel = viewModel()
) {
    // Data
    val pemesananDetailList by pemesananViewModel.pemesananDetailList.collectAsState()
    val pemesananDetail = pemesananDetailList.find { it.idPemesanan == id }
    val isLoading = pemesananDetail?.jenisPemesanan?.isEmpty() ?: true


    var expandedStatus by remember { mutableStateOf(false) }
    val statusOptions = listOf("Menunggu", "Di Proses", "Selesai")


    var tanggalEstimasi by remember { mutableStateOf("") }
    var statusPemesanan by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(false) }


    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(

                    title = {
                        Text("Konfirmasi Pemesanan", fontSize = 20.sp)
                    },
                    navigationIcon = {
                        //
                        IconButton(onClick = { onBackPressed() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {


                pemesananDetail?.let { CardPemesanan(it) } ?: Text("Data tidak ditemukan")

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = expandedStatus,
                        onExpandedChange = { expandedStatus = !expandedStatus }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = statusPemesanan,
                            onValueChange = {},
                            label = { Text("Jenis Pemesanan") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedStatus,
                            onDismissRequest = { expandedStatus = false }
                        ) {
                            statusOptions.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        statusPemesanan = status
                                        expandedStatus = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = tanggalEstimasi,
                        onValueChange = {},
                        label = { Text("Tanggal Estimasi") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = "Pilih Tanggal"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showDatePicker) {
                        val context = LocalContext.current
                        val calendar = Calendar.getInstance()
                        val datePicker = DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val formattedDate = "$year-${month + 1}-$dayOfMonth"
                                tanggalEstimasi = formattedDate
                                showDatePicker = false
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                        datePicker.show()
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            loading = true

                            pemesananDetail?.let {
                                pemesananViewModel.updatePemesanan(
                                    PemesananPakan(
                                        idPemesanan = it.idPemesanan,
                                        jenisPemesanan = it.jenisPemesanan,
                                        keteranganPemesanan = it.keteranganPemesanan,
                                        idMitra = it.idMitra,
                                        tanggalPemesanan = it.tanggalPemesanan,
                                        statusPemesanan = statusPemesanan,
                                        estimasiPemesanan = tanggalEstimasi
                                    )
                                )
                                loading = false
                                onBackPressed()
                            } ?: run {
                                loading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        if (loading) {
                            CircularProgressIndicator(modifier = Modifier.width(16.dp))
                        } else {
                            Text(
                                "Simpan",
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

            }

        }
    }
}


@Composable
fun CardPemesanan(pemesanan: PemesananDetail) {

    Column(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = pemesanan.mitra.nama,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            Card(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = when (pemesanan.statusPemesanan) {
                        "Selesai" -> colorResource(R.color.teal_700)
                        "Menunggu" -> colorResource(R.color.warning)
                        else -> MaterialTheme.colorScheme.primary
                    },
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
            ) {
                Text(
                    text = pemesanan.statusPemesanan,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "tanggal : ${pemesanan.tanggalPemesanan}",
                fontSize = 12.sp,
            )
            Text(
                text = "estimasi : ${pemesanan.estimasiPemesanan}",
                fontSize = 12.sp,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Jenis Pemesanan : ${pemesanan.jenisPemesanan}",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Keterangan: ${pemesanan.keteranganPemesanan}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
    }

    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}