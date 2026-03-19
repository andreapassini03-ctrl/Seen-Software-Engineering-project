package com.example.progetto_software.data

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "recensioni",
    foreignKeys = [
        ForeignKey(
            entity = Utente::class,
            parentColumns = ["id"],
            childColumns = ["idUtente"],
            onDelete = ForeignKey.CASCADE
        ),
        // --- NUOVA CHIAVE ESTERNA PER FILM ---
        ForeignKey(
            entity = Film::class, // Ora punta a Film (concreto)
            parentColumns = ["id"],
            childColumns = ["idFilm"], // Nuovo campo in Recensione
            onDelete = ForeignKey.CASCADE,
            deferred = true // Utile per transazioni complesse o per evitare errori temporanei
        ),
        // --- NUOVA CHIAVE ESTERNA PER SERIETV ---
        ForeignKey(
            entity = SerieTv::class, // Ora punta a SerieTv (concreto)
            parentColumns = ["id"],
            childColumns = ["idSerieTv"], // Nuovo campo in Recensione
            onDelete = ForeignKey.CASCADE,
            deferred = true
        ),
        // --- AGGIUNTA CHIAVE ESTERNA PER EPISODIO ---
        ForeignKey(
            entity = Episodio::class, // Ora punta a Episodio
            parentColumns = ["id"],
            childColumns = ["idEpisodio"], // Nuovo campo in Recensione
            onDelete = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [
        // Indici per le chiavi esterne per migliorare le performance delle query
        Index(value = ["idUtente"]),
        Index(value = ["idFilm"]),
        Index(value = ["idSerieTv"]),
        Index(value = ["idEpisodio"]) // Nuovo indice per Episodio
        // Se vuoi garantire che un utente possa recensire UN SOLO Film O UNA SOLA SerieTV O UN SOLO Episodio,
        // la logica di unicità diventa più complessa e va gestita a livello di applicazione
        // o con un CHECK constraint in un RoomDatabase.Callback.
        // Ad esempio, non puoi fare Index(value = ["idUtente", "idFilm"], unique = true) E
        // Index(value = ["idUtente", "idSerieTv"], unique = true) perché entrambi possono essere null.
        // La combinazione di (idUtente, idFilm, idSerieTv, idEpisodio) potrebbe essere unica,
        // ma dovresti assicurarti che solo UNO di questi sia non-null.
    ]
)
data class Recensione(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val valutazione: Int,
    val commento: String?,
    @ColumnInfo(name = "timestamp") val timestamp: Date,
    @ColumnInfo(name = "idUtente") val idUtente: String,
    // --- NUOVI CAMPI PER REFERENZIARE IL CONTENUTO SPECIFICO (uno solo non sarà null) ---
    @ColumnInfo(name = "idFilm") val idFilm: String?, // Può essere null se la recensione è per una SerieTv o Episodio
    @ColumnInfo(name = "idSerieTv") val idSerieTv: String?, // Può essere null se la recensione è per un Film o Episodio
    @ColumnInfo(name = "idEpisodio") val idEpisodio: String? // Può essere null se la recensione è per un Film o SerieTv
) {
    // Campi @Ignore per la logica in-memory / per il caricamento tramite @Relation
    @Ignore
    val segnalazioni: MutableList<Segnalazione> = ArrayList()
    @Ignore
    var utente: Utente? = null
    @Ignore
    var contenutoAssociato: Contenuto? = null // Oggetto generico (Film, SerieTv, Episodio) caricato dal DAO

    // Costruttore di convenienza per facilitare la creazione di Recensioni in Kotlin
    // Assicurati che questo costruttore gestisca correttamente l'assegnazione degli ID
    constructor(
        valutazione: Int,
        commento: String?,
        timestamp: Date,
        utente: Utente,
        // Modifica qui per accettare Contenuto o Episodio, o un tipo più generico se necessario
        contenuto: Any // Accetta un Contenuto (Film/SerieTv) o un Episodio
    ) : this(
        valutazione = valutazione,
        commento = commento,
        timestamp = timestamp,
        idUtente = utente.id,
        // Logica per assegnare l'ID corretto e lasciare gli altri a null
        idFilm = if (contenuto is Film) contenuto.id else null,
        idSerieTv = if (contenuto is SerieTv) contenuto.id else null,
        idEpisodio = if (contenuto is Episodio) contenuto.id else null
    ) {
        this.utente = utente
        // Assegna contenutoAssociato solo se è un Contenuto
        this.contenutoAssociato = if (contenuto is Contenuto) contenuto else null

        // !!! IMPORTANTE: Aggiungi un controllo per assicurarti che SOLO UNO dei tre ID sia non-null !!!
        val nonNullIds = listOf(idFilm, idSerieTv, idEpisodio).count { it != null }
        if (nonNullIds != 1) {
            throw IllegalArgumentException("Una recensione deve essere associata ESATTAMENTE a un Film, a una SerieTv o a un Episodio, non a più di uno o a nessuno.")
        }
    }

    // Metodi di dominio
    fun aggiungiSegnalazione(segnalazione: Segnalazione) {
        segnalazioni.add(segnalazione)
    }
}