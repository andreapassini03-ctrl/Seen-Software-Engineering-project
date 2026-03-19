// file: com.example.progetto_software.data/Segnalazione.kt
package com.example.progetto_software.data

import androidx.room.*
import java.time.LocalDateTime

@Entity(
    tableName = "segnalazioni",
    foreignKeys = [
        ForeignKey(
            entity = Utente::class,
            parentColumns = ["id"],
            childColumns = ["idUtente"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Recensione::class,
            parentColumns = ["id"],
            childColumns = ["idRecensione"],
            onDelete = ForeignKey.CASCADE
        )
        // --- RIMOZIONE: ForeignKey a Moderatore non è più necessaria qui ---
    ],
    indices = [
        Index(value = ["idUtente"]),
        Index(value = ["idRecensione"])
        // --- RIMOZIONE: Index per idModeratore non è più necessario ---
    ]
)
data class Segnalazione(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "timestamp") val timestamp: LocalDateTime,
    val motivazione: String,
    @ColumnInfo(name = "idUtente") val idUtente: Int,
    @ColumnInfo(name = "idRecensione") val idRecensione: Int
    // --- RIMOZIONE: idModeratore non è più una colonna di Segnalazione ---
) {
    @Ignore
    var utenteAssociato: Utente? = null
    @Ignore
    var recensioneAssociata: Recensione? = null
    // --- RIMOZIONE: moderatoreAssegnato non è più necessario ---

    // Costruttore di convenienza aggiornato
    constructor(
        timestamp: LocalDateTime,
        motivazione: String,
        utente: Utente,
        recensione: Recensione
        // --- RIMOZIONE: moderatore come parametro ---
    ) : this(
        timestamp = timestamp,
        motivazione = motivazione,
        idUtente = utente.id.toInt(),
        idRecensione = recensione.id
    ) {
        this.utenteAssociato = utente
        this.recensioneAssociata = recensione
    }
}