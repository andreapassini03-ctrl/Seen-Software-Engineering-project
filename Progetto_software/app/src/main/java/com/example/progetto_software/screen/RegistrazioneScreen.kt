// file: com.example.progetto_software.Screen/RegistrazioneScreen.kt
package com.example.progetto_software.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.viewmodel.compose.viewModel // Import this
import com.example.progetto_software.data.Utente // Import your Utente data class
import androidx.compose.ui.platform.LocalContext // For accessing the database instance

// IMPORTANT: Ensure these imports point to the correct ViewModel and Factory
// Based on previous discussions, it should be in 'viewmodel' package, not 'Controller'
// Make sure UtenteViewModel has the registerUtente method as well as checkUsernameExists
import com.example.progetto_software.controller.UtenteViewModel
import com.example.progetto_software.controller.UtenteViewModelFactory
import com.example.progetto_software.controller.RegistrationState // NEW: Import the RegistrationState
import com.example.progetto_software.controller.SchedaContenutoPersonaleViewModel
import com.example.progetto_software.data.Sezione
import com.example.progetto_software.database.MyApplicationClass // Updated import for YourApplicationClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrazioneScreen(
    onRegistrationSuccess: () -> Unit, // Callback per quando la registrazione ha successo
    onBackClick: () -> Unit, // Callback per il tasto indietro
    // Pass the ViewModel as a parameter or obtain it using viewModel()
    utenteViewModel: UtenteViewModel = viewModel(
        factory = UtenteViewModelFactory(
            (LocalContext.current.applicationContext as MyApplicationClass).database.utenteDao(),
            LocalContext.current.applicationContext as MyApplicationClass // <--- PASS 'application' HERE
        )
    )
) {
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val registrationState by utenteViewModel.registrationState.collectAsState()

    // Nuovo stato per i messaggi di errore
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrazione", color = Color.White) },
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
        containerColor = Color(0xFFF9EE5C)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .background(Color(0xFFF9EE5C))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Crea un nuovo account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE0D8FF),
                    unfocusedContainerColor = Color(0xFFE0D8FF),
                    disabledContainerColor = Color(0xFFE0D8FF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = cognome,
                onValueChange = { cognome = it },
                label = { Text("Cognome") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE0D8FF),
                    unfocusedContainerColor = Color(0xFFE0D8FF),
                    disabledContainerColor = Color(0xFFE0D8FF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE0D8FF),
                    unfocusedContainerColor = Color(0xFFE0D8FF),
                    disabledContainerColor = Color(0xFFE0D8FF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = newUsername,
                onValueChange = { newUsername = it },
                label = { Text("Username") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE0D8FF),
                    unfocusedContainerColor = Color(0xFFE0D8FF),
                    disabledContainerColor = Color(0xFFE0D8FF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val imageVector = if (passwordVisibility)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(imageVector = imageVector, contentDescription = "Toggle password visibility")
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE0D8FF),
                    unfocusedContainerColor = Color(0xFFE0D8FF),
                    disabledContainerColor = Color(0xFFE0D8FF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Conferma Password") },
                singleLine = true,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val imageVector = if (passwordVisibility)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(imageVector = imageVector, contentDescription = "Toggle password visibility")
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE0D8FF),
                    unfocusedContainerColor = Color(0xFFE0D8FF),
                    disabledContainerColor = Color(0xFFE0D8FF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Visualizza il messaggio di errore se presente
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    errorMessage = null // Clear previous errors on new attempt
                    if (nome.isBlank() || cognome.isBlank() || email.isBlank() ||
                        newUsername.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()
                    ) {
                        errorMessage = "Per favore, compila tutti i campi."
                    } else if (newPassword != confirmPassword) {
                        errorMessage = "Le password non corrispondono."
                    } else {
                        // Se le validazioni di base passano, tenta la registrazione tramite ViewModel
                        val newUser = Utente(
                            id = newUsername, // Assegna l'ID all'username per unicità (supponendo che ID sia username)
                            nome = nome,
                            cognome = cognome,
                            email = email,
                            username = newUsername,
                            hashPassword = newPassword, // Ricorda di hashare questa password nel ViewModel!
                            sezione = Sezione.FILM.toString() // Valore di default
                        )
                        utenteViewModel.registerUtente(newUser) // La logica di controllo username è nel ViewModel
                        onRegistrationSuccess() // Callback per quando la registrazione ha successo
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = MaterialTheme.shapes.medium,
                enabled = registrationState !is RegistrationState.Loading // Disabilita durante il caricamento
            ) {
                if (registrationState is RegistrationState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(text = "Registrati", fontSize = 20.sp, color = Color.White)
                }
            }
        }
    }
}