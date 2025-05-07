package com.bagusrizki.delapanjayafarm.ui.components.mitra

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.R
import com.bagusrizki.delapanjayafarm.data.Jadwal
import com.bagusrizki.delapanjayafarm.data.LogJadwal
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.ui.screens.mitra.jadwal.JadwalMitraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ItemJadwalMitra(
    userLogin: Mitra,
    jadwal: Jadwal,
    jadwalViewModel: JadwalMitraViewModel = viewModel()
) {
    val context = LocalContext.current

    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val currentJam = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    // Data
    val logList by jadwalViewModel.logDetailList.collectAsState()

    val idLog = "${userLogin.id}-${jadwal.id}-$currentDate"

    val logById = logList.find { it.id == idLog }


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
                if (logById?.status == "Selesai") {
                    Card(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.teal_700),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(Modifier.width(8.dp))

                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)

                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Selesai", fontSize = 12.sp,
                                modifier = Modifier.padding(end = 10.dp),
                            )
                        }

                    }
                } else {
                    Button(
                        onClick = {
                            val log = LogJadwal(
                                id = idLog,
                                idMitra = userLogin.id,
                                idJadwal = jadwal.id,
                                tanggal = currentDate,
                                jam = currentJam,
                                status = "Selesai"
                            )
                            jadwalViewModel.updateLog(log)
                        },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(34.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Selesaikan", fontSize = 12.sp)
                    }
                }
            }
        }
    }

}
