package com.example.myapplication.appUI.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RouteDialogChild(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    title: @Composable (ColumnScope.() -> Unit),
    body: @Composable (ColumnScope.() -> Unit)
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest.invoke() },
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
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Dismiss")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetRoute(
    pointStartLon: String,
    onPointStartLonChange: (String) -> Unit,
    pointStartLat: String,
    onPointStartLatChange: (String) -> Unit,
    pointEndLon: String,
    onPointEndLonChange: (String) -> Unit,
    pointEndLat: String,
    onPointEndLatChange: (String) -> Unit,
    routeInitiaized: MutableState<Boolean>,
    loadDialogState: MutableState<Boolean>,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onDeleteClick: () -> Unit
) {
    if (loadDialogState.value) {
        RouteDialogChild(
            onDismissRequest = onDismissRequest,
            onConfirmation = onConfirmation,
            title = {
                Text(text = "Create Route")
            },
            body = {
                Column {
                    Column {
                        Text(text = "Start Point: ", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        TextField(
                            value = pointStartLon,
                            onValueChange = { onPointStartLonChange(it) },
                            label = { Text("Start longitude value...") }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        TextField(
                            value = pointStartLat,
                            onValueChange = { onPointStartLatChange(it) },
                            label = { Text("Start latitude value...") }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column {
                        Text(text = "Destination Point: ", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        TextField(
                            value = pointEndLon,
                            onValueChange = { onPointEndLonChange(it) },
                            label = { Text("Destination longitude value...") }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        TextField(
                            value = pointEndLat,
                            onValueChange = { onPointEndLatChange(it) },
                            label = { Text("Destination latitude value...") }
                        )
                    }
                    if(routeInitiaized.value){
                        Button(
                            onClick = { onDeleteClick() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) { Text(text = "delete route") }
                    }
                }
            }
        )
    }
}
@Composable
fun RouteMain() {
    val loadDialogState = remember { mutableStateOf(false) }
    // start point value state
    var pointStartLon by remember { mutableStateOf("") }
    var pointStartLat by remember { mutableStateOf("") }
    // end point value state
    var pointEndLon by remember { mutableStateOf("") }
    var pointEndLat by remember { mutableStateOf("") }
    // state of route
    val routeInitiaized = remember { mutableStateOf(false ) }

    // Handle dismiss req states
    fun dismisssHandle() {
        loadDialogState.value = false
        pointStartLon = ""
        pointStartLat = ""
        pointEndLon = ""
        pointEndLat = ""
    }

    //handle confirmation req states
    fun confirmHandle(){
        loadDialogState.value = false
        routeInitiaized.value = true
    }

    //handle delete request states -> reset all states to false and empty
    fun deleteHandle(){
        loadDialogState.value = false
        routeInitiaized.value = false
        pointStartLon = ""
        pointStartLat = ""
        pointEndLon = ""
        pointEndLat = ""
    }

    Column {
        //change state so it shows a button or to show RouteInfo Button
        if (!routeInitiaized.value){
            TextButton(onClick = { loadDialogState.value = true }) {
                Text("Create Route")
            }
            SetRoute(
                pointStartLon = pointStartLon,
                onPointStartLonChange = { pointStartLon = it },
                pointStartLat = pointStartLat,
                onPointStartLatChange = { pointStartLat = it },
                pointEndLon = pointEndLon,
                onPointEndLonChange = {pointEndLon = it},
                pointEndLat = pointEndLat,
                onPointEndLatChange = {pointEndLat = it},
                routeInitiaized = routeInitiaized,
                loadDialogState = loadDialogState,
                onDismissRequest = { dismisssHandle() },
                onConfirmation = { confirmHandle() },
                onDeleteClick = {deleteHandle()}
            )
        }
        else{
            TextButton(onClick = { loadDialogState.value = true }) {
                Text("View Route")
            }
            SetRoute(
                pointStartLon = pointStartLon,
                onPointStartLonChange = { pointStartLon = it },
                pointStartLat = pointStartLat,
                onPointStartLatChange = { pointStartLat = it },
                pointEndLon = pointEndLon,
                onPointEndLonChange = {pointEndLon = it},
                pointEndLat = pointEndLat,
                onPointEndLatChange = {pointEndLat = it},
                routeInitiaized = routeInitiaized,
                loadDialogState = loadDialogState,
                onDismissRequest = { loadDialogState.value = false },
                onConfirmation = { confirmHandle() },
                onDeleteClick = {deleteHandle()}
            )
        }
    }
}