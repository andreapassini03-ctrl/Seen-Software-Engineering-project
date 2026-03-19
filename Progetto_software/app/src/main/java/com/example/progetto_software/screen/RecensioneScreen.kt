package com.example.progetto_software.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progetto_software.components.MyBottomNavBar
import com.example.progetto_software.AppScreen
import com.example.progetto_software.controller.SchedaContenutoPersonaleViewModel
import com.example.progetto_software.data.Recensione
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.util.Date
import android.util.Log
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Import for observing StateFlow with lifecycle awareness

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecensioneScreen(
    onBackClick: () -> Unit,
    schedaContenutoPersonaleViewModel: SchedaContenutoPersonaleViewModel,
    contentId: String,
    contentType: String,
    loggedInUserId: String,
    onCatalogoClick: () -> Unit,
    onAreaPersonaleClick: () -> Unit
) {
    var rating by remember { mutableFloatStateOf(0f) }
    var commento by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Observe the reviewAddedSuccessfully state from the ViewModel.
    // collectAsStateWithLifecycle ensures proper lifecycle handling for StateFlows.
    val reviewAddedSuccessfully by schedaContenutoPersonaleViewModel.reviewAddedSuccessfully.collectAsStateWithLifecycle()

    // LaunchedEffect to react to the review being added successfully.
    // This effect will run whenever 'reviewAddedSuccessfully' changes its value.
    LaunchedEffect(key1 = reviewAddedSuccessfully) {
        if (reviewAddedSuccessfully) {
            // Show a success message to the user.
            Toast.makeText(context, "Recensione pubblicata con successo!", Toast.LENGTH_SHORT).show()
            // Reset the ViewModel's state to prevent re-triggering navigation on recomposition.
            schedaContenutoPersonaleViewModel.resetReviewAddedSuccessfully()
            // Navigate back to the previous screen.
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", color = Color.White) },
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
        bottomBar = {
            MyBottomNavBar(
                selectedRoute = AppScreen.Utente.route,
                onCatalogoClick = onCatalogoClick,
                onAreaPersonaleClick = onAreaPersonaleClick
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Valutazione*",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stella ${index + 1}",
                        tint = if (index < rating) Color.Yellow else Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { rating = (index + 1).toFloat() }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = commento,
                onValueChange = { commento = it },
                label = { Text("Commento", color = Color.Gray) },
                placeholder = { Text("Scrivi il tuo commento qui...", color = Color.Gray.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.DarkGray,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.Yellow,
                    unfocusedLabelColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = false,
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "L'hai visto, ti sei fatto un'idea.\nOra fallo sapere a tutti, senza spoiler!",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (rating == 0f) {
                        Toast.makeText(context, "Per favore, assegna una valutazione!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (commento.isBlank()) {
                        Toast.makeText(context, "Il commento non può essere vuoto!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Create the Recensione object
                    val nuovaRecensione = Recensione(
                        valutazione = rating.toInt(),
                        commento = commento,
                        timestamp = Date(System.currentTimeMillis()),
                        idUtente = loggedInUserId,
                        idFilm = if (contentType == "film") contentId else null,
                        idSerieTv = if (contentType == "serieTv") contentId else null,
                        idEpisodio = if (contentType == "episodio") contentId else null
                    )

                    // Call the ViewModel method to add the review.
                    // The ViewModel should then update its 'reviewAddedSuccessfully' state.
                    schedaContenutoPersonaleViewModel.aggiungiRecensione(nuovaRecensione, contentId)

                    // Navigation back is now handled by the LaunchedEffect,
                    // which waits for the ViewModel to confirm success.
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
            ) {
                Text(
                    text = "PUBBLICA",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
