// In RecensioneDao.kt
package com.example.progetto_software.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction // Keep this import
import com.example.progetto_software.data.Recensione
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first // Needed for .first() call in the new method

@Dao
interface RecensioneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // **IMPORTANT**: Ensure no @Transaction here. Room handles it for single inserts.
    suspend fun insertRecensione(recensione: Recensione)

    @Query("SELECT * FROM recensioni WHERE idFilm = :idContenuto")
    fun getRecensioniByFilmId(idContenuto: String): Flow<List<Recensione>>

    @Query("SELECT * FROM recensioni WHERE idSerieTv = :idContenuto")
    fun getRecensioniBySerieTvId(idContenuto: String): Flow<List<Recensione>>

    @Query("SELECT * FROM recensioni WHERE idEpisodio = :idContenuto")
    fun getRecensioniByEpisodioId(idContenuto: String): Flow<List<Recensione>>

    // General method to get reviews for any content type
    @Query("SELECT * FROM recensioni WHERE idFilm = :idContent OR idSerieTv = :idContent OR idEpisodio = :idContent")
    fun getReviewsByContent(idContent: String): Flow<List<Recensione>>

    // --- NEW: Atomic method to insert review and update content rating ---
    @Transaction // <-- This is the crucial annotation
    suspend fun insertReviewAndUpdateRating(
        recensione: Recensione,
        filmDao: FilmDao,
        serieTvDao: SerieTvDao,
        episodioDao: EpisodioDao
    ) {
        insertRecensione(recensione) // Insert the review

        // Logic to update the average rating for the associated content
        val contentId = recensione.idFilm ?: recensione.idSerieTv ?: recensione.idEpisodio
        if (contentId != null) {
            val averageRating = getAverageRatingForContent(contentId) // You need a query for this

            when {
                recensione.idFilm != null -> {
                    val film = filmDao.getFilmById(contentId)
                    film?.let {
                        if (averageRating != null) {
                            it.valutazioneMedia = averageRating.toFloat()
                        }
                        filmDao.updateFilm(it)
                    }
                }

                recensione.idSerieTv != null -> {
                    val serieTv = serieTvDao.getSerieTvById(contentId)
                    serieTv?.let {
                        if (averageRating != null) {
                            it.valutazioneMedia = averageRating.toFloat()
                        }
                        serieTvDao.updateSerieTv(it)
                    }
                }

                recensione.idEpisodio != null -> {
                    val episodio = episodioDao.getEpisodioById(contentId)
                    episodio?.let {
                        if (averageRating != null) {
                            it.valutazioneMedia = averageRating.toFloat()
                        }
                        episodioDao.updateEpisodio(it)
                    }
                }
            }
        }
    }

    // You'll need a query like this in your RecensioneDao
    @Query("SELECT AVG(valutazione) FROM recensioni WHERE idFilm = :contentId OR idSerieTv = :contentId OR idEpisodio = :contentId")
    suspend fun getAverageRatingForContent(contentId: String): Float?

}