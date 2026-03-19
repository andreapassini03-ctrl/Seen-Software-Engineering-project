// File: com/example/progetto_software/data/Episodio.kt
package com.example.progetto_software.data

import androidx.room.*
import java.time.LocalDateTime

@Entity(
    tableName = "episodi",
    foreignKeys = [
        ForeignKey(
            entity = SerieTv::class,
            parentColumns = ["id"], // ID della SerieTv
            childColumns = ["id_serie_di_appartenenza"], // <--- NOME COLONNA FK in Episodio
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id_serie_di_appartenenza"]) // <--- NOME COLONNA FK
    ]
)
data class Episodio(
    @PrimaryKey override val id: String,
    override var trama: String,
    override var nome: String,
    override var genere: String,
    override var eta: Int,
    @ColumnInfo(name = "valutazione_media") // <--- NOME COLONNA DB (se vuoi snake_case nel DB)
    override var valutazioneMedia: Float,    // <--- NOME CAMPO KOTLIN (camelCase)

    override var imageUrl: String?,

    @ColumnInfo(name = "durata_secondi") val durataSecondi: Long,

    // Questo è il CAMPO FK effettivo che Room persisterà nella tabella "episodi"
    @ColumnInfo(name = "id_serie_di_appartenenza") val idSerieDiAppartenenza: String // <-- CAMBIATO NOME e TIPO

) : Contenuto(id, trama, nome, genere, eta, valutazioneMedia, imageUrl) { // <-- Passa il nome corretto qui

    // Questo è il campo per l'oggetto SerieTv completo, da usare solo in-memory o con @Relation
    @Ignore
    var serieDiAppartenenza: SerieTv? = null // <-- CAMBIATO NOME

}