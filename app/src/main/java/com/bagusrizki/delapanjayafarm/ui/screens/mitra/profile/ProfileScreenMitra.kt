package com.bagusrizki.delapanjayafarm.ui.screens.mitra.profile


import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.LoginActivity
import com.bagusrizki.delapanjayafarm.UserPreferences
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.ui.screens.mitra.home.SapiMitraViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProfileScreenMitra(
    userLogin : Mitra, sapiMitraViewModel: SapiMitraViewModel = viewModel()
) {
    val context = LocalContext.current

    // Observasi data
    val userPreferences = UserPreferences(context)

    val sapiList = sapiMitraViewModel.sapiList.collectAsState().value.filter { it.idMitra == userLogin.id}

    var isExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {
        Text(
            text = "Profil Anda",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isExpanded.value = !isExpanded.value
                    }
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f) // Pastikan Column mengambil sisa ruang di Row
                ) {
                    Text(
                        text = userLogin?.nama ?: "",
                        fontSize = 16.sp,
                    )
                    Text(
                        text = "${userLogin?.username} (Mitra)",
                        fontSize = 14.sp,
                    )
                    if (isExpanded.value) {
                        if (isExpanded.value) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "No Hp : ${userLogin.noHp}",
                                    fontSize = 14.sp,
                                )
                                Text(
                                    text = "Jumlah Sapi : ${sapiList.size}",
                                    fontSize = 14.sp,
                                )

                                Text(
                                    text = "Alamat : ${userLogin.alamat}",
                                    fontSize = 14.sp,
                                )
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
        }
//        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    CoroutineScope(Dispatchers.IO).launch {
                        userPreferences.clearUser()
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = "Logout")
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = "Logout", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(text = "Keluar dari akun anda yang sudah masuk saat ini ", fontSize = 12.sp, color = Color.Gray)
            }
        }

    }
}