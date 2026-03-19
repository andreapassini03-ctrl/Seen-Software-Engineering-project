// File: com/example/progetto_software/data/ContenutoCatalogo.kt
package com.example.progetto_software.data

import androidx.room.Ignore

abstract class ContenutoCatalogo(
    id: String,
    trama: String,
    nome: String,
    genere: String,
    eta: Int,
    valutazioneMedia: Float,
    imageUrl: String?,
    recensioni: MutableList<Recensione> = mutableListOf()
) :
    Contenuto(id, trama, nome, genere, eta, valutazioneMedia, imageUrl, recensioni) {
    //Relazione tra Contenuto_utente e ContenutoCatalogo
    // --> si può aggiungere un contenuto solo se è una Serie Tv o Film
    // è qua che implemento la lista di Contenuto_utente
    // Chiama il costruttore della classe astratta Contenuto

    @Ignore
    val contenuti_utente: List<Contenuto_utente> = ArrayList()
}