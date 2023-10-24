package com.example.myapplication

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://serviceregistry.navelink.org/api/"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MyApp()
                }
            }
        }
    }
}
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MyApp() {
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
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Bottom app bar",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ){innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ){
            Row (modifier = Modifier
                .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                SimpleOutlinedTextFieldSample()
                Button(onClick = { /* handle search click here */ }) {
                    Text("Search")
                }
            }
            LoadServices()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleOutlinedTextFieldSample() {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it },
    )
}

fun getServices(callback: (List<Service>?) -> Unit){
    val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(API::class.java)

    api.getCompanies().enqueue(object : Callback<List<Service>> {
        override fun onResponse(
            call: Call<List<Service>>,
            response: Response<List<Service>>
        ) {
            if (response.isSuccessful) {
                val serviceList = response.body() // save data in list

                if (serviceList != null) {
                    for (service in serviceList) {
                        Log.i(ContentValues.TAG, "onResponse: ID: ${service.id}")
                        Log.i(ContentValues.TAG, "onResponse: Name: ${service.name}")
                        Log.i(ContentValues.TAG, "onResponse: Keywords: ${service.keywords}")
                        Log.i(ContentValues.TAG, "onResponse: Geometry: ${service.geometry}")
                    }
                }
                callback(serviceList)
            }
        }

        override fun onFailure(call: Call<List<Service>>, t: Throwable) {
            Log.e(ContentValues.TAG, "Request failed: ${t.message}", t)
            callback(null)
        }
    })
}

@Composable
fun LoadServices(){
    val serviceState = remember { mutableStateOf<List<Service>>(emptyList()) }

    LaunchedEffect(true) {
        //LaunchedEffect for handling errors
        getServices { serviceList ->
            serviceList?.let {
                // state gets updated when data == available()
                serviceState.value = it
            }
        }
    }
    ServicesWindow(serviceState.value)
}

@Composable
fun ServicesWindow(services: List<Service>){
    val serviceStates = remember { mutableStateMapOf<Int, Boolean>() }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(420.dp) // Set the desired height
    ) {
        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 300.dp)) {
            items(services) { service ->
                Box(
                    Modifier
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary))
                        .padding(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Get the state for the current service
                        val showServiceInfo = serviceStates[service.id] ?: false

                        //title
                        Button(onClick = {serviceStates[service.id] = !showServiceInfo}){
                            Text(
                                text = service.name ,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        //when state is changed to actual id value
                        //render Service Dialog Window
                        if (serviceStates[service.id] == true) {
                            ServiceDialogWindow(
                                onDismissRequest = { serviceStates[service.id] = false },
                                onConfirmation = {
                                    serviceStates[service.id] = false
                                    println("Confirmation registered") // Add logic here to handle confirmation.
                                },
                                title = {
                                    Text(text = service.name)
                                },
                                body = {
                                    Text("Keywords: ${service.keywords}")
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Geometry: ${service.geometry}}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ServiceDialogWindow(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    title: @Composable (ColumnScope.() -> Unit),
    body: @Composable (ColumnScope.() -> Unit)
) {
    AlertDialog(
        title = {
            Column {
                title()
            }
        },
        text = {
           Column {
               body()
           }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Dismiss")
            }
        },
    )
}

