package com.example.progetto_software.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progetto_software.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegnalazioneScreen(
    onBackClick: () -> Unit,
    onReportSubmitted: () -> Unit // Callback when the report is successfully submitted
) {
    var reportMessage by remember { mutableStateOf("") }
    val isReportButtonEnabled = reportMessage.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Segnalazione", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = Color.Black // Consistent background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Invia una segnalazione",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Descrivi il problema o la violazione che desideri segnalare.",
                color = Color.LightGray,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = reportMessage,
                onValueChange = { reportMessage = it },
                label = { Text("Il tuo messaggio", color = Color.Gray) },
                placeholder = { Text("Esempio: Contenuto inappropriato, bug, ecc.", color = Color.DarkGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Make it a multi-line input
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Yellow,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.Yellow,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.Yellow,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
// **FIX STARTS HERE**
                    focusedContainerColor = Color(0xFF1C1C1E), // Use focusedContainerColor
                    unfocusedContainerColor = Color(0xFF1C1C1E) // Use unfocusedContainerColor
// **FIX ENDS HERE**
                ),
                shape = MaterialTheme.shapes.medium,
                singleLine = false // Allow multiple lines
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
// TODO: Implement your report submission logic here (e.g., send to ViewModel, then to backend)
// For now, we'll just trigger the onReportSubmitted callback.
                    onReportSubmitted()
                },
                enabled = isReportButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Yellow,
                    disabledContainerColor = Color.Gray // Visually indicate disabled state
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Invia Segnalazione",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}