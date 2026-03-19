package com.example.progetto_software.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progetto_software.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.example.progetto_software.controller.SchedaContenutoPersonaleViewModel
import com.example.progetto_software.data.Episodio
import com.example.progetto_software.data.Recensione
import com.example.progetto_software.data.SerieTv
import com.example.progetto_software.data.Stato
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Import this!

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SerieTvDetailContent(
    serieTv: SerieTv,
    reviews: List<Recensione>,
    // stato: Stato?, // Rimuovi questo parametro, lo otterremo dal ViewModel
    episodi: List<Episodio>?, // episodes can now be null
    onNavigateToSegnalazione: () -> Unit,
    onCatalog: Boolean,
    onRecensioneClick: () -> Unit,
    onUpdateStato: (Stato) -> Unit, // Callback for updating status of the SerieTv itself
    onEpisodeClick: (Episodio) -> Unit, // Callback for episode clicks
    viewModel: SchedaContenutoPersonaleViewModel // ViewModel is now REQUIRED, not optional
) {
    val context = LocalContext.current
    val generiList = serieTv.genere.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    // --- NUOVE LINEE: OSSERVA LO STATO DELLA SERIE TV DAL VIEWMODEL ---
    // Questo è lo stato effettivo della Serie TV, che viene aggiornato dal ViewModel.
    val contenutoUtenteState by viewModel.contenutoUtenteState.collectAsStateWithLifecycle()
    // Prendi lo stato della Serie TV, se esiste, altrimenti NON_VISTO come default.
    val statoCorrenteSerieTv = contenutoUtenteState?.statoEnum ?: Stato.NON_VISTO

    // Osserva gli stati dei singoli episodi dal ViewModel per la visualizzazione nella lista episodi
    val episodiStati by viewModel.getEpisodiStati().collectAsStateWithLifecycle()


    // State for managing episode list expansion
    var episodesExpanded by remember { mutableStateOf(false) }

    // State for managing dropdown menu for status (for the SerieTv itself)
    var expandedStatusDropdown by remember { mutableStateOf(false) }


    Log.d("SerieTvDetailContent", "Composing SerieTvDetailContent for: ${serieTv.nome}")
    Log.d("SerieTvDetailContent", "Stato SerieTv corrente dal VM: ${statoCorrenteSerieTv.name}") // Logga il nuovo stato
    Log.d("SerieTvDetailContent", "Episodi ricevuti: ${episodi?.size ?: 0} episodi")
    Log.d("SerieTvDetailContent", "Recensioni ricevute: ${reviews.size} recensioni")
    Log.d("SerieTvDetailContent", "EpisodiStati dal VM: ${episodiStati.size} stati") // Log per gli stati degli episodi

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // Spazio per la TopAppBar (assuming it's handled by Scaffold or parent)
        Spacer(modifier = Modifier.height(16.dp))

        // Sezione Immagine Contenuto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            val imageResId = ImageResourceUtil.getDrawableResIdByName(context, serieTv.imageUrl)
            Image(
                painter = if (imageResId != 0) painterResource(id = imageResId) else painterResource(id = R.color.black),
                contentDescription = "Copertina ${serieTv.nome}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = serieTv.nome,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Stato dropdown per la Serie TV
        // Usa `statoCorrenteSerieTv` per determinare se mostrarlo o meno
        if (!onCatalog) { // Non mostrare il dropdown se è in catalogo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Serie TV",
                    color = Color.LightGray,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${serieTv.eta}+",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // ExposedDropdownMenuBox per lo stato della Serie TV
                ExposedDropdownMenuBox(
                    expanded = expandedStatusDropdown,
                    onExpandedChange = { expandedStatusDropdown = !expandedStatusDropdown },
                    modifier = Modifier.width(160.dp)
                ) {
                    OutlinedTextField(
                        value = statoCorrenteSerieTv.name, // <-- USA `statoCorrenteSerieTv` QUI
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatusDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2C2C2C),
                            unfocusedContainerColor = Color(0xFF2C2C2C),
                            disabledContainerColor = Color(0xFF2C2C2C),
                            focusedIndicatorColor = Color.Yellow,
                            unfocusedIndicatorColor = Color.Gray,
                            focusedTextColor = Color.Yellow,
                            unfocusedTextColor = Color.Yellow,
                            focusedTrailingIconColor = Color.Yellow,
                            unfocusedTrailingIconColor = Color.Gray,
                            cursorColor = Color.Yellow
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    )

                    // Calcola le opzioni disponibili per la Serie TV
                    val statusOptions = remember(statoCorrenteSerieTv) { // <-- Ricalcola quando `statoCorrenteSerieTv` cambia
                        val baseOptions = Stato.entries.toMutableList()

                        // Sempre escludere lo stato corrente
                        baseOptions.remove(statoCorrenteSerieTv)

                        // Una Serie TV può essere PREFERITO solo se è VISTA (o INIZIATA e poi VISTA)
                        // Se lo stato attuale non è VISTO, non possiamo impostare su PREFERITO
                        if (statoCorrenteSerieTv != Stato.VISTO) {
                            baseOptions.remove(Stato.PREFERITO)
                        }

                        baseOptions.toList()
                    }

                    ExposedDropdownMenu(
                        expanded = expandedStatusDropdown,
                        onDismissRequest = { expandedStatusDropdown = false },
                        modifier = Modifier
                            .background(Color(0xFF2C2C2C))
                    ) {
                        statusOptions
                            .forEach { statusOption ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = statusOption.name,
                                            fontSize = 14.sp
                                        )
                                    },
                                    onClick = {
                                        // Chiama il callback per aggiornare lo stato della Serie TV nel ViewModel
                                        onUpdateStato(statusOption)
                                        expandedStatusDropdown = false
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = Color.Yellow
                                    )
                                )
                            }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Generi a "chip"
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 4
        ) {
            generiList.forEach { genre ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = genre,
                        color = Color.Yellow,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Trama
        Text(
            text = "Trama",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = serieTv.trama,
            color = Color.LightGray,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Valutazione a stelle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val roundedRating = (serieTv.valutazioneMedia * 2).toInt() / 2.0f

            repeat(5) { index ->
                val starResource = when {
                    roundedRating >= index + 1 -> R.drawable.ic_star_filled
                    roundedRating >= index + 0.5 -> R.drawable.ic_star_half
                    else -> R.drawable.ic_star_empty
                }
                val starTint = when (starResource) {
                    R.drawable.ic_star_filled -> Color.Yellow
                    R.drawable.ic_star_half -> Color.Yellow
                    R.drawable.ic_star_empty -> Color.Gray
                    else -> Color.Gray
                }

                Icon(
                    painter = painterResource(id = starResource),
                    contentDescription = "Star",
                    tint = starTint,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format("%.1f/5", serieTv.valutazioneMedia),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Episodi Section - Conditionally visible and expandable
        if (!episodi.isNullOrEmpty()) {
            Log.d("SerieTvDetailContent", "Rendering Episodes Section. Episodes count: ${episodi.size}")
            Column(modifier = Modifier.animateContentSize(animationSpec = tween(durationMillis = 300))) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { episodesExpanded = !episodesExpanded }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Episodi",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (episodesExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight,
                        contentDescription = if (episodesExpanded) "Comprimi episodi" else "Espandi episodi",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (episodesExpanded) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp) // Max height for the scrollable episode list
                    ) {
                        items(episodi) { episode ->
                            // Passa lo stato dell'episodio da episodiStati (se presente, altrimenti NON_VISTO)
                            val episodeStato = episodiStati[episode.id] ?: Stato.NON_VISTO
                            EpisodeItem(
                                episode = episode,
                                currentStato = episodeStato, // Passa lo stato dell'episodio
                                onEpisodeClick = onEpisodeClick,
                                // Passa il ViewModel per EpisodeItem se ha bisogno di chiamare modificaStato
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Log.d("SerieTvDetailContent", "Episodes list is null or empty. Not rendering Episodes Section.")
        }

        // Recensioni
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recensioni",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )

            // Mostra l'icona di modifica recensione solo se la Serie TV è VISTA o PREFERITA
            if(!onCatalog && (statoCorrenteSerieTv == Stato.VISTO || statoCorrenteSerieTv == Stato.PREFERITO)){ // <-- USA `statoCorrenteSerieTv` QUI
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifica recensioni",
                    tint = Color.Yellow,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Yellow.copy(alpha = 0.2f))
                        .clickable {
                            onRecensioneClick()
                        }
                        .padding(4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (reviews.isEmpty()) {
            Log.d("SerieTvDetailContent", "No reviews available.")
            Text(
                text = "Nessuna recensione disponibile.",
                color = Color.LightGray,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        } else {
            Log.d("SerieTvDetailContent", "Rendering ${reviews.size} reviews.")
            Column {
                reviews.forEach { rec ->
                    ReviewCard(review = rec, onFlagClick = onNavigateToSegnalazione)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Helper Composable for each episode item
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeItem(
    episode: Episodio,
    currentStato: Stato, // NUOVO: Stato dell'episodio corrente
    onEpisodeClick: (Episodio) -> Unit,
    viewModel: SchedaContenutoPersonaleViewModel // ViewModel è necessario per l'episodio
) {
    Log.d("EpisodeItem", "Composing Episode: ${episode.nome}, Stato: ${currentStato.name}")

    // Stato per il menu a tendina dell'episodio
    var expandedEpisodeStatusDropdown by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Parte cliccabile dell'episodio (nome)
        Row(
            modifier = Modifier
                .weight(1f) // Occupa lo spazio rimanente prima della dropdown
                .clickable { onEpisodeClick(episode) }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${episode.nome}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}