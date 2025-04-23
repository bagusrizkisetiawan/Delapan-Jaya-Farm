package com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal


import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagusrizki.delapanjayafarm.data.Jadwal
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.ui.theme.DelapanJayaFarmTheme
import java.util.Calendar

class EditJadwalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Ambil ID jadwal dari Intent
        val jadwalId = intent.getStringExtra("jadwalId") ?: ""

        setContent {
            DelapanJayaFarmTheme {
                EditJadwalScreen(onBackPressed = { finish() }, jadwalId = jadwalId)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJadwalScreen(
    onBackPressed: () -> Unit = {},
    jadwalId: String,
    jadwalViewModel: JadwalViewModel = JadwalViewModel()
) {
    var namaJadwal by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var jam by remember { mutableStateOf("") }

    // Time picker dialog state
    var isDialogOpen by remember { mutableStateOf(false) }

    // Waktu saat ini untuk TimePicker
    val calendar = Calendar.getInstance()
    var hour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }


    // Admin Data
    val jadwalList by jadwalViewModel.jadwalList.collectAsState()

    // Cari pengguna berdasarkan ID
    val jadwal = jadwalList.find { it.id == jadwalId }

    // Set the initial values when user data is available
    LaunchedEffect(jadwal) {
        jadwal?.let {
            namaJadwal = it.namaJadwal
            keterangan = it.keterangan
            jam = it.jam
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    Text("Edit Jadwal", fontSize = 20.sp)
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
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedButton(
                onClick = { isDialogOpen = true },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = if (jam.isEmpty()) "Pilih Waktu" else "Waktu Dipilih: $jam",
                    modifier = Modifier.padding(8.dp)
                )
            }
            // Dialog untuk memilih waktu
            if (isDialogOpen) {
                TimePickerDialog(
                    LocalContext.current,
                    { _, selectedHour, selectedMinute ->
                        hour = selectedHour
                        minute = selectedMinute
                        jam = String.format("%02d:%02d", hour, minute) // Format waktu ke string
                        isDialogOpen = false
                    },
                    hour,
                    minute,
                    true // Format 24 jam
                ).show()
            }

            OutlinedTextField(
                value = namaJadwal,
                onValueChange = { namaJadwal = it },
                label = { Text(text = "Nama Jadwal") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
            )
            OutlinedTextField(
                value = keterangan,
                onValueChange = { keterangan = it },
                label = { Text(text = "Keterangan") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )


            Button(
                onClick = {
                    loading = true
                    // Add Admin to firebase
                    val jadwal = Jadwal(
                        id = jadwalId,
                        jam = jam,
                        namaJadwal = namaJadwal,
                        keterangan = keterangan
                    )
                    jadwalViewModel.updateJadwal(jadwal)

                    loading = false
                    // redirect finish activity
                    onBackPressed()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(4.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.width(16.dp))
                } else {
                    Text(
                        "Simpan", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview5() {
    DelapanJayaFarmTheme {
//        Greeting3("Android")
    }
}