package com.example.route_function

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.route_function.ui.theme.Route_functionTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Route_functionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HarborRoutingScreen()
                }
            }
        }
    }
}

@Composable
fun HarborRoutingScreen() {
    var longitude by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var displayResults by remember { mutableStateOf(false) }
    val buttonAlpha by remember { mutableStateOf(1f) }
    val buttonClickAnimation = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
        )

        if (displayResults) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center

            ) {
                Text("Result: \nLongitude - $longitude, \nLatitude - $latitude")
            }
        }
        //}

        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0072FF), Color(0xFF00C6FF))
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // First Row: Input bars side by side
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BasicTextField(
                    value = longitude,
                    onValueChange = { text: String -> longitude = text },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.LightGray)
                        .padding(8.dp)
                )

                BasicTextField(
                    value = latitude,
                    onValueChange = { text: String -> latitude = text },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.LightGray)
                        .padding(8.dp)
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Longitude Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            // Second Row: Button
            Button(
                onClick = {
                    displayResults = true
                }
            ) {
                Text(text = "SEARCH Route")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HarborRoutingScreenPreview() {
    Route_functionTheme {
        var longitude by remember { mutableStateOf("") }
        var latitude by remember { mutableStateOf("") }
        var displayResults by remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HarborRoutingScreen()
        }
    }
}
