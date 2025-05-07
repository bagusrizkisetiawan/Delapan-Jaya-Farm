package com.bagusrizki.delapanjayafarm.ui.screens.mitra.jadwal

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagusrizki.delapanjayafarm.UserPreferences
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.data.UserLogin
import com.bagusrizki.delapanjayafarm.ui.components.admin.ItemLog
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.JadwalViewModel
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.ui.theme.DelapanJayaFarmTheme
import java.text.SimpleDateFormat
import java.util.Locale

class LogMitraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DelapanJayaFarmTheme {
                LogScreen(onBackPressed = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    onBackPressed: () -> Unit = {},
    jadwalViewModel: JadwalViewModel = JadwalViewModel()
) {
    val context = LocalContext.current
    var idUserLogin by remember { mutableStateOf("") }

    // preference user
    val userPreferences = UserPreferences(context)

    val userId by userPreferences.userIdFlow.collectAsState(initial = null)
    val userLevel by userPreferences.userLevelFlow.collectAsState(initial = null)

    LaunchedEffect(userId, userLevel) {
        if (userId != null && userLevel != null) {
            idUserLogin = userId as String
        }
    }

    val logList = jadwalViewModel.logDetailList.collectAsState()
    val logListSorted = logList.value.sortedBy { it.jadwal.jam }.filter { it.idMitra == idUserLogin }.sortedByDescending {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.tanggal)
    }

    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    Text("Seluruh Aktivitas Anda", fontSize = 20.sp)
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
