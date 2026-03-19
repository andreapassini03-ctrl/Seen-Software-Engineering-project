package com.example.progetto_software.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.* // Rimuovi se non usi Instant/LocalDateTime direttamente in Room Entity

import androidx.room.*

@Entity(tableName = "utenti")
data class Utente(
    @PrimaryKey val id: String = "",
    var nome: String,
    var cognome: String,
    var username: String,
    var email: String,
    @ColumnInfo(name = "hash_password") val hashPassword: String = "",
    // Per Sezione: Salva come String. Puoi usare un TypeConverter se vuoi salvarla come Int
    var sezione: String // Salva come String per Room (es. "FILM", "SERIE_TV")
) {
    // Le liste come segnalazioni e contenutiUtente NON sono direttamente mappabili in Room
    // e vanno gestite tramite relazioni (Foreign Keys e @Relation nei DAO)
    @Ignore // Dici a Room di ignorare questa proprietà
    private val segnalazioni: MutableList<Segnalazione> = ArrayList()
    @Ignore // Dici a Room di ignorare questa proprietà
    private val contenutiUtente: MutableList<Contenuto_utente> = ArrayList()

    // I metodi che interagiscono con le liste IGNORE o con altri oggetti non Room
    // Questi metodi possono rimanere, ma la loro logica dovrà recuperare dati
    // dal database usando i DAO.

    fun getContenutiUtente(): List<Contenuto_utente> {
        // Questa logica dovrà recuperare i contenuti dal database tramite il DAO
        return emptyList() // Placeholder
    }

    @Ignore // SegnalaRecensione è una logica di dominio, non mappata direttamente a Room
    @RequiresApi(Build.VERSION_CODES.O)
    fun SegnalaRecensione(recensione: Recensione, motivazione: String) {
        // La logica di creazione di Segnalazione va nel DAO o Repository
        segnalazioni.add(Segnalazione(LocalDateTime.now(), motivazione, this, recensione))
    }

    @Ignore
    fun rimuoviContenuto(myContenuto: Contenuto_utente?) {
        // Questa logica deve usare il DAO per eliminare dal DB
        if (myContenuto != null) {
            contenutiUtente.remove(myContenuto)
        }
    }

    @Ignore
    fun inserisciAdAreaPersonale(contenutoDaAggiungere: ContenutoCatalogo?) {
        // Questa logica deve usare il DAO per inserire nel DB
        if (contenutoDaAggiungere != null) {
            // Qui dovrai creare un Contenuto_utente con gli ID corretti e inserirlo via DAO
            // contenutiUtente.add(Contenuto_utente(Stato.NON_VISTO, this, contenutoDaAggiungere))
        }
    }

    // ... (altri metodi di dominio)

    // Getter per Sezione se vuoi riavere l'enum
    val sezioneEnum: Sezione
        get() = Sezione.valueOf(sezione)


}