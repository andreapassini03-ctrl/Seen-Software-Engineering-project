// File: com/example/progetto_software/data/SerieTv.kt
package com.example.progetto_software.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore
import androidx.room.ColumnInfo
// import com.example.progetto_software.data.Episodio // Ensure Episodio is imported if it's in this package

@Entity(tableName = "serie_tv")
data class SerieTv(
    @PrimaryKey override val id: String,
    override var trama: String,
    override var nome: String,
    override var genere: String,
    override var eta: Int,
    @ColumnInfo(name = "valutazione_media")
    override var valutazioneMedia: Float,
    override var imageUrl: String?,
) : ContenutoCatalogo(id, trama, nome, genere, eta, valutazioneMedia, imageUrl, mutableListOf()) {

    // THIS IS THE PROPERTY. Kotlin generates getEpisodi() for this.
    // You will typically load episodes via a DAO, not directly from this property
    // when using Room relations. This @Ignore property serves more as a placeholder
    // for runtime data, not for direct Room persistence.
    @Ignore
    val episodi: MutableList<Episodio> = mutableListOf() // This line declares the property


    // I metodi aggiungi/rimuovi episodi dovranno interagire con il DAO
    // per persistere i cambiamenti nel database.
    @Ignore // This method is not persisted by Room directly
    fun aggiungiEpisodi(episodiDaAggiungere: List<Episodio?>?) {
        if (episodiDaAggiungere != null) {
            for (episodio in episodiDaAggiungere) {
                if (episodio != null && !episodi.contains(episodio)) {
                    episodi.add(episodio)
                }
            }
        }
    }

    @Ignore // This method is not persisted by Room directly
    fun rimuoviEpisodi(episodiDaRimuovere: List<Episodio?>?) {
        if (episodiDaRimuovere != null) {
            for (episodio in episodiDaRimuovere) {
                if (episodio != null) {
                    episodi.remove(episodio)
                }
            }
        }
    }

    @Ignore // This method is not persisted by Room directly
    fun aggiungiEpisodio(episodio: Episodio?) {
        if (episodio != null && !episodi.contains(episodio)) {
            episodi.add(episodio)
        }
    }

    @Ignore // This method is not persisted by Room directly
    fun rimuoviEpisodio(episodio: Episodio?) {
        if (episodio != null) {
            episodi.remove(episodio)
        }
    }
}