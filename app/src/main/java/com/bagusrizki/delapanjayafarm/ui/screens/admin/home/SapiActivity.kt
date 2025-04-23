package com.bagusrizki.delapanjayafarm.ui.screens.admin.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagusrizki.delapanjayafarm.data.SapiDetail
import com.bagusrizki.delapanjayafarm.ui.components.admin.ItemSapi
import com.bagusrizki.delapanjayafarm.ui.screens.admin.home.ui.theme.DelapanJayaFarmTheme

class SapiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val intentLabel = intent.getStringExtra("status") ?: ""

        setContent {
            DelapanJayaFarmTheme {
                SapiScreen(onBackPressed = { finish() }, intentLabel = intentLabel)
            }
        }
    }
}

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SapiScreen(
    onBackPressed: () -> Unit = {},
    intentLabel: String = "",
    sapiViewModel: SapiViewModel= viewModel()
) {
    val context = LocalContext.current

    val sapiDetailList = sapiViewModel.sapiDetailList.collectAsState()
    val hargaSapi = sapiViewModel.listHargaSapi.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var filteredSapiList by remember { mutableStateOf(sapiDetailList.value) }

    var selectedStatus by remember { mutableStateOf(intentLabel) }

    LaunchedEffect(searchText, selectedStatus, sapiDetailList.value) {
        filteredSapiList = sapiDetailList.value.filter { sapi ->
            (searchText.isEmpty() || sapi.namaSapi.contains(searchText, ignoreCase = true)) &&
                    (selectedStatus.isEmpty() || sapi.statusSapi == selectedStatus)
        }
    }

    if (searchText.isEmpty() and selectedStatus.isEmpty()) {
        filteredSapiList = sapiDetailList.value
    }

    val loadingList = filteredSapiList.isEmpty()


    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("Sapi dalam Peternakan", fontSize = 20.sp)
                    },
                    navigationIcon = {
                        IconButton(onClick = { onBackPressed() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearching = !isSearching }) {
                            Icon(
                                imageVector = if (isSearching) Icons.Default.Clear else Icons.Default.Search,
                                contentDescription = "Cari"
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddSapiActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Icon(Icons.Filled.Add, "Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            if (isSearching) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Cari sapi...") },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            // Tampilkan LazyRow untuk status
            SapiStatusLazyRow(
                sapiList = sapiDetailList.value,
                selectedStatus = selectedStatus,
                onStatusSelected = { status -> selectedStatus = status }
            )

            if (loadingList) {
                Text("Loading...")
            } else {
                LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
                    items(filteredSapiList) { sapi ->
                        ItemSapi(sapi = sapi)
                    }
                }
            }
        }
    }
}


@Composable
fun SapiStatusLazyRow(
    sapiList: List<SapiDetail>,
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    val uniqueStatuses = sapiList.map { it.statusSapi }.distinct()

    LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
        items(uniqueStatuses.size) { index ->
            val status = uniqueStatuses[index]
            Card(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clickable {
                        val newStatus = if (selectedStatus == status) "" else status
                        onStatusSelected(newStatus)
                    },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedStatus == status) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                    contentColor = if (selectedStatus == status) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    fontSize = 12.sp
                )
            }
        }
    }
}





