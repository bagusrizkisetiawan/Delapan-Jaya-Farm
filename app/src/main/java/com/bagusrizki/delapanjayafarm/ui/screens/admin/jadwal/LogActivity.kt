package com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.ui.components.admin.ItemLog
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.ui.theme.DelapanJayaFarmTheme
import java.text.SimpleDateFormat
import java.util.Locale

class LogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val jadwalId = intent.getStringExtra("IdJadwal") ?: ""

        setContent {
            DelapanJayaFarmTheme {
                LogScreen(onBackPressed = { finish() }, idJadwal = jadwalId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    onBackPressed: () -> Unit = {},
    idJadwal: String,
    jadwalViewModel: JadwalViewModel = viewModel()
) {

    val logList = jadwalViewModel.logDetailList.collectAsState()
    var logListSorted = logList.value.sortedBy { it.jadwal.jam }.sortedByDescending {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.tanggal)
    }

    if (idJadwal!=""){
        logListSorted = logListSorted.filter {  it.jadwal.id == idJadwal }
    }

    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    if (idJadwal != ""){
                        Text("${logListSorted.firstOrNull()?.jadwal?.namaJadwal} Hari ini", fontSize = 20.sp)
                    }else{
                        Text("Seluruh Aktifitas", fontSize = 20.sp)
                    }
                },
                navigationIcon = {
                    //
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(it).padding(horizontal = 18.dp)
        ){
            items(logListSorted){log->
                ItemLog(log = log)
            }
        }

    }
}
