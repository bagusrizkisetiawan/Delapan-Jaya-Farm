package com.bagusrizki.delapanjayafarm.ui.components.admin

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagusrizki.delapanjayafarm.R
import com.bagusrizki.delapanjayafarm.data.Jadwal
import com.bagusrizki.delapanjayafarm.data.LogJadwal
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.EditJadwalActivity
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.JadwalViewModel
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.LogActivity
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.ui.theme.DelapanJayaFarmTheme

@Composable
fun ItemJadwal(
    jadwal: Jadwal,
    jadwalViewModel: JadwalViewModel = JadwalViewModel()
) {
    val context = LocalContext.current

    var showDeleteDialog = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp, top = 8.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_time),
                contentDescription = null,
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp)
                    .size(18.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = jadwal.jam,
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 12.sp,
                color = Color.Gray,
            )
            Text(
                text = jadwal.namaJadwal,
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = jadwal.keterangan,
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val intent = Intent(context, LogActivity::class.java)
                        intent.putExtra("IdJadwal", jadwal.id)
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimaryContainer)
                ) {
                    Text(text = "Lihat Aktifitas", fontSize = 12.sp)
                }
                TextButton(
                    onClick = {
                        val intent = Intent(context, EditJadwalActivity::class.java)
                        intent.putExtra("jadwalId", jadwal.id)
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(34.dp),
                ) {
                    Text("Edit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                TextButton(
                    onClick = { showDeleteDialog.value = true },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(34.dp),
                ) {
                    Text("Hapus", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
    // AlertDialog untuk konfirmasi penghapusan
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = {
                Text(text = "Konfirmasi Hapus")
            },
            text = {
                Text(text = "Apakah Anda yakin ingin menghapus ${jadwal.namaJadwal}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // delete admin from firebase
                        jadwalViewModel.deleteJadwal(jadwal)
                        val log = LogJadwal(idJadwal = jadwal.id)
                        jadwalViewModel.deleteLog(log)
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


@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    DelapanJayaFarmTheme {
        ItemJadwal(
            Jadwal(
                jam = "08.00 ", namaJadwal = "Pemberian Pakan Pagi",
                keterangan = "Pemberian Pakan Pagi Pemberian Pakan Pagi Pemberian Pakan Pagi Pemberian Pakan Pagi Pemberian Pakan Pagi"
            )
        )
    }
}