package com.bagusrizki.delapanjayafarm.ui.components.mitra

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.ui.screens.admin.home.SapiActivity
import com.bagusrizki.delapanjayafarm.ui.screens.mitra.home.HomeMitraViewModel

@Composable
fun CardHomeSapiMitra(userLogin: Mitra, homeViewModel: HomeMitraViewModel = viewModel()) {
    val context = LocalContext.current

    val _listSapi = homeViewModel.sapiList.collectAsState()
    val listSapi = _listSapi.value.filter { it.idMitra == userLogin.id }
    val sapiSakit = listSapi.filter { it.statusSapi == "Sakit" }
    val sapiSiapJual = listSapi.filter { it.statusSapi == "Siap Jual" }
    val sapiProses = listSapi.filter { it.statusSapi == "Proses" }
    val sapiTerjual = listSapi.filter { it.statusSapi == "Terjual" }


    val jumlahSapi = listSapi.size
    val jumlahSapiSakit = sapiSakit.size
    val jumlahSapiSiapJual = sapiSiapJual.size
    val jumlahProses = sapiProses.size
    val jumlahTerjual = sapiTerjual.size

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = "Jumlah Sapi dalam peternakan",
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontSize = 12.sp,
                )
                Text(
                    text = "${jumlahSapi} Sapi",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 0.7.dp, color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .clickable {
                            // Membuat intent untuk berpindah ke AddMitraActivity
                            val intent = Intent(context, SapiActivity::class.java)
                            // Memulai aktivitas baru
                            context.startActivity(intent)
                        }
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Lihat Semua Sapi",
                        modifier = Modifier,
                        fontSize = 12.sp,
                    )
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Show more"
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onPrimaryContainer)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
               IconBottom(
                    jumlah = jumlahSapiSiapJual.toString(),
                    label = "Siap Jual",
                    intentLabel = "Siap Jual"
                )
                IconBottom(
                    jumlah = jumlahSapiSakit.toString(),
                    label = "Sakit",
                    intentLabel = "Sakit"
                )
                IconBottom(
                    jumlah = jumlahProses.toString(),
                    label = "Proses",
                    intentLabel = "Proses"
                )
                IconBottom(
                    jumlah = jumlahTerjual.toString(),
                    label = "Terjual",
                    intentLabel = "Terjual"
                )
            }
        }
    }
}


@Composable
fun IconBottom(jumlah: String, label: String, intentLabel:String) {

    val context = LocalContext.current

    Column(
        modifier = Modifier.width(76.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .clickable {
                    // Membuat intent untuk berpindah ke AddMitraActivity
                    val intent = Intent(context, SapiActivity::class.java)
                    // Memulai aktivitas baru
                    intent.putExtra("status", intentLabel)
                    context.startActivity(intent)
                }
                .width(52.dp)
                .height(52.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = jumlah,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Text(
            text = label,
            modifier = Modifier,
            fontSize = 12.sp,
        )
    }
}
