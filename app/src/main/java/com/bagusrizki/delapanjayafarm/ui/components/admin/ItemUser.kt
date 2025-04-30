package com.bagusrizki.delapanjayafarm.ui.components.admin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.data.Admin
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.ui.screens.admin.home.SapiViewModel
import com.bagusrizki.delapanjayafarm.ui.screens.admin.users.EditAdminActivity
import com.bagusrizki.delapanjayafarm.ui.screens.admin.users.EditMitraActivity
import com.bagusrizki.delapanjayafarm.ui.screens.admin.users.UsersViewModel
import kotlinx.coroutines.flow.filter

@Composable
fun ItemAdmin(admin: Admin, usersViewModel: UsersViewModel = viewModel()) {
    val context = LocalContext.current // Ambil konteks dari Compose

    var isExpanded = remember { mutableStateOf(false) }
    var showDeleteDialog = remember { mutableStateOf(false) }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isExpanded.value = !isExpanded.value
            }
            .padding(horizontal = 18.dp, vertical = 12.dp)

    ) {
        Button(
            onClick = {},
            modifier = Modifier
                .width(8.dp)
                .height(48.dp),
        ) {}
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f) // Pastikan Column mengambil sisa ruang di Row
        ) {
            Text(
                text = admin.nama,
                fontSize = 16.sp,
            )
            Text(
                text = "${admin.username} (Admin)",
                fontSize = 14.sp,
                color = Color.Gray,
            )
            if (isExpanded.value) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
                ) {
                    Button(
                        onClick = {
                            showDeleteDialog.value = true
                        },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(34.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                    ) {
                        Text(text = "Hapus", fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            val intent = Intent(context, EditAdminActivity::class.java)
                            intent.putExtra("adminId", admin.id) // Kirimkan ID Admin
                            context.startActivity(intent) // Mulai activity baru
                        },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(34.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(text = "Edit", fontSize = 12.sp)
                    }
                }
            }
        }
        Icon(
            modifier = Modifier.padding(top = 12.dp),
            imageVector = if (isExpanded.value) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight,
            contentDescription = "Show more"
        )
    }

    // AlertDialog untuk konfirmasi penghapusan
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = {
                Text(text = "Konfirmasi Hapus")
            },
            text = {
                Text(text = "Apakah Anda yakin ingin menghapus admin ${admin.nama}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        usersViewModel.deleteAdmin(admin)
                        showDeleteDialog.value = false
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog.value = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ItemMitra(mitra: Mitra, usersViewModel: UsersViewModel = viewModel(),  sapiViewModel: SapiViewModel= viewModel()) {
    val context = LocalContext.current

    var isExpanded = remember { mutableStateOf(false) }
    var showDeleteDialog = remember { mutableStateOf(false) }

    val sapiList = sapiViewModel.sapiList.collectAsState().value.filter { it.idMitra == mitra.id}

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isExpanded.value = !isExpanded.value
            }
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Button(
            onClick = {},
            modifier = Modifier
                .width(8.dp)
                .height(48.dp),
        ) {}
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = mitra.nama,
                fontSize = 16.sp,
            )
            Text(
                text = "${mitra.username} (Mitra)",
                fontSize = 14.sp,
                color = Color.Gray,
            )
            if (isExpanded.value) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "No Hp : ${mitra.noHp}",
                        fontSize = 14.sp,
                    )
                    Text(
                        text = "Jumlah Sapi : ${sapiList.size}",
                        fontSize = 14.sp,
                    )

                    Text(
                        text = "Alamat : ${mitra.alamat}",
                        fontSize = 14.sp,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
                    ) {
                        Button(
                            onClick = {
                                showDeleteDialog.value = true
                            },
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.height(34.dp),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                        ) {
                            Text(text = "Hapus", fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                val intent = Intent(context, EditMitraActivity::class.java)
                                intent.putExtra("mitraId", mitra.id)
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.height(34.dp),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(text = "Edit", fontSize = 12.sp)
                        }
                    }
                }

            }
        }
        Icon(
            modifier = Modifier.padding(top = 12.dp),
            imageVector = if (isExpanded.value) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight,
            contentDescription = "Show more"
        )
    }

    // AlertDialog untuk konfirmasi penghapusan
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = {
                Text(text = "Konfirmasi Hapus")
            },
            text = {
                Text(text = "Apakah Anda yakin ingin menghapus mitra ${mitra.nama}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        usersViewModel.deleteMitra(mitra)
                        showDeleteDialog.value = false
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog.value = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}


