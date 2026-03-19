package com.example.progetto_software.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.layout.ContentScale // Not needed here
// import androidx.compose.ui.platform.LocalContext // Not needed here
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import com.example.progetto_software.components.ImageResourceUtil // Not directly used in this composable
import com.example.progetto_software.R
import com.example.progetto_software.data.Recensione
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ReviewCard(
    review: Recensione,
    onFlagClick: () -> Unit // Pass the review object to the callback
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)), // Darker background for review cards
        shape = RoundedCornerShape(12.dp), // Added rounded corners for better appearance
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Added elevation
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // Puts content at opposite ends
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Username

                Text(
                    text = review.idUtente, // <--- Use nomeUtente directly from Recensione
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // Right side: Stars and Flag
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            // Ensure you have these drawable resources
                            painter = painterResource(
                                id = if (index < review.valutazione) R.drawable.ic_star_filled else R.drawable.ic_star_empty
                            ),
                            contentDescription = "Star",
                            tint = if (index < review.valutazione) Color.Yellow else Color.Gray.copy(alpha = 0.5f), // Dim empty stars
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { onFlagClick() }, // Pass the review object when clicking
                        modifier = Modifier.size(24.dp) // Adjust IconButton size
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_flag),
                            contentDescription = "Segnala Recensione",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp) // Keep icon size consistent
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Review Comment
            review.commento?.let {
                Text(
                    text = it, // commento is guaranteed not null if you defined it as String
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            // Optional: Add timestamp
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(review.timestamp),
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End) // Align date to the right
            )
        }
    }
}