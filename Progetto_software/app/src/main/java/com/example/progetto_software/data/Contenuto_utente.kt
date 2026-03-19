package com.example.progetto_software.data

import androidx.room.*

@Entity(
    tableName = "contenuti_utente",
    foreignKeys = [
        ForeignKey(
            entity = Utente::class,
            parentColumns = ["id"],
            childColumns = ["idUtente"],
            onDelete = ForeignKey.CASCADE // Cancella Contenuto_utente se l'utente viene eliminato
        ),
        ForeignKey(
            entity = Film::class, // Assumiamo Film sia una @Entity
            parentColumns = ["id"],
            childColumns = ["idFilm"],
            onDelete = ForeignKey.SET_NULL, // Imposta a NULL se il Film viene eliminato
            deferred = true // Permette di avere relazioni nulle temporaneamente
        ),
        ForeignKey(
            entity = SerieTv::class, // La tua entità SerieTv
            parentColumns = ["id"],
            childColumns = ["idSerieTv"],
            onDelete = ForeignKey.SET_NULL, // Imposta a NULL se la SerieTv viene eliminata
            deferred = true
        ),
        ForeignKey(
            entity = Episodio::class, // <-- NEW: Foreign Key for Episodio
            parentColumns = ["id"],
            childColumns = ["idEpisodio"],
            onDelete = ForeignKey.SET_NULL, // Imposta a NULL se l'Episodio viene eliminato
            deferred = true
        )
    ],
    indices = [
        // Indici per garantire l'unicità e migliorare le query di join
        Index(value = ["idUtente", "idFilm"], unique = true), // Utente-Film è unico
        Index(value = ["idUtente", "idSerieTv"], unique = true), // Utente-SerieTV è unico
        Index(value = ["idUtente", "idEpisodio"], unique = true) // <-- NEW: Utente-Episodio è unico
    ]
)
data class Contenuto_utente(
    @PrimaryKey(autoGenerate = true) val cuId: Int = 0, // Primary Key per Contenuto_utente
    var stato: String,
    @ColumnInfo(name = "idUtente") val idUtente: String,
    @ColumnInfo(name = "idFilm") val idFilm: String?, // Può essere null
    @ColumnInfo(name = "idSerieTv") val idSerieTv: String?, // Può essere null
    @ColumnInfo(name = "idEpisodio") val idEpisodio: String? // <-- NEW: Può essere null se è un Film o SerieTv
) {
    @Ignore
    var utenteAssociato: Utente? = null
    @Ignore
    var contenutoAssociato: Contenuto? = null // Will be Film, SerieTv, or Episodio

    constructor(
        stato: Stato,
        utente: Utente,
        contenuto: Contenuto // Can be Film, SerieTv, or Episode
    ) : this(
        stato = stato.name,
        idUtente = utente.id,
        idFilm = if (contenuto is Film) contenuto.id else null,
        idSerieTv = if (contenuto is SerieTv) contenuto.id else null,
        idEpisodio = if (contenuto is Episodio) contenuto.id else null // <-- NEW: Set idEpisodio
    ) {
        this.utenteAssociato = utente
        this.contenutoAssociato = contenuto
    }

    fun modificaStato(newStato: Stato) {
        this.stato = newStato.name
    }

    @Ignore
    fun rimuovi() {
        println("Contenuto utente rimosso. Use DAO to persist changes.")
    }

    val statoEnum: Stato
        get() = Stato.valueOf(stato)
}