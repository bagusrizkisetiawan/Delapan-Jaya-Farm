package com.bagusrizki.delapanjayafarm.ui.screens.admin.pemesanan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.ui.components.admin.ItemPemesanan

@Composable
fun PemesananScreen(pemesananViewModel: PemesananViewModel = viewModel()) {
    val context = LocalContext.current

    // Data
    val pemesananDetailList by pemesananViewModel.pemesananDetailList.collectAsState()
    val sortPemesanan = pemesananDetailList.sortedByDescending { it.tanggalPemesanan }

    val isLoading = sortPemesanan.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Pemesanan Pakan",
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Display the list of jadwal
        if (isLoading) {
            Text("Belum ada pesanan")
        } else {
            LazyColumn(
            ) {
                items(sortPemesanan) { pemesanan ->
                    ItemPemesanan(pemesanan = pemesanan)
                }
            }
        }

    }
}