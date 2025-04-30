package com.bagusrizki.delapanjayafarm.ui.components.mitra

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.R
import com.bagusrizki.delapanjayafarm.data.LogDetail
import com.bagusrizki.delapanjayafarm.data.LogJadwal
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.data.UserLogin
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.JadwalViewModel
import com.bagusrizki.delapanjayafarm.ui.screens.mitra.jadwal.JadwalMitraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ItemLogMitra(log: LogDetail, userLogin : Mitra, jadwalViewModel:JadwalMitraViewModel = viewModel()) {

    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    var isExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clickable {
                isExpanded.value = !isExpanded.value
            }
            .padding( vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "${log.jadwal.namaJadwal}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = if (isExpanded.value) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Card(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = when (log.status) {
                        "Selesai" -> colorResource(R.color.teal_700)
                        "Belum" -> colorResource(R.color.danger)
                        else -> MaterialTheme.colorScheme.primary
                    },
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {

                if (log.status == "Selesai") {
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
                            text = log.status, fontSize = 12.sp,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                    }
                }else{
                    Text(
                        text = log.status,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }

            }
        }
        Row {
            Text(
                text = "Jadwal: ${log.jadwal.jam} (${log.tanggal})",
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Selesai : ${log.jam}",
                fontSize = 12.sp,

                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = log.jadwal.keterangan,
            fontSize = 12.sp,
            maxLines = if (isExpanded.value) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis
        )

        if (isExpanded.value){
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (log.status != "Selesai") {
                    Button(
                        onClick = {
                            val log = LogJadwal(
                                id = log.id,
                                idMitra = userLogin.id,
                                idJadwal = log.idJadwal,
                                tanggal = currentDate,
                                jam = log.jam,
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

    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

}