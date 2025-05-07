package com.bagusrizki.delapanjayafarm.ui.components.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.bagusrizki.delapanjayafarm.R
import com.bagusrizki.delapanjayafarm.data.BobotSapi
import com.bagusrizki.delapanjayafarm.data.Sapi
import com.bagusrizki.delapanjayafarm.data.SapiDetail
import com.bagusrizki.delapanjayafarm.ui.screens.admin.home.SapiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSapi(sapi: SapiDetail, sapiViewModel: SapiViewModel = viewModel()) {

    val mitraList by sapiViewModel.mitraList.collectAsState()
    val hargaSapi by sapiViewModel.listHargaSapi.collectAsState()

    val mitra = mitraList.find { it.id == sapi.idMitra }
    val mitraName = mitra?.nama ?: "Unknown Mitra"

    val lastTanggal = sapi.bobot.lastOrNull()?.tanggal ?: ""
    val lastBobot: Int = sapi.bobot.lastOrNull()?.bobot?.toInt() ?: 0
    val hargaPerKg: Int = hargaSapi.firstOrNull()?.hargaSapi ?: 0
    val totalHarga: Int = lastBobot * hargaPerKg

    val showDetail = remember { mutableStateOf(false) }
    val expandUpdateSapi = remember { mutableStateOf<Boolean>(false) }

    val bobotList = sapi.bobot.sortedByDescending { it.tanggal }

    var showDeleteDialog = remember { mutableStateOf(false) }


    Row(
        modifier = Modifier
            .clickable {
                showDetail.value = !showDetail.value
            }
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image Card
        Card(
            modifier = Modifier
                .size(86.dp)

        ) {
            AsyncImage(
                model = sapi.imageSapi,
                contentDescription = sapi.namaSapi,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Detail Column
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = sapi.namaSapi,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Jenis: ${sapi.jenisSapi}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${mitraName}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold

            )
        }

        // Weight and Date Column
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = "Rp ${"%,d".format(totalHarga)}",
                fontSize = 14.sp,
                maxLines = 1,

                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$lastBobot Kg / $lastTanggal",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Status Card
            Card(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = when (sapi.statusSapi) {
                        "Siap Jual" -> colorResource(R.color.teal_700)
                        "Sakit" -> colorResource(R.color.danger)
                        "Proses" -> Color(0xFF9E9E9E)   // Grey color
                        else -> MaterialTheme.colorScheme.primary
                    },
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = sapi.statusSapi,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }
    }


    Spacer(modifier = Modifier.height(8.dp))


    if (showDetail.value) {
        Dialog(
            onDismissRequest = { showDetail.value = false },
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                ) {

                    LazyColumn {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(240.dp)
                            ) {
                                AsyncImage(
                                    model = sapi.imageSapi,
                                    contentDescription = sapi.namaSapi,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .padding(top = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = sapi.namaSapi,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Card(
                                        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                                        colors = CardDefaults.cardColors(
                                            containerColor = when (sapi.statusSapi) {
                                                "Siap Jual" -> colorResource(R.color.teal_700)
                                                "Sakit" -> colorResource(R.color.danger)
                                                "Proses" -> Color(0xFF9E9E9E)   // Grey color
                                                else -> MaterialTheme.colorScheme.primary
                                            },
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Text(
                                            text = sapi.statusSapi,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp),
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Jenis : ${sapi.jenisSapi}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "$lastBobot Kg / $lastTanggal",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Mitra : $mitraName",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.SemiBold

                                )

                                Text(
                                    text = "keterangan : ${sapi.keteranganSapi}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "Alamat : ${mitra?.alamat}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Rp ${"%,d".format(totalHarga)}",
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(18.dp))
                                Row {
                                    Button(
                                        onClick = {
                                            expandUpdateSapi.value = !expandUpdateSapi.value
                                        },
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(34.dp),
                                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimaryContainer)
                                    ) {
                                        Text(text = "Update Sapi", fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            showDeleteDialog.value = true
                                        },
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(34.dp),
                                        colors = ButtonDefaults.buttonColors(colorResource(R.color.danger))
                                    ) {
                                        Text(text = "Hapus", fontSize = 12.sp)
                                    }
                                }

                                // update sapi
                                if (expandUpdateSapi.value) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    var expandedStatus by remember { mutableStateOf(false) }
                                    val statusOptions =
                                        listOf("Siap Jual", "Sakit", "Proses", "Terjual")

                                    var statusSapi by remember { mutableStateOf(sapi.statusSapi) }
                                    var bobotSapi by remember { mutableStateOf("") }
                                    var loading by remember { mutableStateOf(false) }

                                    var tanggalUpdateBobot =
                                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                                            Date()
                                        )

                                    ExposedDropdownMenuBox(
                                        expanded = expandedStatus,
                                        onExpandedChange = { expandedStatus = !expandedStatus }
                                    ) {
                                        OutlinedTextField(
                                            readOnly = true,
                                            value = statusSapi,
                                            onValueChange = {},
                                            label = { Text("Status Sapi") },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = expandedStatus
                                                )
                                            },
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
                                                        statusSapi = status
                                                        expandedStatus = false
                                                    },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }

                                    // Tanggal bobot sapi
                                    OutlinedTextField(
                                        value = bobotSapi,
                                        onValueChange = { bobotSapi = it },
                                        label = { Text("Bobot Sapi") },
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            imeAction = ImeAction.Next,
                                            keyboardType = KeyboardType.Number
                                        ),
                                        maxLines = 1,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            loading = true

                                            val sapiUpdateData: Sapi = Sapi(
                                                sapi.idSapi,
                                                sapi.idMitra,
                                                sapi.namaSapi,
                                                sapi.jenisSapi,
                                                sapi.tanggalPemeliharaan,
                                                sapi.imageSapi,
                                                statusSapi,
                                                sapi.keteranganSapi
                                            )

                                            sapiViewModel.updateSapi(sapiUpdateData)

                                            val bobotValue = bobotSapi.toIntOrNull()
                                            if (bobotValue != null && bobotValue > 0) {
                                                val addBobotSapi = BobotSapi(
                                                    idSapi = sapi.idSapi,
                                                    bobot = bobotValue.toString(),
                                                    tanggal = tanggalUpdateBobot
                                                )
                                                sapiViewModel.addBobot(addBobotSapi)
                                            }

                                            loading = false
                                            expandUpdateSapi.value = false
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        if (loading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.width(
                                                    16.dp
                                                )
                                            )
                                        } else {
                                            Text(
                                                "Simpan",
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Text(
                                    text = "Data Bobot",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        items(bobotList) { bobot ->
                           ItemBobotSapi(bobot = bobot)
                        }
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = {
                Text(text = "Konfirmasi Hapus")
            },
            text = {
                Text(text = "Apakah Anda yakin ingin menghapus Sapi ${sapi.namaSapi}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // delete admin from firebase
                        sapiViewModel.deleteSapi(Sapi(sapi.idSapi))

                        showDeleteDialog.value = false
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog.value = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}


@Composable
fun ItemBobotSapi(bobot: BobotSapi, sapiViewModel: SapiViewModel = viewModel()){
    val showDeleteBobot = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    showDeleteBobot.value = !showDeleteBobot.value
                }
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            if (showDeleteBobot.value) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete_outline),
                    contentDescription = null,
                    tint = colorResource(R.color.danger),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            sapiViewModel.deleteBobot(bobot)
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(
                text = "${bobot.bobot} Kg",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Text(
            text = bobot.tanggal,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}




