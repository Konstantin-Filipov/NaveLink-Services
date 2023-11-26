package com.example.myapplication.appUI.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.room.Room.databaseBuilder
import com.example.myapplication.appUI.components.ServicesWindow
import com.example.myapplication.appUI.components.RouteMain
import com.example.myapplication.appUI.components.splitGeometryString
import com.example.myapplication.data.Service
import com.example.myapplication.data.database.ServiceDatabase
import com.example.myapplication.network.getServices
import com.example.myapplication.ui.theme.MyApplicationTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val db = databaseBuilder(
            applicationContext,
            ServiceDatabase::class.java, "service_database").build()

        val coordinatePairList = mutableListOf<String>()
/*
        runBlocking {
            launch {
                .also { db = it }
            }
        }*/
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HomeScreen(db, coordinatePairList)
                }
            }
        }
    }
}
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(db: ServiceDatabase, coordinatePairList: MutableList<String>) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("NaveLink")
                }
            )
        },
//        bottomBar = {
//            BottomAppBar(
//                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                contentColor = MaterialTheme.colorScheme.primary,
//            ) {
//                Text(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    textAlign = TextAlign.Center,
//                    text = "Bottom app bar",
//                )
//            }
//        },
//        floatingActionButton = {
//            FloatingActionButton(onClick = {}) {
//                Icon(Icons.Default.Add, contentDescription = "Add")
//            }
//        }
    ){innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ){
            LoadServices(db, coordinatePairList)
            RouteMain()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadServices(db: ServiceDatabase, coordinatePairList: MutableList<String>){
    val serviceState = remember { mutableStateOf<List<Service>>(emptyList()) }
    var keywordState = remember { mutableStateOf("") }
    val coordinateSearchState = remember { mutableStateOf(false) }
    val coordinateSearchConfirmationState = remember { mutableStateOf(false)}


    LaunchedEffect(true) {
        //LaunchedEffect for handling errors
        getServices { serviceList ->
            serviceList?.let {
                // state gets updated when data == available()
                serviceState.value = it
            }
        }
    }



    val filteredServices =
        serviceState.value.filter { service ->
            val geometryArray = splitGeometryString(service.geometry)
            val serviceCoordinatePairs = geometryArray.subList(1, geometryArray.size).chunked(2)
            Log.i("coordPair elem test", "$coordinatePairList")
            Log.i("serviceCoordinatePairs", "$serviceCoordinatePairs")
            if(coordinateSearchConfirmationState.value){
                var tempBool = true
                Log.i("new test", "$service")
                for (i in 0 until 6){
                    if(coordinatePairList[i].isNullOrBlank())
                        continue

                    tempBool = serviceCoordinatePairs[i][0] == coordinatePairList[i]
                            || serviceCoordinatePairs[i][1] == coordinatePairList[i+1]
                    if (!tempBool)
                        break
                }

                tempBool
/*                coordinatePairList.all{ element ->
                    if(element.isNotBlank()) {
                        Log.i("Element inside coord lambda", "$element")
                        Log.i("Element bool inside coord lambda", "${service.geometry.contains(element)}")
                        service.geometry.contains(element)
                    }
                    else
                        true
                }*/
            }
            else
                service.keywords.contains(keywordState.value, ignoreCase = true)
        }

    Column {
        // Add a TextField for user input
        Row(verticalAlignment = Alignment.CenterVertically){
            TextField(
                value = keywordState.value,
                onValueChange = {
                    coordinateSearchConfirmationState.value = false
                    keywordState.value = it
                                },
                label = { Text("Enter keyword") },
                modifier = Modifier.padding(vertical =  16.dp, horizontal = 8.dp)
            )
            IconButton(onClick = {
                coordinateSearchState.value = true
                Log.i("Coordinate search button clicked!", "${coordinateSearchState.value}")
            }) {
                Icon(Icons.Default.LocationOn,      // Not exactly an intuitive choice of icon
                    contentDescription = "Search By Coordinates",
                    modifier = Modifier.fillMaxSize())
            }
            if(coordinateSearchState.value){
                searchByCoordinate(coordinateSearchState, coordinatePairList, coordinateSearchConfirmationState) // filter service variable call here with argument return value
                Log.i("coordPairList declaration check", "$coordinatePairList")
                Log.i("Object ID outside method", "${System.identityHashCode(coordinatePairList)}")

            }
        }

        Log.i("filtereServicesByCoordinates check", "$filteredServices")
        ServicesWindow(filteredServices, db)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun searchByCoordinate(
    coordinateSearchState: MutableState<Boolean>,
    coordinatePairList: MutableList<String>,
    coordinateSearchConfirmationState: MutableState<Boolean>
) {
    // Each pair stands for a coordinate pair, together making up the minimum 5 pairs for a polygon
    var firstPairLong by remember { mutableStateOf("") }
    var firstPairLat by remember { mutableStateOf("") }

    var secondPairLong by remember { mutableStateOf("") }
    var secondPairLat by remember { mutableStateOf("") }

    var thirdPairLong by remember { mutableStateOf("") }
    var thirdPairLat by remember { mutableStateOf("") }

    var fourthPairLong by remember { mutableStateOf("") }
    var fourthPairLat by remember { mutableStateOf("") }

    var fifthPairLong by remember { mutableStateOf("") }
    var fifthPairLat by remember { mutableStateOf("") }


    AlertDialog(
        title = {
                Text(text = "Search by route")
        },
        text = {
            LazyColumn {
                item {
                    coordinateTextFields(
                        firstPairLong,
                        firstPairLat,
                        onFirstLongChange = { firstPairLong = it },
                        onFirstLatChange = {firstPairLat = it},
                        secondPairLong,
                        secondPairLat,
                        onSecondLongChange = { secondPairLong = it },
                        onSecondLatChange = { secondPairLat = it },
                        thirdPairLong,
                        thirdPairLat,
                        onThirdLongChange = { thirdPairLong = it },
                        onThirdLatChange = { thirdPairLat = it },
                        fourthPairLong,
                        fourthPairLat,
                        onFourthLongChange = { fourthPairLong = it },
                        onFourthLatChange = { fourthPairLat = it },
                        fifthPairLong,
                        fifthPairLat,
                        onFifthLongChange = { fifthPairLong = it },
                        onFifthLatChange = { fifthPairLat = it }
                    )
                }
            }
        },
        onDismissRequest = {
            Log.i("CLEAR CHECK", "")
            coordinatePairList.clear()
            coordinateSearchState.value = false
            coordinateSearchConfirmationState.value = false },

        confirmButton = {
            TextButton(onClick = {
                coordinatePairList.clear()
                // adding coordinate pairs to list here >
                coordinatePairList.addAll(listOf(
                    firstPairLong, firstPairLat,
                    secondPairLong, secondPairLat,
                    thirdPairLong, thirdPairLat,
                    fourthPairLong, fourthPairLat,
                    fifthPairLong, fifthPairLat))


                Log.i("coordinatePairList coord search check", "${coordinatePairList}")
                coordinateSearchState.value = false
                Log.i("Object ID inside method", "${System.identityHashCode(coordinatePairList)}")
                coordinateSearchConfirmationState.value = true

            }) {
                Text("Search")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                Log.i("CLEAR CHECK DISMISSBUTTON", "")
                coordinatePairList.clear()
                coordinateSearchState.value = false
                coordinateSearchConfirmationState.value = false
            }) {
                Text("Close & Clear")
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun coordinateTextFields(
    firstPairLong: String,
    firstPairLat: String,
    onFirstLongChange: (String) -> Unit,
    onFirstLatChange: (String) -> Unit,
    secondPairLong: String,
    secondPairLat: String,
    onSecondLongChange: (String) -> Unit,
    onSecondLatChange: (String) -> Unit,
    thirdPairLong: String,
    thirdPairLat: String,
    onThirdLongChange: (String) -> Unit,
    onThirdLatChange: (String) -> Unit,
    fourthPairLong: String,
    fourthPairLat: String,
    onFourthLongChange: (String) -> Unit,
    onFourthLatChange: (String) -> Unit,
    fifthPairLong: String,
    fifthPairLat: String,
    onFifthLongChange: (String) -> Unit,
    onFifthLatChange: (String) -> Unit
) {
    Text(text = "First Coordinate Pair", fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(5.dp))
    TextField(
        value = firstPairLong,
        onValueChange = { onFirstLongChange(it) },
        label = { Text("Longitude") }
    )
    Spacer(modifier = Modifier.height(5.dp))
    TextField(
        value = firstPairLat,
        onValueChange = { onFirstLatChange(it) },
        label = { Text("Latitude") }
    )

    Text(text = "Second Coordinate Pair", fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(5.dp))
    TextField(
        value = secondPairLong,
        onValueChange = { onSecondLongChange(it) },
        label = { Text("Longitude") }
    )
    Spacer(modifier = Modifier.height(5.dp))
    TextField(
        value = secondPairLat,
        onValueChange = { onSecondLatChange(it) },
        label = { Text("Latitude") }
    )

    Text(text = "Third Coordinate Pair", fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(5.dp))
    TextField(
        value = thirdPairLong,
        onValueChange = { onThirdLongChange(it) },
        label = { Text("Longitude") }
    )
    Spacer(modifier = Modifier.height(5.dp))
    TextField(
        value = thirdPairLat,
        onValueChange = { onThirdLatChange(it) },
        label = { Text("Latitude") }
    )

    Text(text = "Fourth Coordinate Pair", fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(5.dp))
    TextField(
        value = fourthPairLong,
        onValueChange = { onFourthLongChange(it) },
        label = { Text("Longitude") }
    )
    Spacer(modifier = Modifier.height(5.dp))
    TextField(
        value = fourthPairLat,
        onValueChange = { onFourthLatChange(it) },
        label = { Text("Latitude") }
    )

    Text(text = "Fifth Coordinate Pair", fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(5.dp))
    TextField(
        value = fifthPairLong,
        onValueChange = { onFifthLongChange(it) },
        label = { Text("Longitude") }
    )
    Spacer(modifier = Modifier.height(5.dp))
    TextField(
        value = fifthPairLat,
        onValueChange = { onFifthLatChange(it) },
        label = { Text("Latitude") }
    )
}
