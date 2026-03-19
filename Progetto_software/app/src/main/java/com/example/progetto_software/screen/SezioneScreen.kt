package com.example.progetto_software.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progetto_software.AppScreen
import com.example.progetto_software.R
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progetto_software.controller.SezioneViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.progetto_software.components.ContentPosterCard
import com.example.progetto_software.components.MyBottomNavBar
import com.example.progetto_software.data.Contenuto
import com.example.progetto_software.data.Contenuto_utente
import com.example.progetto_software.data.Film
import com.example.progetto_software.data.SerieTv
import com.example.progetto_software.database.AppDatabase // Import AppDatabase
import com.example.progetto_software.database.MyApplicationClass // Import MyApplicationClass
import com.example.progetto_software.controller.UtenteViewModel
import com.example.progetto_software.controller.UtenteViewModelFactory
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SezioneScreen(
    title: String, // "Film" or "Serie TV"
    onBackClick: () -> Unit,
    onCatalogoClick: () -> Unit,
    onAreaPersonaleClick: () -> Unit,
    onContentCardClick: (Contenuto) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as MyApplicationClass
    val database = AppDatabase.getDatabase(context)

    val contenutoUtenteDao = database.contenutoUtenteDao()
    val utenteDao = database.utenteDao()

    val serieTvDao = database.serieTvDao()
    val filmDao = database.filmDao()

    val sezioneViewModel: SezioneViewModel = viewModel(
        factory = SezioneViewModel.SezioneViewModelFactory(
            contenutoUtenteDao = contenutoUtenteDao,
            utenteDao = utenteDao,
            filmDao = filmDao,
            serieTvDao = serieTvDao,
            application = application
        )
    )

    // Collect the specific lists from the ViewModel
    val filmNonVisti by sezioneViewModel.filmNonVisti.collectAsState()
    val filmVisti by sezioneViewModel.filmVisti.collectAsState()
    val filmPreferiti by sezioneViewModel.filmPreferiti.collectAsState()

    val serieTvNonViste by sezioneViewModel.serieTvNonViste.collectAsState()
    val serieTvIniziate by sezioneViewModel.serieTvIniziate.collectAsState()
    val serieTvViste by sezioneViewModel.serieTvViste.collectAsState()
    val serieTvPreferite by sezioneViewModel.serieTvPreferite.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = Color.White) },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            when (title) {
                "Film" -> {
                    ContenutiSezione(
                        title = "NON VISTI",
                        contenuti = filmNonVisti,
                        onCardClick = { item -> item.contenutoAssociato?.let { onContentCardClick(it) } }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    ContenutiSezione(
                        title = "VISTI",
                        contenuti = filmVisti,
                        onCardClick = { item -> item.contenutoAssociato?.let { onContentCardClick(it) } }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    ContenutiSezione(
                        title = "PREFERITI",
                        contenuti = filmPreferiti,
                        onCardClick = { item -> item.contenutoAssociato?.let { onContentCardClick(it) } }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                "Serie TV" -> {
                    ContenutiSezione(
                        title = "NON VISTE", // Changed to feminine plural for Serie TV
                        contenuti = serieTvNonViste,
                        onCardClick = { item -> item.contenutoAssociato?.let { onContentCardClick(it) } }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    ContenutiSezione(
                        title = "INIZIATE", // Changed to feminine plural for Serie TV
                        contenuti = serieTvIniziate,
                        onCardClick = { item -> item.contenutoAssociato?.let { onContentCardClick(it) } }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    ContenutiSezione(
                        title = "VISTE", // Changed to feminine plural for Serie TV
                        contenuti = serieTvViste,
                        onCardClick = { item -> item.contenutoAssociato?.let { onContentCardClick(it) } }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    ContenutiSezione(
                        title = "PREFERITE", // Changed to feminine plural for Serie TV
                        contenuti = serieTvPreferite,
                        onCardClick = { item -> item.contenutoAssociato?.let { onContentCardClick(it) } }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                else -> {
                    // Handle unknown title or show a default message
                    Text("Sezione non riconosciuta", color = Color.White, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}

@Composable
fun ContenutiSezione(
    title: String,
    contenuti: List<Contenuto_utente>,
    onCardClick: (Contenuto_utente) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Yellow)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.Start)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            if (contenuti.isEmpty()) {
                item {
                    Text(
                        text = "Nessun contenuto in questa sezione.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                items(contenuti) { item ->
                    item.contenutoAssociato?.let { contenuto ->
                        ContentPosterCard(
                            imageUrl = contenuto.imageUrl,
                            modifier = Modifier.width(120.dp),
                            onClick = { onCardClick(item) }
                        )
                    }
                }
            }
        }
    }
}