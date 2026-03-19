package com.example.progetto_software.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progetto_software.AppScreen

// Import del ViewModel corretto per questa schermata
import com.example.progetto_software.controller.SchedaContenutoPersonaleViewModel

// Import necessario per i composable dettagli
import com.example.progetto_software.components.FilmDetailContent
import com.example.progetto_software.components.SerieTvDetailContent
import com.example.progetto_software.components.MyBottomNavBar

// Import delle classi dati
import com.example.progetto_software.data.Film
import com.example.progetto_software.data.SerieTv
import com.example.progetto_software.data.Episodio // Import Episodio
import com.example.progetto_software.data.Stato // Importa l'enum Stato

// Import per i ViewModel
import com.example.progetto_software.controller.UtenteViewModel
import com.example.progetto_software.controller.UtenteViewModelFactory

// Import per il Database
import com.example.progetto_software.database.AppDatabase
import com.example.progetto_software.database.MyApplicationClass


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaContenutoPersonaleScreen(
    contentId: String,
    contentType: String,
    onFlagReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    onAreaPersonaleClick: () -> Unit,
    onCatalogoClick: () -> Unit,
    onRecensioneClick: (String, String, String) -> Unit, // Corrected signature
    onNavigateToSegnalazione: () -> Unit,
    viewModel: SchedaContenutoPersonaleViewModel // ViewModel passed from MainActivity
) {
    val context = LocalContext.current
    val application = context.applicationContext as MyApplicationClass
    val database = AppDatabase.getDatabase(context)

    val utenteDao = database.utenteDao()

    val utenteViewModel: UtenteViewModel = viewModel(
        factory = UtenteViewModelFactory(utenteDao, application)
    )
    val loggedInUser by utenteViewModel.loggedInUser.collectAsState()


    // Osserva lo stato del ViewModel personale
    val contenutoState by viewModel.contenuto.collectAsStateWithLifecycle()
    val contenutoUtenteState by viewModel.contenutoUtenteState.collectAsStateWithLifecycle()

    // State per forzare un ricaricamento manuale (es: dopo l'aggiunta)
    var refreshTrigger by remember { mutableStateOf(0) }

    // Questo LaunchedEffect carica i dati quando contentId, contentType, loggedInUser cambiano,
    // o quando il refreshTrigger viene modificato
    LaunchedEffect(contentId, contentType, loggedInUser?.id, refreshTrigger) {
        Log.d("SchedaContenutoPersonaleScreen", "LaunchedEffect triggered. contentId: $contentId, contentType: $contentType, loggedInUser.id: ${loggedInUser?.id}")
        val userId = loggedInUser?.id
        if (userId != null) {
            viewModel.loadContent(
                contentId,
                contentType,
                userId
            )
            Log.d("SchedaContenutoPersonaleScreen", "loadContent called for userId: $userId")
        } else {
            Toast.makeText(context, "Devi essere loggato per vedere i tuoi contenuti personali.", Toast.LENGTH_LONG).show()
            Log.e("SchedaContenutoPersonaleScreen", "loggedInUser is null, cannot load personal content.")
            // Potresti voler navigare indietro o alla schermata di login qui
            onBackClick() // Esempio: naviga indietro se l'utente non è loggato
        }
    }

    // Log per lo stato del contenuto e dell'utente dopo il caricamento
    Log.d("SchedaContenutoPersonaleScreen", "Current contenutoState: $contenutoState")
    Log.d("SchedaContenutoPersonaleScreen", "Current contenutoUtenteState: $contenutoUtenteState")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Nessun titolo, l'immagine è già un buon punto di partenza */ },
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
                selectedRoute = AppScreen.Utente.route, // Route corretta per questa schermata
                onCatalogoClick = onCatalogoClick,
                onAreaPersonaleClick = onAreaPersonaleClick
            )
        },
        floatingActionButton = {
            //solo se serie tv, film e non visto
            val showRemoveButton = (contenutoState is Film || contenutoState is SerieTv) &&
                    contenutoUtenteState != null &&
                    contenutoUtenteState?.statoEnum == Stato.NON_VISTO

            if (showRemoveButton) {
                val isInUserList = contenutoUtenteState != null // True se il contenuto è nella lista dell'utente

                // Colori e icone per lo stato "rimuovibile" (quando il contenuto è già nella lista)
                val icon = Icons.Filled.Delete // Icona di cestino
                val description = "Rimuovi dall'area personale"
                val containerColor = Color(0xFFB90C0C) // Rosso scuro per la rimozione

                FloatingActionButton(
                    onClick = {
                        loggedInUser?.let { currentUser ->
                            if (isInUserList) { // Se il contenuto è già nella lista dell'utente, procedi con la rimozione
                                contenutoUtenteState?.let { userContentToRemove ->
                                    // Chiama il ViewModel per eliminare il Contenuto_utente
                                    viewModel.rimuoviContenutoUtente(userContentToRemove)

                                    // Mostra un messaggio di conferma
                                    Toast.makeText(context, "Rimosso dall'area personale!", Toast.LENGTH_SHORT).show()

                                    onBackClick() // Questo triggerà il "popup indietro"
                                } ?: run {
                                    // Questo caso dovrebbe essere raro se isInUserList è true ma contenutoUtenteState è null
                                    Toast.makeText(context, "Errore: Contenuto utente non trovato per la rimozione.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Se il contenuto NON è nella lista dell'utente, non fare nulla o avvisa l'utente
                                Toast.makeText(context, "Questo contenuto non è nella tua area personale.", Toast.LENGTH_SHORT).show()
                            }
                        } ?: run {
                            // Se l'utente non è loggato
                            Toast.makeText(context, "Devi essere loggato per gestire i contenuti!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = containerColor,
                    modifier = Modifier.size(56.dp),
                    content = {
                        Icon(icon, description, tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                )
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (contenutoState == null) {
                Log.d("SchedaContenutoPersonaleScreen", "contenutoState is null, showing CircularProgressIndicator")
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Yellow)
            }  else {
                Log.d("SchedaContenutoPersonaleScreen", "contenutoState is NOT null. Attempting to display content.")
                when (val contenuto = contenutoState) {
                    is Film -> {
                        // FIX: Ottieni le recensioni direttamente dall'oggetto Film
                        val reviewsToDisplay = contenuto.recensioni
                        Log.d("SchedaContenutoPersonaleScreen", "Film reviews count: ${reviewsToDisplay.size}")

                        FilmDetailContent(
                            film = contenuto,
                            reviews = reviewsToDisplay,
                            onNavigateToSegnalazione = onNavigateToSegnalazione,
                            onCatalog = false,
                            // *** FIX: Pass the lambda correctly here ***
                            onRecensioneClick = {
                                loggedInUser?.id?.let { userId ->
                                    onRecensioneClick(contenuto.id, "film", userId)
                                } ?: run {
                                    Toast.makeText(context, "Utente non loggato. Impossibile scrivere recensione.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            stato = contenutoUtenteState?.statoEnum ?: Stato.NON_VISTO,
                            onUpdateStato = { newStato ->
                                val userId = loggedInUser?.id
                                if (userId != null) {
                                    viewModel.modificaStato(
                                        newStato,
                                        contenuto.id,
                                        "film",
                                        userId
                                    )
                                    Toast.makeText(
                                        context,
                                        "Stato aggiornato a ${newStato.name}!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Utente non loggato.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            viewModel = viewModel
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    is SerieTv -> {
                        // FIX: Ottieni recensioni ed episodi direttamente dall'oggetto SerieTv
                        val reviewsToDisplay = contenuto.recensioni
                        val episodiToDisplay = contenuto.episodi

                        Log.d("SchedaContenutoPersonaleScreen", "SerieTv reviews count: ${reviewsToDisplay.size}")
                        Log.d("SchedaContenutoPersonaleScreen", "SerieTv episodes count: ${episodiToDisplay.size}")

                        SerieTvDetailContent(
                            serieTv = contenuto,
                            reviews = reviewsToDisplay,
                            episodi = episodiToDisplay,
                            onNavigateToSegnalazione = onNavigateToSegnalazione,
                            onCatalog = false,
                            // *** FIX: Pass the lambda correctly here ***
                            onRecensioneClick = {
                                loggedInUser?.id?.let { userId ->
                                    onRecensioneClick(contenuto.id, "serieTv", userId)
                                } ?: run {
                                    Toast.makeText(context, "Utente non loggato. Impossibile scrivere recensione.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onUpdateStato = { newStato ->
                                val userId = loggedInUser?.id
                                if (userId != null) {
                                    viewModel.modificaStato(newStato,
                                        contenuto.id,
                                        "serieTv",
                                        userId
                                    )
                                    Toast.makeText(context, "Stato aggiornato a ${newStato.name}!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Utente non loggato.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onEpisodeClick = { episode ->
                                val userId = loggedInUser?.id
                                if (userId != null) {
                                    viewModel.loadContent(episode.id, "episodio", userId)
                                } else {
                                    Toast.makeText(context, "Utente non loggato.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            viewModel = viewModel
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    is Episodio -> {

                        val reviewsToDisplay = contenuto.recensioni
                        Log.d("SchedaContenutoPersonaleScreen", "Displaying Episode: ${contenuto.nome}")
                        Log.d("SchedaContenutoPersonaleScreen", "Episode reviews count: ${reviewsToDisplay.size}")

                        // IMPORTANT: You are passing an Episodio to FilmDetailContent.
                        // FilmDetailContent expects a 'Film' object.
                        // This might lead to runtime issues or incorrect display.
                        // You should ideally have a separate `EpisodeDetailContent` or
                        // ensure `FilmDetailContent` can handle `Episodio` correctly.
                        FilmDetailContent(
                            film = contenuto, // Cast will happen, but check if FilmDetailContent correctly handles Episodio data
                            reviews = reviewsToDisplay,
                            onNavigateToSegnalazione = onNavigateToSegnalazione,
                            onCatalog = false,
                            // *** FIX: Pass the lambda correctly here ***
                            onRecensioneClick = {
                                loggedInUser?.id?.let { userId ->
                                    onRecensioneClick(contenuto.id, "episodio", userId) // Assuming "episodio" is the correct content type
                                } ?: run {
                                    Toast.makeText(context, "Utente non loggato. Impossibile scrivere recensione.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            stato = contenutoUtenteState?.statoEnum ?: Stato.NON_VISTO,
                            onUpdateStato = { newStato ->
                                val userId = loggedInUser?.id
                                if (userId != null) {
                                    viewModel.modificaStato(
                                        newStato,
                                        contenuto.id,
                                        "film", // This might need to be "episodio" depending on your data model
                                        userId
                                    )
                                    Toast.makeText(context, "Stato aggiornato a ${newStato.name}!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Utente non loggato.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            viewModel = viewModel
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    else -> {
                        Log.e("SchedaContenutoPersonaleScreen", "ContenutoState is of an unknown type: $contenuto")
                        Text(
                            text = "Contenuto personale non disponibile.",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}