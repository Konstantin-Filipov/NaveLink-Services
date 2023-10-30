package com.example.myapplication.appUI.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.Service

@Composable
fun ServicesWindow(services: List<Service>) {
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
                        Button(onClick = { serviceStates[service.id] = !showServiceInfo }) {
                            Text(
                                text = service.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        //when state is changed to the actual id value
                        //render Service Dialog Window
                        if (serviceStates[service.id] == true) {
                            LoadServiceWindow(service, serviceStates)
                        }
                    }
                }
            }
        }
    }
}
