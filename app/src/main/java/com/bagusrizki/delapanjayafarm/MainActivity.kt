package com.bagusrizki.delapanjayafarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
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
import com.bagusrizki.delapanjayafarm.data.Mitra
import com.bagusrizki.delapanjayafarm.ui.screens.mitra.home.HomeScreenMitra
import com.bagusrizki.delapanjayafarm.ui.screens.mitra.jadwal.JawalScreenMitra
import com.bagusrizki.delapanjayafarm.ui.screens.mitra.pemesanan.PemesananScreenMitra
import com.bagusrizki.delapanjayafarm.ui.screens.mitra.profile.ProfileScreenMitra
import com.bagusrizki.delapanjayafarm.ui.theme.DelapanJayaFarmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DelapanJayaFarmTheme {
                MainMitraScreen()
            }
        }
    }
}

@Composable
fun MainMitraScreen(mainViewModel: MainViewModel = MainViewModel()) {
    // state
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
    val mitraList by mainViewModel.mitraList.collectAsState()
    val mitra = mitraList.find { it.id == idUserLogin }

    val userLogin = mitra ?: Mitra()


    Scaffold(
        bottomBar = { BottomNavigationBarMitra(navController) },
        floatingActionButton = {

        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavigationHostMitra(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            userLogin = userLogin
        )
    }
}

@Composable
fun NavigationHostMitra(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userLogin: Mitra
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreenMitra(userLogin) }
        composable("jawal") { JawalScreenMitra(userLogin) }
        composable("pemesanan") { PemesananScreenMitra(userLogin) }
        composable("profile") { ProfileScreenMitra(userLogin) }
    }
}

@Composable
fun BottomNavigationBarMitra(navController: NavHostController) {
    val items = listOf(
        NavigationItemMitra.Home,
        NavigationItemMitra.Jadwal,
        NavigationItemMitra.Pesan,
        NavigationItemMitra.Profile
    )
    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
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

sealed class NavigationItemMitra(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String
) {
    object Home : NavigationItemMitra("home", Icons.Filled.Home, "Home")
    object Jadwal : NavigationItemMitra("jawal", Icons.Filled.Notifications, "Jadwal")
    object Pesan : NavigationItemMitra("pemesanan", Icons.Filled.Email, "Pemesanan")
    object Profile : NavigationItemMitra("profile", Icons.Filled.AccountCircle, "Profil")
}


@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_4_XL)
@Composable
fun MainPreview() {
    DelapanJayaFarmTheme {
        MainMitraScreen()
    }
}
