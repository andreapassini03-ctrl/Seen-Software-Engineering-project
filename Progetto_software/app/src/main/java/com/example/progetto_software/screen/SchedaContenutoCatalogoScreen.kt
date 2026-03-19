package com.example.progetto_software.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progetto_software.controller.ContenutoDettaglio
import com.example.progetto_software.controller.SchedaContenutoCatalogoViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progetto_software.AppScreen
import com.example.progetto_software.components.FilmDetailContent
// import com.example.progetto_software.components.ContentPosterCard // Commentato se non usato qui direttamente
import com.example.progetto_software.components.MyBottomNavBar
import com.example.progetto_software.components.SerieTvDetailContent
import com.example.progetto_software.controller.SchedaContenutoPersonaleViewModel
import com.example.progetto_software.controller.UtenteViewModel
import com.example.progetto_software.controller.UtenteViewModelFactory
import com.example.progetto_software.data.Contenuto
import com.example.progetto_software.data.Contenuto_utente
import com.example.progetto_software.data.Stato
import com.example.progetto_software.database.AppDatabase
import com.example.progetto_software.database.MyApplicationClass

object DurationFormatter {
    fun formatDuration(totalSeconds: Long): String {
        if (totalSeconds < 0) {
            return "N/D"
        }

        val hours = totalSeconds / 3600
        val remainingSecondsAfterHours = totalSeconds % 3600
        val minutes = remainingSecondsAfterHours / 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "0m"
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaContenutoCatalogoScreen(
    contentId: String,
    contentType: String,
    onBackClick: () -> Unit,
    onCatalogoClick: () -> Unit,
    onAreaPersonaleClick: () -> Unit,
    onNavigateToSegnalazione: () -> Unit,

    viewModel: SchedaContenutoCatalogoViewModel,

    schedaContenutoPersonaleViewModel: SchedaContenutoPersonaleViewModel
) {
    val context = LocalContext.current
    val application = context.applicationContext as MyApplicationClass
    val database = AppDatabase.getDatabase(context)

    val utenteDao = database.utenteDao()

    val utenteViewModel: UtenteViewModel = viewModel(
        factory = UtenteViewModelFactory(utenteDao, application)
    )
    val loggedInUser by utenteViewModel.loggedInUser.collectAsState()

    val contenutoState by viewModel.contenuto.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val contenutoUtenteState by viewModel.contenutoUtenteState.collectAsState()

    LaunchedEffect(contentId, contentType, loggedInUser?.id) {
        viewModel.loadContenuto(contentId, contentType, loggedInUser?.id)
    }

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
        floatingActionButton = {
            val isInUserList = contenutoUtenteState != null
            val icon = if (isInUserList) Icons.Filled.Check else Icons.Filled.Add
            val description = if (isInUserList) "Aggiunto all'area personale" else "Aggiungi all'area personale"
            val containerColor = if (isInUserList) Color(0xFF66BB6A) else Color.Yellow

            FloatingActionButton(
                onClick = {
                    loggedInUser?.let { currentUser ->
                        if (!isInUserList) { // Solo se non è già nella lista, aggiungi
                            val contenutoDaSalvare: Contenuto? = when (contenutoState) {
                                is ContenutoDettaglio.FilmDetail -> (contenutoState as ContenutoDettaglio.FilmDetail).film
                                is ContenutoDettaglio.SerieTvDetail -> (contenutoState as ContenutoDettaglio.SerieTvDetail).serieTv
                                else -> null
                            }

                            if (contenutoDaSalvare != null) {
                                val newUserContent = Contenuto_utente(
                                    stato = Stato.NON_VISTO,
                                    utente = currentUser,
                                    contenuto = contenutoDaSalvare
                                )
                                schedaContenutoPersonaleViewModel.saveContenutoUtente(newUserContent)
                                Toast.makeText(context, "Aggiunto all'area personale!", Toast.LENGTH_SHORT).show()
                                // Ricarica lo stato per aggiornare l'icona del FAB
                                viewModel.loadContenuto(contentId, contentType, loggedInUser?.id)
                            } else {
                                Toast.makeText(context, "Impossibile aggiungere: contenuto non disponibile.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Già presente nell'area personale!", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Toast.makeText(context, "Devi essere loggato per aggiungere contenuti!", Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = containerColor,
                modifier = Modifier.size(56.dp),
                content = {
                    Icon(icon, description, tint = Color.Black, modifier = Modifier.size(32.dp))
                }
            )
        },
        bottomBar = {
            MyBottomNavBar(
                selectedRoute = AppScreen.Catalogo.route,
                onCatalogoClick = onCatalogoClick,
                onAreaPersonaleClick = onAreaPersonaleClick
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when (contenutoState) {
                is ContenutoDettaglio.Loading -> {
                    CircularProgressIndicator(color = Color.Yellow, modifier = Modifier
                        .align(Alignment.Center)
                        .wrapContentSize())
                }
                is ContenutoDettaglio.FilmDetail -> {
                    val film = (contenutoState as ContenutoDettaglio.FilmDetail).film
                    FilmDetailContent(
                        film = film,
                        reviews = reviews,
                        onNavigateToSegnalazione = onNavigateToSegnalazione,
                        stato = null,
                        onUpdateStato = { /**/ },
                        onCatalog = true,
                        onRecensioneClick = { /**/ },
                        viewModel = null
                    )
                }
                is ContenutoDettaglio.SerieTvDetail -> {
                    val serieTv = (contenutoState as ContenutoDettaglio.SerieTvDetail).serieTv
                    SerieTvDetailContent(
                        serieTv = serieTv,
                        reviews = reviews,
                        episodi = null,
                        onNavigateToSegnalazione = onNavigateToSegnalazione,
                        onUpdateStato = { /**/ },
                        onEpisodeClick = { /**/ },
                        viewModel = schedaContenutoPersonaleViewModel,
                        onCatalog = true,
                        onRecensioneClick = { /**/ }
                    )
                }
                is ContenutoDettaglio.NotFound -> {
                    Text("Contenuto non trovato", color = Color.White, fontSize = 20.sp, modifier = Modifier.align(Alignment.Center))
                }
                is ContenutoDettaglio.Error -> {
                    Text("Errore nel caricamento del contenuto", color = Color.Red, fontSize = 20.sp, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}