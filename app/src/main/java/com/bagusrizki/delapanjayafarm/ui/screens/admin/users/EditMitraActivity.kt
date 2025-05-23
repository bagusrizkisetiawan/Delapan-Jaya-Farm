package com.bagusrizki.delapanjayafarm.ui.screens.admin.users

import android.annotation.SuppressLint
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.ui.screens.admin.users.ui.theme.DelapanJayaFarmTheme

class EditMitraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ambil ID Mitra dari Intent
        val mitraId = intent.getStringExtra("mitraId") ?: ""

        setContent {
            DelapanJayaFarmTheme {
                EditMitraScreen(onBackPressed = { finish() }, mitraId = mitraId)
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMitraScreen(
    onBackPressed: () -> Unit = {},
    mitraId: String,
    usersViewModel: UsersViewModel = UsersViewModel()
) {
    var nama by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var noHp by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Admin Data
    val mitraList by usersViewModel.mitraList.collectAsState()

    // Cari pengguna berdasarkan ID
    val mitra = mitraList.find { it.id == mitraId }

    // Set the initial values when user data is available
    LaunchedEffect(mitra) {
        mitra?.let {
            nama = it.nama
            username = it.username
            password = it.password
            noHp = it.noHp
            alamat = it.alamat
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    Text("Tambah Mitra", fontSize = 20.sp)
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
            OutlinedTextField(
                value = nama,
                onValueChange = { newNama -> nama = newNama },
                label = { Text(text = "Nama") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
            )
            OutlinedTextField(
                value = username,
                onValueChange = { newUsername -> username = newUsername },
                label = { Text(text = "Username") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { newPassword -> password = newPassword },
                label = { Text(text = "Password") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = noHp,
                onValueChange = { newNoHp -> noHp = newNoHp },
                label = { Text(text = "No Hp") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = alamat,
                onValueChange = { newAlamat -> alamat = newAlamat },
                label = { Text(text = "Alamat") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Button(
                onClick = {
                    loading = true
                    // Add Admin to firebase
                    val mitra =
                        Mitra(
                            id = mitraId,
                            nama = nama,
                            username = username,
                            password = password,
                            noHp = noHp,
                            alamat = alamat
                        )
                    usersViewModel.updateMitra(mitra)

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
fun EditMitraPreview() {
    DelapanJayaFarmTheme {

    }
}