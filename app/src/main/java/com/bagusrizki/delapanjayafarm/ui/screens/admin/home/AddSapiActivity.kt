package com.bagusrizki.delapanjayafarm.ui.screens.admin.home

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.R
import com.bagusrizki.delapanjayafarm.data.BobotSapi
import com.bagusrizki.delapanjayafarm.data.Sapi
import com.bagusrizki.delapanjayafarm.service.uploadImageToCloudinary
import com.bagusrizki.delapanjayafarm.ui.screens.admin.home.ui.theme.DelapanJayaFarmTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddSapiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DelapanJayaFarmTheme {
                AddSapiScreen(onBackPressed = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSapiScreen(
    onBackPressed: () -> Unit = {},
    sapiViewModel: SapiViewModel = viewModel()
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    var loading by remember { mutableStateOf(false) }

    // Mitra Data
    val mitraList by sapiViewModel.mitraList.collectAsState()
    var selectedMitra by remember { mutableStateOf("") }
    var selectedMitraName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Status Sapi Dropdown
    var expandedStatus by remember { mutableStateOf(false) }
    val statusOptions = listOf("Siap Jual", "Sakit", "Proses")

    // Status Sapi Dropdown
    var expandedJenis by remember { mutableStateOf(false) }
    val jenisOptions = listOf("Simetal", "Limosin")

    // Form Fields
    var namaSapi by remember { mutableStateOf("") }
    var jenisSapi by remember { mutableStateOf("") }
    var tanggalPemeliharaan = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    var bobotSapi by remember { mutableStateOf("") }
    var statusSapi by remember { mutableStateOf("") }
    var keteranganSapi by remember { mutableStateOf("") }

    // Default image resource
    val context = LocalContext.current
    val defaultImage =
        BitmapFactory.decodeResource(context.resources, R.drawable.add_photos_icon_vector)
    val bitmap = remember { mutableStateOf(defaultImage) }

    // Launcher untuk mengambil foto dari kamera
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { result ->
            result?.let { bitmap.value = it }
        }

    // Launcher untuk mengambil gambar dari galeri
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val newBitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
                bitmap.value = newBitmap
            }
        }

    // Show alert dialog for missing fields
    var showErrorDialog by remember { mutableStateOf(false) }

    // Validation logic
    fun validateForm(): Boolean {
        return namaSapi.isNotEmpty() && selectedMitra.isNotEmpty() && jenisSapi.isNotEmpty() &&
                bobotSapi.isNotEmpty() && statusSapi.isNotEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Sapi", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showBottomSheet = true },
            ) {
                Image(
                    bitmap = bitmap.value.asImageBitmap(),
                    contentDescription = "Gambar Sapi",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray)
                )
            }

            // ID Mitra Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedMitraName,
                    onValueChange = {},
                    label = { Text("Mitra") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    mitraList.forEach { mitra ->
                        DropdownMenuItem(
                            text = { Text(mitra.nama) },
                            onClick = {
                                selectedMitra = mitra.id
                                selectedMitraName = mitra.nama
                                expanded = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Nama Sapi Input
            OutlinedTextField(
                value = namaSapi,
                onValueChange = { namaSapi = it },
                label = { Text("Nama Sapi") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            // Jenis Sapi Input
            ExposedDropdownMenuBox(
                expanded = expandedJenis,
                onExpandedChange = { expandedJenis = !expandedJenis }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = jenisSapi,
                    onValueChange = {},
                    label = { Text("Jenis Sapi") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJenis) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedJenis,
                    onDismissRequest = { expandedJenis = false }
                ) {
                    jenisOptions.forEach { jenis ->
                        DropdownMenuItem(
                            text = { Text(jenis) },
                            onClick = {
                                jenisSapi = jenis
                                expandedJenis = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Tanggal bobot sapi
            OutlinedTextField(
                value = bobotSapi,
                onValueChange = { bobotSapi = it },
                label = { Text("Bobot Sapi") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            // Status Sapi Input
            ExposedDropdownMenuBox(
                expanded = expandedStatus,
                onExpandedChange = { expandedStatus = !expandedStatus }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = statusSapi,
                    onValueChange = {},
                    label = { Text("Status Sapi") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedStatus,
                    onDismissRequest = { expandedStatus = false }
                ) {
                    statusOptions.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                statusSapi = status
                                expandedStatus = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Keterangan Sapi Input
            OutlinedTextField(
                value = keteranganSapi,
                onValueChange = { keteranganSapi = it },
                label = { Text("Keterangan Sapi") },
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )

            Button(
                onClick = {
                    // Validate the form before submitting
                    if (validateForm()) {
                        loading = true

                        // Unggah gambar ke Cloudinary
                        uploadImageToCloudinary(bitmap.value, onSuccess = { imageUrl ->
                            val sapi = Sapi(
                                idMitra = selectedMitra,
                                namaSapi = namaSapi,
                                jenisSapi = jenisSapi,
                                tanggalPemeliharaan = tanggalPemeliharaan,
                                imageSapi = imageUrl,
                                statusSapi = statusSapi,
                                keteranganSapi = keteranganSapi
                            )
                            sapiViewModel.addSapi(sapi,
                                onSuccess = { sapiId ->
                                    val bobotSapi = BobotSapi(
                                        idSapi = sapiId,
                                        bobot = bobotSapi,
                                        tanggal = tanggalPemeliharaan
                                    )

                                    sapiViewModel.addBobot(
                                        bobotSapi = bobotSapi
                                    )
                                },
                                onError = { error ->
                                    Log.e("Firebase", "Gagal menambahkan sapi: ${error.message}")
                                    loading = false
                                }
                            )

                        }, onError = { error ->
                            Log.e("Cloudinary", "Failed to upload image: ${error.message}")
                        })

                        loading = false
                        // redirect finish activity
                        onBackPressed()
                    } else {
                        // Show error dialog if form is not valid
                        showErrorDialog = true
                    }
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

        // Error Dialog
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Form Tidak Lengkap") },
                text = { Text("Silakan lengkapi semua data yang diperlukan.") },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
                modifier = Modifier.height(200.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                cameraLauncher.launch()
                                showBottomSheet = false
                            }
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Buka Kamera",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Buka Kamera")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                galleryLauncher.launch("image/*")
                                showBottomSheet = false
                            }
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_image),
                            contentDescription = "Buka Galeri",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Buka Foto dari Galeri")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddSapiScreenPreview() {
    DelapanJayaFarmTheme {
        AddSapiScreen()
    }
}
