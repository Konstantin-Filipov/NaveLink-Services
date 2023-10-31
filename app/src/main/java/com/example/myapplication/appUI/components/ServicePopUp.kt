package com.example.myapplication.appUI.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Service
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

fun splitGeometryString(geometry: String): List<String> {
    // Split the geometry string into an array of strings
    val splitArray = geometry.split("[\\s,:\\[\\]\"{}]+".toRegex())
    // Filter out empty strings and the words "type" and "coordinates"
    return splitArray.filter { it.isNotBlank() && it !in setOf("type", "coordinates") }
}

fun geometryListToString(geometryType: String, coordinatePairs: List<List<String>>): String {
    val geometryString = StringBuilder()

    geometryString.append("$geometryType(")
    for(i in 0 until coordinatePairs.size){
        if (i != coordinatePairs.size-1){
            geometryString.append(coordinatePairs[i][0])
            geometryString.append(" ")
            geometryString.append(coordinatePairs[i][1])
            geometryString.append(", ")
        }
        else{
            geometryString.append(coordinatePairs[i][0])
            geometryString.append(" ")
            geometryString.append(coordinatePairs[i][1])
            geometryString.append(")")
        }
    }
    return geometryString.toString()
}

@Composable
fun RenderCoordinateTable(geometryArray: List<String>) {
    // Divide the array into pairs (longitude, latitude)
    val coordinatePairs = geometryArray.subList(1, geometryArray.size).chunked(2)
    val polygonType = geometryArray[0]
    // Initializing the ClipboardManager
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Column {
        // Header row for "Polygon" type
        Text(
            text = "Type: $polygonType",
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            fontWeight = FontWeight.Bold
        )

        // Header row for coordinates
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(text = "Longitude", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(48.dp))
            Text(text = "Latitude", fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Data rows
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(coordinatePairs.size) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Text(text = coordinatePairs[index][0], modifier = Modifier.weight(1f))
                        Text(text = coordinatePairs[index][1], modifier = Modifier.weight(1f))
                    }
                }
            }
            // Copy button
            Button(
                onClick = {
                    Log.i("print", "copy click!")
                    //call setText -> formatted polygon type and coordinates
                    clipboardManager.setText(AnnotatedString((geometryListToString(polygonType, coordinatePairs))))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) { Text(text = "copy $polygonType") }
        }
    }
}

@Composable
fun LoadServiceWindow(service: Service, serviceStates: MutableMap<Int, Boolean>) {
    val geometryStr = service.geometry
    val geometryArray = splitGeometryString(geometryStr)

    // Print each element in the array
    geometryArray.forEachIndexed { index, element ->
        Log.d("printer", "Element $index: $element")
    }

    ServiceDialogWindow(
        onDismissRequest = { serviceStates[service.id] = false },
        onConfirmation = {
            serviceStates[service.id] = false
            // implement UPDATE feature here
        },
        title = {
            Text(text = service.name)
        },
        body = {
            RenderCoordinateTable(geometryArray)

        }
    )
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
                Text("Add")
            }
        },
        dismissButton = {

            TextButton(onClick = onDismissRequest) {
                Text("Dismiss")
            }


        },
    )
}