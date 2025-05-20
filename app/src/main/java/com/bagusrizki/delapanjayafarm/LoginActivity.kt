package com.bagusrizki.delapanjayafarm

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagusrizki.delapanjayafarm.ui.theme.DelapanJayaFarmTheme


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()  // Request permission on app start
        setContent {
            DelapanJayaFarmTheme {
                val context = this
                val userPreferences = UserPreferences(context)
                // Observasi data
                val userId by userPreferences.userIdFlow.collectAsState(initial = null)
                val userLevel by userPreferences.userLevelFlow.collectAsState(initial = null)

                // Navigasi otomatis jika sudah login
                LaunchedEffect(userId, userLevel) {
                    if (userId != null && userLevel != null) {
                        if (userLevel == "admin") navigateToAdmin() else navigateToMitra()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyLogin(
                        loginViewModel = LoginViewModel(UserPreferences(context))
                    )
                }
            }
        }
    }

    private fun navigateToMitra() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToAdmin() {
        startActivity(Intent(this, AdminActivity::class.java))
        finish()
    }

    // Notification permission request for Android 13+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

}

@SuppressLint("SuspiciousIndentation")
@Composable
fun MyLogin(
    loginViewModel: LoginViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val adminList by loginViewModel.adminList.collectAsState()
    val mitraList by loginViewModel.mitraList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bghero),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 18f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        ) {

            Text(
                text = "Eight Farm!",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Aplikasi Delapan Jaya Group, Masuk sekarang untuk mempermudah aktivitas Anda.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 28.dp)
            )

            Text(
                text = "Username",
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
            )

            Text(
                text = "Password",
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 6.dp, top = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                            ),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    when {
                        username.isBlank() || password.isBlank() -> {
                            errorMessage = "Username dan password tidak boleh kosong"
                            showError = true
                        }

                        else -> {
                            val admin =
                                adminList.find { it.username == username && it.password == password }
                            val mitra =
                                mitraList.find { it.username == username && it.password == password }

                            if (admin != null) {
                                loginViewModel.saveUser(admin.id, "admin")
                            } else if (mitra != null) {
                                loginViewModel.saveUser(mitra.id, "mitra")
                            } else {

                                val usernameExistsInAdmin =
                                    adminList.any { it.username == username }
                                val usernameExistsInMitra =
                                    mitraList.any { it.username == username }

                                errorMessage = when {
                                    usernameExistsInAdmin || usernameExistsInMitra -> "Password salah"
                                    else -> "Username tidak ditemukan"
                                }
                                showError = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Login",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

        }

        if (showError) {
            AlertDialog(
                onDismissRequest = { showError = false },
                title = {
                    Text(
                        text = "Login Gagal",
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
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_4_XL, showSystemUi = true)
@Composable
fun MyLoginPreview() {
    DelapanJayaFarmTheme {
//        MyLogin (
////            loginViewModel = LoginViewModel(UserPreferences(context))
//        )
    }
}
