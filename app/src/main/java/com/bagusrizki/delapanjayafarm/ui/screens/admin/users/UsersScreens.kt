package com.bagusrizki.delapanjayafarm.ui.screens.admin.users

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagusrizki.delapanjayafarm.R
import com.bagusrizki.delapanjayafarm.ui.components.admin.ItemAdmin
import com.bagusrizki.delapanjayafarm.ui.components.admin.ItemMitra
import com.bagusrizki.delapanjayafarm.ui.theme.DelapanJayaFarmTheme


@Composable
fun UsersScreen(usersViewModel: UsersViewModel = UsersViewModel()) {
    // Mendapatkan konteks
    val context = LocalContext.current

    // Admin Data
    val adminList by usersViewModel.adminList.collectAsState()

    // Mitra Data
    val mitraList by usersViewModel.mitraList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Pengguna",
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Component tambah admin
        Row(
            modifier = Modifier
                .clickable {
                    // Membuat intent untuk berpindah ke AddAdminActivity
                    val intent = Intent(context, AddAdminActivity::class.java)
                    // Memulai aktivitas baru
                    context.startActivity(intent)
                }
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_admin),
                contentDescription = null,
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
                    .size(24.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Tambah Admin",
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        // Component tambah mitra
        Row(
            modifier = Modifier
                .clickable {
                    // Membuat intent untuk berpindah ke AddMitraActivity
                    val intent = Intent(context, AddMitraActivity::class.java)
                    // Memulai aktivitas baru
                    context.startActivity(intent)
                }
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_groups),
                contentDescription = null,
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
                    .size(24.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Tambah Mitra",
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Text(
            text = "Pengguna dalam aplikasi",
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 12.dp
                ),
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )

        // Display the list of users
        LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
            items(adminList) { admin ->
                ItemAdmin(
                    admin = admin
                )
            }
            items(mitraList) { mitra ->
                ItemMitra(
                    mitra = mitra
                )
            }
        }

    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_4_XL)
@Composable
fun UsersScreenPreview() {
    DelapanJayaFarmTheme {
        UsersScreen()
    }
}
