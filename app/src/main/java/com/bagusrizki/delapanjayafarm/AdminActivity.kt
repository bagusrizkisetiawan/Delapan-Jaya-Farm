package com.bagusrizki.delapanjayafarm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bagusrizki.delapanjayafarm.data.UserLogin
import com.bagusrizki.delapanjayafarm.ui.screens.admin.home.HomeScreen
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.AddJadwalActivity
import com.bagusrizki.delapanjayafarm.ui.screens.admin.jadwal.JawalScreen
import com.bagusrizki.delapanjayafarm.ui.screens.admin.pemesanan.PemesananScreen
import com.bagusrizki.delapanjayafarm.ui.screens.admin.profile.ProfileScreen
import com.bagusrizki.delapanjayafarm.ui.screens.admin.users.UsersScreen
import com.bagusrizki.delapanjayafarm.ui.theme.DelapanJayaFarmTheme

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DelapanJayaFarmTheme {
                MainAdminScreen()
            }
        }
    }
}

@Composable
fun MainAdminScreen(adminViewModel: AdminViewModel = AdminViewModel()) {
    // state
    var namaUserLogin by remember { mutableStateOf("") }
    var idUserLogin by remember { mutableStateOf("") }

    // navigation
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route

    // context
    val context = LocalContext.current

    // preference user
    val userPreferences = UserPreferences(context)

    val userId by userPreferences.userIdFlow.collectAsState(initial = null)
    val userLevel by userPreferences.userLevelFlow.collectAsState(initial = null)

    LaunchedEffect(userId, userLevel) {
        if (userId != null && userLevel != null) {
            idUserLogin = userId as String
        }
    }

    // user Data
    val adminList by adminViewModel.adminList.collectAsState()
    val admin = adminList.find { it.id == idUserLogin }
    if (admin != null) {
        namaUserLogin = admin.nama
    }

    val userLogin = UserLogin(
        id = idUserLogin,
        nama = namaUserLogin,
        level = "Admin"
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            // Tampilkan FAB hanya jika di layar "jawal"
            if (currentRoute == "jawal") {
                FloatingActionButton(
                    onClick = {
                        // Membuat intent untuk berpindah ke AddJadwalActivity
                        val intent = Intent(context, AddJadwalActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Icon(Icons.Filled.Add, "Add")
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavigationHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            userLogin
        )
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userLogin: UserLogin
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen(userLogin) }
        composable("users") { UsersScreen() }
        composable("jawal") { JawalScreen() }
        composable("pemesanan") { PemesananScreen() }
        composable("profile") { ProfileScreen() }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Users,
        NavigationItem.Jadwals,
        NavigationItem.Pesan,
        NavigationItem.Profile
    )
    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    androidx.compose.material3.Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

sealed class NavigationItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String
) {
    object Home : NavigationItem("home", Icons.Filled.Home, "Home")
    object Users : NavigationItem("users", Icons.Filled.Person, "Pengguna")
    object Jadwals : NavigationItem("jawal", Icons.Filled.Notifications, "Jadwal")
    object Pesan : NavigationItem("pemesanan", Icons.Filled.Email, "Pesan")
    object Profile : NavigationItem("profile", Icons.Filled.AccountCircle, "Profil")
}


@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_4_XL)
@Composable
fun GreetingPreview2() {
    DelapanJayaFarmTheme {
        MainAdminScreen()
    }
}

