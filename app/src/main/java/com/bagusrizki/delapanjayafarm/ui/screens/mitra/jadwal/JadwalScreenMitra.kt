package com.bagusrizki.delapanjayafarm.ui.screens.mitra.jadwal

import android.content.Intent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.R
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.ui.components.mitra.ItemJadwalMitra
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.LogActivity


@Composable
fun JawalScreenMitra(userLogin:Mitra, jadwalViewModel: JadwalMitraViewModel = viewModel()) {
    // Mendapatkan konteks
    val context = LocalContext.current

    // Data
    val jadwalList by jadwalViewModel.jadwalList.collectAsState()
    val isLoading = jadwalList.isEmpty()

    // Urutkan berdasarkan jam
    val sortedJadwalList = jadwalList.sortedBy { it.jam }


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Jawal Pakan",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_list_bulleted),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        val intent = Intent(context, LogMitraActivity::class.java)
                        context.startActivity(intent)
                    }
                    .size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Display the list of jadwal
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(horizontal = 18.dp)) {
                items(sortedJadwalList) { jadwal ->
                    ItemJadwalMitra(
                        userLogin = userLogin,
                        jadwal = jadwal
                    )
                }
            }
        }
    }
}