// File: com/example/progetto_software/data/Contenuto.kt
package com.example.progetto_software.data

import androidx.room.Ignore
import androidx.room.PrimaryKey

// Contenuto è una classe astratta base per Film e SerieTv.
// Non sarà un'Entity diretta, ma le sue proprietà saranno ereditate.
abstract class Contenuto(
    @PrimaryKey open val id: String, // Ogni contenuto ha un ID unico
    open var trama: String,
    open var nome: String,
    open var genere: String,
    open var eta: Int,
    open var valutazioneMedia: Float,
    open var imageUrl: String?, // URL o percorso del file dell'immagine (copertina)

    // Keep this as open var MutableList if you intend to modify it directly
    @Ignore open var recensioni: MutableList<Recensione> = mutableListOf()
) {

    // RIMOSSO: La funzione getRecensioni() esplicita è stata rimossa.
    // Puoi accedere alla lista direttamente tramite 'recensioni'.
    // Se vuoi un getter che restituisca una List immutabile, puoi farlo così:
    // fun getRecensioniReadOnly(): List<Recensione> = recensioni

    fun aggiungiRecensione(recensione: Recensione) {
        // ATTENZIONE: Questo aggiunge solo alla lista in-memory.
        // Per salvarla nel database, dovrai usare il RecensioneDao.
        aggiornaValutazioneMedia(recensione.valutazione)
        recensioni.add(recensione)
    }

    private fun aggiornaValutazioneMedia(valutazione: Int) {
        // Evita divisione per zero se non ci sono ancora recensioni
        if (recensioni.isEmpty()) {
            this.valutazioneMedia = valutazione.toFloat()
        } else {
            this.valutazioneMedia =
                (this.valutazioneMedia * recensioni.size + valutazione) / (recensioni.size + 1)
        }
    }
}