package com.example.progetto_software.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progetto_software.AppScreen
import com.example.progetto_software.controller.UtenteViewModel
import com.example.progetto_software.controller.UtenteViewModelFactory
import com.example.progetto_software.R
import com.example.progetto_software.database.MyApplicationClass
import com.example.progetto_software.components.MyBottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtenteScreen(
    onCatalogoClick: () -> Unit,
    onAreaPersonaleClick: () -> Unit,
    onSezioneTvClick: (String) -> Unit, // <--- Change this to accept a String
    utenteViewModel: UtenteViewModel
) {
    val utente by utenteViewModel.loggedInUser.collectAsState()

    Scaffold(
        bottomBar = {
            MyBottomNavBar(
                selectedRoute = AppScreen.Utente.route, // Evidenzia "Area Personale"
                onCatalogoClick = onCatalogoClick,
                onAreaPersonaleClick = onAreaPersonaleClick
            )
        },
        containerColor = Color.Black // Sfondo nero
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .background(Color.Black)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            Spacer(modifier = Modifier.height(40.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_avatar), // Placeholder per l'immagine profilo
                    contentDescription = "Immagine Profilo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(60.dp)), // Immagine circolare
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = utente?.username ?: "username",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            ProfileMenuItem(
                text = "Serie TV",
                onClick = { onSezioneTvClick("Serie TV") }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.DarkGray
            )
            ProfileMenuItem(
                text = "Film",
                onClick = { onSezioneTvClick("Film") }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.DarkGray
            )

        }
    }
}

@Composable
fun ProfileMenuItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Arrow Right",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
