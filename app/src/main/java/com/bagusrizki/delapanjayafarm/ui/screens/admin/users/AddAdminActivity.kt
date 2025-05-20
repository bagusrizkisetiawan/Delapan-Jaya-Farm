package com.bagusrizki.delapanjayafarm.ui.screens.admin.users

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagusrizki.delapanjayafarm.data.Admin
import com.bagusrizki.delapanjayafarm.ui.screens.admin.users.ui.theme.DelapanJayaFarmTheme

class AddAdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            DelapanJayaFarmTheme {
                //
                AddAdminScreen(onBackPressed = { finish() })
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdminScreen(
    onBackPressed: () -> Unit = {},
    usersViewModel: UsersViewModel = UsersViewModel()
) {
    var nama by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    Text("Tambah Admin", fontSize = 20.sp)
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

            Button(
                onClick = {
                    loading = true

                    if (nama.isEmpty() || username.isEmpty() || password.isEmpty()) {
                        errorMessage = "Semua kolom harus diisi"
                        showError = true
                    } else {
                        val admin =
                            Admin(nama = nama, username = username, password = password)
                        usersViewModel.addAdmin(admin)

                        onBackPressed()
                    }
                    loading = false
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
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

            if (showError) {
                AlertDialog(
                    onDismissRequest = { showError = false },
                    title = {
                        Text(
                            text = "Gagal Simpan",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    text = {
                        Text(
                            text = errorMessage,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                    },
                    icon = {

                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_4_XL)
@Composable
fun AddAdminPreview() {
    DelapanJayaFarmTheme {
//        AddAdminScreen()
    }
}