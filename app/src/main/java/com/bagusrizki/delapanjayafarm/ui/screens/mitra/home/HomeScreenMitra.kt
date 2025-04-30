package com.bagusrizki.delapanjayafarm.ui.screens.mitra.home

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.ui.components.admin.ItemLog
import com.bagusrizki.delapanjayafarm.ui.components.mitra.CardHomeSapiMitra
import com.bagusrizki.delapanjayafarm.ui.components.mitra.ItemLogMitra
import com.bagusrizki.delapanjayafarm.ui.screens.admin.home.SapiViewModel
import com.bagusrizki.delapanjayafarm.ui.screens.admin.home.SetHargaActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreenMitra(
    userLogin: Mitra,
    homeViewModel: HomeMitraViewModel = viewModel(),
    sapiViewModel: SapiViewModel = viewModel()
) {
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val logDayList = homeViewModel.logDayList.collectAsState()
    val LogDayListById = logDayList.value.filter { it.idMitra == userLogin.id }
    val logDayListSorted = LogDayListById.sortedBy { it.jadwal.jam }

    // Collect the hargaSapi state from the ViewModel
    val listHargaSapi = sapiViewModel.listHargaSapi.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
    ) {
        item {
            Column(
                modifier = Modifier.padding(top = 18.dp)
            ) {
                Text(
                    text = userLogin.nama,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mitra",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp,
                    color = Color.Gray,
                )
                Spacer(modifier = Modifier.height(16.dp))

                CardHomeSapiMitra(userLogin = userLogin)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        items(listHargaSapi.value) {
            CardHargaMitra(it.hargaSapi)
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Aktifitas Hari Ini",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = currentDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,

                    )
            }
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

        }

        items(logDayListSorted) { log ->
            ItemLogMitra(log = log, userLogin = userLogin)
        }
    }

}

@Composable
fun CardHargaMitra(harga: Int) {
    val context = LocalContext.current
    Card(
        modifier = Modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {

                Text(
                    text = "Harga Sapi Saat Ini", fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rp ${"%,d".format(harga)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

            }
        }
    }
}