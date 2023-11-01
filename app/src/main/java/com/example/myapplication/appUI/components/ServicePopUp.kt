package com.example.myapplication.appUI.components

import android.os.Environment
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
import androidx.compose.foundation.lazy.items
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun splitGeometryString(geometry: String): List<String> {
    // Split the geometry string into an array of strings
    val splitArray = geometry.split("[\\s,:\\[\\]\"{}]+".toRegex())
    // Filter out empty strings and the words "type" and "coordinates"
    return splitArray.filter { it.isNotBlank() && it !in setOf("type", "coordinates") }
}

@Composable
fun RenderCoordinateTable(geometryArray: List<String>) {
    // Divide the array into pairs (longitude, latitude)
    val coordinatePairs = geometryArray.subList(1, geometryArray.size).chunked(2)
    val polygonType = geometryArray[0]

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

        // Data rows
        coordinatePairs.forEachIndexed { _, pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(text = pair[0], modifier = Modifier.weight(1f))
                Text(text = pair[1], modifier = Modifier.weight(1f))

            }
        }
    }
}

@Composable
fun LoadServiceWindow(service: Service, serviceStates: MutableMap<Int, Boolean>) {
    val geometryStr = service.geometry
    val geometryArray = splitGeometryString(geometryStr)
    val additionalInfo = listOf(
        "Service Type: ${service.serviceType}",
        "Endpoint URI: ${service.endpointUri}",
        "Instance as XML: ${service.instanceAsXml.content},${service.instanceAsXml.comment},${service.instanceAsXml.contentContentType}"
        // Add more strings if needed
    )

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
            Column {
                RenderCoordinateTable(geometryArray)
            }
        },
        additionalInfo = additionalInfo,
        xmlContent = service.instanceAsXml.content
    )
}
fun downloadXmlFile(xmlContent: String) {
    val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(directory, "downloaded_xml.xml")

    try {
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(xmlContent.toByteArray())
        fileOutputStream.close()
        Log.d("FileDownload", "File downloaded successfully")
    } catch (e: IOException) {
        Log.e("FileDownload", "Error while downloading file: ${e.message}")
    }
}
@Composable
fun ServiceDialogWindow(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    title: @Composable (ColumnScope.() -> Unit),
    body: @Composable (ColumnScope.() -> Unit),
    additionalInfo: List<String>,
    xmlContent: String
) {

    AlertDialog(
        title = {
            Column {
                title()
            }
        },
        text = {
            Column {
                LazyColumn{
                    item{body()}
                    items(additionalInfo) { info ->
                        Text(text = info)
                    }
                }

            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                downloadXmlFile(xmlContent)
            }) {
                Text("Save XML")
            }
        },
        dismissButton = {

            TextButton(onClick = onDismissRequest) {
                Text("Dismiss")
            }
        },
    )
}