package com.example.progetto_software.database

import androidx.room.*
import com.example.progetto_software.data.*

// Questa classe serve a Room per unire i dati.
// Rappresenta un Utente e tutti i Contenuto_utente ad esso associati.
data class UtenteConContenutiUtente(
    @Embedded val utente: Utente,
    @Relation(
        parentColumn = "id", // Colonna di Utente
        entityColumn = "idUtente" // Colonna di Contenuto_utente
    )
    val contenutiUtente: List<Contenuto_utente>
)

// Classe di supporto per la query di join
// Rappresenta un Contenuto_utente con i dettagli del Film o della SerieTv associata
data class ContenutoUtenteConContenuto(
    @Embedded val contenutoUtente: Contenuto_utente,
    @Relation(
        parentColumn = "idFilm",
        entityColumn = "id"
    )
    val film: Film?, // Può essere nullo se è una serie TV
    @Relation(
        parentColumn = "idSerieTv",
        entityColumn = "id"
    )
    val serieTv: SerieTv? // Può essere nulla se è un film
) {
    // Helper per ottenere l'oggetto Contenuto (Film o SerieTv)
    val contenuto: Contenuto?
        get() = film ?: serieTv
}

// Combinando le due per ottenere Utente -> Contenuto_utente -> Contenuto
data class UtenteConContenutiDettagliati(
    @Embedded val utente: Utente,
    @Relation(
        entity = Contenuto_utente::class,
        parentColumn = "id",
        entityColumn = "idUtente"
    )
    val contenutiUtenteDettagliati: List<ContenutoUtenteConContenuto>
)

// Relazione: Film con le sue Recensioni
data class FilmConRecensioni(
    @Embedded val film: Film,
    @Relation(
        parentColumn = "id", // Colonna di Film
        entityColumn = "idFilm" // Colonna di Recensione
    )
    val recensioni: List<Recensione>
)

// Relazione: SerieTv con le sue Recensioni
data class SerieTvConRecensioni(
    @Embedded val serieTv: SerieTv,
    @Relation(
        parentColumn = "id", // Colonna di SerieTv
        entityColumn = "idSerieTv" // Colonna di Recensione
    )
    val recensioni: List<Recensione>
)

// Relazione: SerieTv con episodi
data class SerieTvConEpisodi(
    @Embedded val serieTv: SerieTv,
    @Relation(
        parentColumn = "id", // Colonna di SerieTv
        entityColumn = "id_serie_di_appartenenza"
    )
    val episodi: List<Episodio>
)

// Relazione: Episodio con le sue Recensioni
data class EpisodioConRecensioni(
    @Embedded val episodio: Episodio,
    @Relation(
        parentColumn = "id", // Colonna di Episodio
        entityColumn = "idEpisodio" // Colonna di Recensione
    )
    val recensioni: List<Recensione>
)

// Questa relazione è utile se vuoi ottenere una recensione e il suo elemento associato (Film, SerieTv, o Episodio)
// Senza dover fare join separati nel DAO.
data class RecensioneConElementoAssociato(
    @Embedded val recensione: Recensione,
    @Relation(
        parentColumn = "idFilm",
        entityColumn = "id"
    )
    val film: Film?,

    @Relation(
        parentColumn = "idSerieTv",
        entityColumn = "id"
    )
    val serieTv: SerieTv?,

    @Relation(
        parentColumn = "idEpisodio",
        entityColumn = "id"
    )
    val episodio: Episodio?
) {
    // Helper per ottenere l'elemento effettivo (Film, SerieTv, o Episodio)
    val elementoAssociato: Any?
        get() = film ?: serieTv ?: episodio
}



