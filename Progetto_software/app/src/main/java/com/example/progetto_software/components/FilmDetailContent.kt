package com.example.progetto_software.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.progetto_software.data.Contenuto
import com.example.progetto_software.data.Film
import com.example.progetto_software.data.Recensione
import com.example.progetto_software.data.Stato
import com.example.progetto_software.screen.DurationFormatter
import com.example.progetto_software.data.Episodio // Import Episodio
import com.example.progetto_software.data.SerieTv

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilmDetailContent(
    film: Contenuto?, // This can be Film, SerieTv, or Episodio
    stato: Stato?,
    reviews: List<Recensione>,
    onNavigateToSegnalazione: () -> Unit,

    onCatalog: Boolean,
    onRecensioneClick: () -> Unit,

    onUpdateStato: (Stato) -> Unit, // Callback per l'aggiornamento dello stato

    viewModel: SchedaContenutoPersonaleViewModel? = null // ViewModel opzionale, passato dal genitore
) {
    val context = LocalContext.current
    val generiList = film?.genere?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }

    var expandedStatusDropdown by remember { mutableStateOf(false) }

    // selectedStato riflette lo stato corrente passato e gestisce lo stato interno del dropdown
    var selectedStato by remember(stato) { mutableStateOf(stato) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp) // Padding orizzontale per il contenuto
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // --- Image Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            val imageResId = ImageResourceUtil.getDrawableResIdByName(context, film?.imageUrl)
            Image(
                painter = if (imageResId != 0) painterResource(id = imageResId) else painterResource(id = R.drawable.ic_image_placeholder),
                contentDescription = "Copertina ${film?.nome}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Title Section ---
        film?.nome?.let {
            Text(
                text = it,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // --- Duration/Type and Age Rating Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (film) {
                is Film -> {
                    Text(
                        text = "Film ${DurationFormatter.formatDuration(film.durataSecondi)}",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${film.eta}+",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                is Episodio -> { //caso episodio
                    Text(
                        text = "Episodio ${DurationFormatter.formatDuration(film.durataSecondi)}",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${film.eta}+", // Assuming Episodio also has 'eta'
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Spacer to push the dropdown to the right
            Spacer(modifier = Modifier.weight(1f))

            // --- Status Dropdown ---
            if (!onCatalog) {
                ExposedDropdownMenuBox(
                    expanded = expandedStatusDropdown,
                    onExpandedChange = { expandedStatusDropdown = !expandedStatusDropdown },
                    modifier = Modifier.width(160.dp)
                ) {
                    OutlinedTextField(
                        value = selectedStato?.name ?: "Error",
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
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 14.sp)
                    )


                    val statusOptions = remember(film, stato) { // Re-calculate when 'film' type or 'stato' changes
                        val baseOptions = Stato.entries.toMutableList()

                        // Rule 1: Always exclude INIZIATO (it's typically an internal/transient state)
                        baseOptions.remove(Stato.INIZIATO)

                        // Always exclude stato corrente
                        stato?.let { currentStato ->
                            baseOptions.remove(currentStato)
                        }

                        // Rule 2: If the content is an Episodio, exclude PREFERITO
                        if (film is Episodio) {
                            baseOptions.remove(Stato.PREFERITO)
                        }

                        // Rule 3: If the current content's status (stato) is NOT VISTO, exclude PREFERITO
                        // This applies to Films/SerieTv (Episodio is already handled by Rule 2)
                        if (stato != Stato.VISTO) {
                            baseOptions.remove(Stato.PREFERITO)
                        }

                        baseOptions.toList() // Convert back to an immutable list for safety
                    }


                    ExposedDropdownMenu(
                        expanded = expandedStatusDropdown,
                        onDismissRequest = { expandedStatusDropdown = false },
                        modifier = Modifier.background(Color(0xFF2C2C2C))
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
                                        onUpdateStato(statusOption)
                                        //selectedStato = statusOption
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
        Spacer(modifier = Modifier.height(16.dp))

        // --- Genres Section ---
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 4
        ) {
            generiList?.forEach { genre ->
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

        // --- Plot Section ---
        Text(
            text = "Trama",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        film?.trama?.let {
            Text(
                text = it,
                color = Color.LightGray,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // --- Star Rating Section ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val roundedRating = ((film?.valutazioneMedia?.times(2))?.toInt() ?: 0) / 2.0f

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
                text = String.format("%.1f/5", film?.valutazioneMedia),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recensioni
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, // Distribuisce lo spazio tra gli elementi
            verticalAlignment = Alignment.CenterVertically // Allinea verticalmente al centro
        ) {
            Text(
                text = "Recensioni",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                // Modifier per il testo non ha bisogno di fillMaxWidth qui, la Row lo gestisce
            )

            //stato visto o preferito
            if(!onCatalog && (selectedStato == Stato.VISTO || selectedStato == Stato.PREFERITO)){
                // Questo è l'elemento che vuoi mettere a destra.
                // Ho usato un'icona di esempio, ma puoi sostituirla con qualsiasi Composable tu voglia.
                Icon(
                    imageVector = Icons.Default.Edit, // Esempio: un'icona di modifica
                    contentDescription = "Modifica recensioni",
                    tint = Color.Yellow, // Colore giallo come nell'immagine che hai fornito
                    modifier = Modifier
                        .size(32.dp) // Dimensione dell'icona
                        .clip(RoundedCornerShape(50)) // Rendi l'icona rotonda
                        .background(Color.Yellow.copy(alpha = 0.2f)) // Sfondo leggermente trasparente
                        .clickable { /* Azione al click, es. aggiungere la recensione */
                            onRecensioneClick() // Vado ad aggiungere la recensione
                        }
                        .padding(4.dp) // Padding interno all'icona
                )
            }

        }
        Spacer(modifier = Modifier.height(16.dp))

        if (reviews.isEmpty()) {
            Text(
                text = "Nessuna recensione disponibile.",
                color = Color.LightGray,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        } else {
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