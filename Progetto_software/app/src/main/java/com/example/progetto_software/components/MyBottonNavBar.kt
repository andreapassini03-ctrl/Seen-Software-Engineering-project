package com.example.progetto_software.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progetto_software.AppScreen

@Composable
fun MyBottomNavBar(
    selectedRoute: String, // La rotta attualmente selezionata
    onCatalogoClick: () -> Unit,
    onAreaPersonaleClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.Black,
        contentColor = Color.White
    ) {
        // Catalogo Item
        NavigationBarItem(
            selected = selectedRoute == AppScreen.Catalogo.route, // Controlla se la rotta corrente è Catalogo
            onClick = onCatalogoClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Catalogo",
                    tint = if (selectedRoute == AppScreen.Catalogo.route) Color(0xFFFFA500) else Color.White // Colore arancione se selezionato, altrimenti bianco
                )
            },
            label = {
                Text(
                    text = "Catalogo",
                    color = if (selectedRoute == AppScreen.Catalogo.route) Color(0xFFFFA500) else Color.White, // Colore arancione se selezionato, altrimenti bianco
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFA500), // Colore arancione quando selezionato
                selectedTextColor = Color(0xFFFFA500), // Colore arancione quando selezionato
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White,
                indicatorColor = Color.Transparent // Nessun indicatore di sfondo
            )
        )

        // Area Personale Item
        NavigationBarItem(
            selected = selectedRoute == AppScreen.Utente.route, // Controlla se la rotta corrente è Area Personale (Utente)
            onClick = onAreaPersonaleClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Area Personale",
                    tint = if (selectedRoute == AppScreen.Utente.route) Color(0xFFFFA500) else Color.White // Colore arancione se selezionato, altrimenti bianco
                )
            },
            label = {
                Text(
                    text = "Area Personale",
                    color = if (selectedRoute == AppScreen.Utente.route) Color(0xFFFFA500) else Color.White, // Colore arancione se selezionato, altrimenti bianco
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFA500),
                selectedTextColor = Color(0xFFFFA500),
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White,
                indicatorColor = Color.Transparent
            )
        )
    }
}