// File: com/example/progetto_software/components/ContentCard.kt
package com.example.progetto_software.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progetto_software.database.ImageResourceUtil // Assicurati che il percorso sia corretto

@Composable
fun ContentPosterCard(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = ImageResourceUtil.getDrawableResIdByName(context, imageUrl)

    Card(
        modifier = modifier
            .width(120.dp) // Larghezza della card
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    // contentDescription = contentName, // Rimosso: non è più necessario
                    contentDescription = null, // Set to null if you don't need a description for accessibility
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth() // L'immagine riempie la larghezza della Card
                        .background(Color.DarkGray)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp) // Altezza di default per il placeholder
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No image",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
            // Spacer e Text(contentName) sono stati rimossi da qui
        }
    }
}