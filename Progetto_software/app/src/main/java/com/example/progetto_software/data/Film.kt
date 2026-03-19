// File: com/example/progetto_software/data/Film.kt
package com.example.progetto_software.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "film")
data class Film(
    @PrimaryKey override val id: String,
    override var trama: String,
    override var nome: String,
    override var genere: String,
    override var eta: Int,
    @ColumnInfo(name = "valutazione_media")
    override var valutazioneMedia: Float,
    override var imageUrl: String?,
    @ColumnInfo(name = "durata_secondi") // <--- NOME DELLA COLONNA NEL DB
    val durataSecondi: Long // <--- TIPO CAMBIATO A LONG (rappresenterà i secondi)
) : ContenutoCatalogo(id, trama, nome, genere, eta, valutazioneMedia, imageUrl, mutableListOf()) { // CAMBIATO QUI: passato mutableListOf()
    // ... (il resto della classe, assicurati che non ci siano riferimenti a 'durata' che siano ancora LocalDateTime)
}