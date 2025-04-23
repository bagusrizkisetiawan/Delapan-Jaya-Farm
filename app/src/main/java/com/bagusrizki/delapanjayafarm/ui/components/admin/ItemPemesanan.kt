package com.bagusrizki.delapanjayafarm.ui.components.admin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagusrizki.delapanjayafarm.R
import com.bagusrizki.delapanjayafarm.data.PemesananDetail
import com.bagusrizki.delapanjayafarm.ui.screens.admin.pemesanan.KonfirmasiPemesananActivity

@SuppressLint("SuspiciousIndentation")
@Composable
fun ItemPemesanan(pemesanan: PemesananDetail) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .clickable {
                val intent = Intent(context, KonfirmasiPemesananActivity::class.java)
                intent.putExtra("PEMESANANID", pemesanan.idPemesanan)
                context.startActivity(intent)
            }
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