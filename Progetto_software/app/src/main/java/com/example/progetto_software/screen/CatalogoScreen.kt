// File: com/example/progetto_software/screen/CatalogoScreen.kt
package com.example.progetto_software.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progetto_software.AppScreen
import com.example.progetto_software.components.ContentPosterCard
import com.example.progetto_software.database.AppDatabase
import com.example.progetto_software.controller.CatalogoViewModel
import com.example.progetto_software.database.MyApplicationClass
import com.example.progetto_software.components.MyBottomNavBar
import com.example.progetto_software.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onBackClick: () -> Unit,
    onCatalogoClick: () -> Unit,
    onAreaPersonaleClick: () -> Unit,
    onContentClick: (contentId: String, contentType: String) -> Unit
) {
    val context = LocalContext.current
    val viewModel: CatalogoViewModel = viewModel(
        factory = CatalogoViewModel.CatalogoViewModelFactory(context.applicationContext as MyApplicationClass)
    )

    val films by viewModel.allFilms.collectAsState(initial = emptyList())
    val serieTv by viewModel.allSerieTv.collectAsState(initial = emptyList())

    var showFilterMenu by remember { mutableStateOf(false) }
    var showAgeFilterDialog by remember { mutableStateOf(false) }
    var selectedAgeRating by remember { mutableStateOf<Int?>(null) }

    var showRatingFilterDialog by remember { mutableStateOf(false) }
    var selectedMinRating by remember { mutableStateOf<Float?>(null) }

    var showGenreFilterDialog by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf<String?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Title is often centered, but we're creating a custom layout below */ },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = Color.White
                        )
                    }
                },
                // Add actions block for the filter icon and dropdown
                actions = {
                    Box(contentAlignment = Alignment.CenterEnd) { // Box to align dropdown
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.Yellow, shape = MaterialTheme.shapes.small)
                                .clickable { showFilterMenu = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filtra",
                                tint = Color.Black
                            )
                        }

                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false },
                            modifier = Modifier.background(Color.DarkGray)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Genere", color = Color.Yellow) },
                                onClick = {
                                    showFilterMenu = false
                                    showGenreFilterDialog = true
                                }
                            )
                            HorizontalDivider(color = Color.Gray)
                            DropdownMenuItem(
                                text = { Text("Età", color = Color.Yellow) },
                                onClick = {
                                    showFilterMenu = false
                                    showAgeFilterDialog = true
                                }
                            )
                            HorizontalDivider(color = Color.Gray)
                            DropdownMenuItem(
                                text = { Text("Valutazione", color = Color.Yellow) },
                                onClick = {
                                    showFilterMenu = false
                                    showRatingFilterDialog = true
                                }
                            )
                        }
                    }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            horizontalAlignment = Alignment.Start
        ) {
            // (Your existing content - Films, Serie TV sections - remains here)
            Spacer(modifier = Modifier.height(16.dp))

            // Sezione Film
            Text(
                text = "Film",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(films) { film ->
                    ContentPosterCard(
                        imageUrl = film.imageUrl,
                        onClick = {
                            onContentClick(film.id, "film")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sezione Serie TV
            Text(
                text = "Serie TV",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(serieTv) { serie ->
                    ContentPosterCard(
                        imageUrl = serie.imageUrl,
                        onClick = {
                            onContentClick(serie.id, "serieTv")
                        }
                    )
                }
            }
        }

        // Age Filter Dialog (remains unchanged)
        if (showAgeFilterDialog) {
            AlertDialog(
                onDismissRequest = { showAgeFilterDialog = false },
                title = { Text("Seleziona Età", color = Color.White) },
                text = {
                    Column {
                        val ageOptions = listOf(0, 7, 13, 16, 18)
                        ageOptions.forEach { age ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAgeRating = age
                                        viewModel.filterContentByAge(age)
                                        showAgeFilterDialog = false
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (selectedAgeRating == age),
                                    onClick = {
                                        selectedAgeRating = age
                                        viewModel.filterContentByAge(age)
                                        showAgeFilterDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color.Yellow)
                                )
                                Text(
                                    text = if (age == 0) "Tutti" else "$age+",
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        // Option to clear the age filter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedAgeRating = null
                                    viewModel.clearAgeFilter()
                                    showAgeFilterDialog = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedAgeRating == null),
                                onClick = {
                                    selectedAgeRating = null
                                    viewModel.clearAgeFilter()
                                    showAgeFilterDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color.Yellow)
                            )
                            Text(
                                text = "Nessun filtro età",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAgeFilterDialog = false }) {
                        Text("Annulla", color = Color.Yellow)
                    }
                },
                containerColor = Color.DarkGray,
                tonalElevation = 0.dp
            )
        }

        // Average Rating Filter Dialog (remains unchanged)
        if (showRatingFilterDialog) {
            AlertDialog(
                onDismissRequest = { showRatingFilterDialog = false },
                title = { Text("Valutazione Media Minima", color = Color.White) },
                text = {
                    Column {
                        val ratingOptions = listOf(5, 4, 3, 2)
                        ratingOptions.forEach { rating ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMinRating = rating.toFloat()
                                        viewModel.filterContentByRating(rating.toFloat())
                                        showRatingFilterDialog = false
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (selectedMinRating == rating.toFloat()),
                                    onClick = {
                                        selectedMinRating = rating.toFloat()
                                        viewModel.filterContentByRating(rating.toFloat())
                                        showRatingFilterDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color.Yellow)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(rating) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_star_filled),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    repeat(5 - rating) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_star_empty),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                        // Option to clear the rating filter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMinRating = null
                                    viewModel.clearRatingFilter()
                                    showRatingFilterDialog = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedMinRating == null),
                                onClick = {
                                    selectedMinRating = null
                                    viewModel.clearRatingFilter()
                                    showRatingFilterDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color.Yellow)
                            )
                            Text(
                                text = "Nessun filtro valutazione",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showRatingFilterDialog = false }) {
                        Text("Annulla", color = Color.Yellow)
                    }
                },
                containerColor = Color.DarkGray,
                tonalElevation = 0.dp
            )
        }

        // Genre Filter Dialog (remains unchanged)
        if (showGenreFilterDialog) {
            AlertDialog(
                onDismissRequest = { showGenreFilterDialog = false },
                title = { Text("Seleziona Genere", color = Color.White) },
                text = {
                    Column {
                        // Updated list of genre options
                        val genreOptions = listOf("Azione", "Avventura", "Thriller", "Horror", "Commedia", "Crime", "Drammatico", "Sci-Fi")
                        genreOptions.forEach { genre ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedGenre = genre
                                        viewModel.filterContentByGenre(genre)
                                        showGenreFilterDialog = false
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (selectedGenre == genre),
                                    onClick = {
                                        selectedGenre = genre
                                        viewModel.filterContentByGenre(genre)
                                        showGenreFilterDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color.Yellow)
                                )
                                Text(
                                    text = genre,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        // Option to clear the genre filter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedGenre = null
                                    viewModel.clearGenreFilter()
                                    showGenreFilterDialog = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedGenre == null),
                                onClick = {
                                    selectedGenre = null
                                    viewModel.clearGenreFilter()
                                    showGenreFilterDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color.Yellow)
                            )
                            Text(
                                text = "Nessun filtro genere",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showGenreFilterDialog = false }) {
                        Text("Annulla", color = Color.Yellow)
                    }
                },
                containerColor = Color.DarkGray,
                tonalElevation = 0.dp
            )
        }
    }
}