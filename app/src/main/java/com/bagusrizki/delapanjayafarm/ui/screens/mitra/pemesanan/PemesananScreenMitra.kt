package com.bagusrizki.delapanjayafarm.ui.screens.mitra.pemesanan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.data.PemesananPakan
import com.bagusrizki.delapanjayafarm.ui.components.mitra.ItemPemesananMitra
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PemesananScreenMitra(
    userLogin: Mitra,
    pemesananViewModel: PemesananMitraViewModel = viewModel()
) {
    var keterangan by remember { mutableStateOf("") }
    var jenisPemesanan by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    var tanggalPemesanan = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    var expandedJenis by remember { mutableStateOf(false) }
    val jenisOptions = listOf("Pakan", "Konsentrat")

    // Data
    val pemesananDetailList by pemesananViewModel.pemesananDetailList.collectAsState()
    val pemesananDetailListByIdMItra =
        pemesananDetailList.filter { it.idMitra == userLogin.id }.sortedByDescending {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.tanggalPemesanan)
        }
    val isLoading = pemesananDetailListByIdMItra.isEmpty()

    var showEmptyJenisAlert by remember { mutableStateOf(false) }

//    android.util.Log.d("pemesananDetailList", pemesananDetailList.toString())

    Column(
        modifier = Modifier.padding(horizontal = 18.dp),
    ) {

        Text(
            text = "Pemesanan Pakan",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 18.dp, bottom = 16.dp)
        )

        LazyColumn {
            item {
                Column {
                    ExposedDropdownMenuBox(
                        expanded = expandedJenis,
                        onExpandedChange = { expandedJenis = !expandedJenis }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = jenisPemesanan,
                            onValueChange = {},
                            label = { Text("Jenis Pemesanan") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJenis) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedJenis,
                            onDismissRequest = { expandedJenis = false }
                        ) {
                            jenisOptions.forEach { jenis ->
                                DropdownMenuItem(
                                    text = { Text(jenis) },
                                    onClick = {
                                        jenisPemesanan = jenis
                                        expandedJenis = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = keterangan,
                        onValueChange = { keterangan = it },
                        label = { Text(text = "Keterangan") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            loading = true

                            if (jenisPemesanan.isBlank()) {
                                showEmptyJenisAlert = true
                            } else {
                                pemesananViewModel.addPemesanan(
                                    PemesananPakan(
                                        jenisPemesanan = jenisPemesanan,
                                        keteranganPemesanan = keterangan,
                                        idMitra = userLogin.id,
                                        tanggalPemesanan = tanggalPemesanan,
                                        statusPemesanan = "Menunggu",
                                        estimasiPemesanan = "-"
                                    )
                                )

                                jenisPemesanan = ""
                                keterangan = ""
                            }

                            loading = false
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
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Pemesanan Anda",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                }
            }

            // Display the list of jadwal
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(pemesananDetailListByIdMItra) { pemesanan ->
                    ItemPemesananMitra(pemesanan = pemesanan)
                }
            }
        }

        if (showEmptyJenisAlert) {
            AlertDialog(
                onDismissRequest = { showEmptyJenisAlert = false },
                title = { Text("Jenis Pakan Kosong") },
                text = { Text("Silakan pilih atau isi jenis pemesanan pakan terlebih dahulu.") },
                confirmButton = {
                    TextButton(onClick = { showEmptyJenisAlert = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}