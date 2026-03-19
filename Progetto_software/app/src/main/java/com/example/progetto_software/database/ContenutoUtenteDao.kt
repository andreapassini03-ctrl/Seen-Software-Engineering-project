package com.example.progetto_software.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.progetto_software.data.Contenuto_utente
import com.example.progetto_software.data.SerieTv // Import SerieTv to check type
import com.example.progetto_software.data.Episodio // Import Episodio
import com.example.progetto_software.data.Stato // Import Stato enum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull // Needed to get a snapshot from Flow

@Dao
interface ContenutoUtenteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContenutoUtente(contenutoUtente: Contenuto_utente)

    @Update
    suspend fun updateContenutoUtente(contenutoUtente: Contenuto_utente)

    @Delete
    suspend fun deleteContenutoUtente(contenutoUtente: Contenuto_utente)

    // --- Pure SELECT queries ---
    @Query("SELECT * FROM contenuti_utente WHERE idUtente = :idUtente ORDER BY cuId DESC")
    fun getContenutiUtentePerUtente(idUtente: String): Flow<List<Contenuto_utente>>

    @Query("SELECT * FROM contenuti_utente WHERE idUtente = :idUtente AND stato = :stato ORDER BY cuId DESC")
    fun getContenutiUtentePerStato(idUtente: String, stato: String): Flow<List<Contenuto_utente>>

    @Query("SELECT * FROM contenuti_utente WHERE idUtente = :idUtente AND stato = :stato AND idFilm IS NOT NULL ORDER BY cuId DESC")
    fun getFilmUtentePerStato(idUtente: String, stato: String): Flow<List<Contenuto_utente>>

    @Query("SELECT * FROM contenuti_utente WHERE idUtente = :idUtente AND stato = :stato AND idSerieTv IS NOT NULL ORDER BY cuId DESC")
    fun getSerieTvUtentePerStato(idUtente: String, stato: String): Flow<List<Contenuto_utente>>

    @Query("SELECT * FROM contenuti_utente WHERE idUtente = :idUtente AND ( (:isFilm AND idFilm IS NOT NULL) OR (:isSerieTv AND idSerieTv IS NOT NULL) OR (:isEpisodio AND idEpisodio IS NOT NULL) ) ORDER BY cuId DESC")
    fun getContenutiUtentePerTipo(idUtente: String, isFilm: Boolean, isSerieTv: Boolean, isEpisodio: Boolean): Flow<List<Contenuto_utente>>


    @Query("SELECT * FROM contenuti_utente WHERE idUtente = :idUtente AND stato = :stato AND ( (:isFilm AND idFilm IS NOT NULL) OR (:isSerieTv AND idSerieTv IS NOT NULL) OR (:isEpisodio AND idEpisodio IS NOT NULL) ) ORDER BY cuId DESC")
    fun getContenutiUtentePerTipoEStato(idUtente: String, stato: String, isFilm: Boolean, isSerieTv: Boolean, isEpisodio: Boolean): Flow<List<Contenuto_utente>>


    // This method is used internally by the upsert for Films/Series.
    @Query("SELECT * FROM contenuti_utente WHERE idUtente = :idUtente AND (idFilm = :idContenuto OR idSerieTv = :idContenuto OR idEpisodio = :idContenuto) LIMIT 1")
    suspend fun getContenutoUtenteByContenutoAndUtente(idUtente: String, idContenuto: String): Contenuto_utente?

    // This new method specifically for episodes.
    @Query("SELECT * FROM contenuti_utente WHERE idUtente = :idUtente AND idEpisodio = :idEpisodio LIMIT 1")
    suspend fun getContenutoUtenteByEpisodioAndUtente(idUtente: String, idEpisodio: String): Contenuto_utente?

    // --- NEW: Atomic upsert method (uses @Transaction) ---
    @Transaction // This ensures the read and write operations are atomic
    suspend fun upsertContenutoUtente(
        contenutoUtente: Contenuto_utente,
        episodioDao: EpisodioDao // <--- NEW: Pass EpisodioDao to fetch episodes
    ): Contenuto_utente? {
        val contentIdentifier = contenutoUtente.idFilm ?: contenutoUtente.idSerieTv ?: contenutoUtente.idEpisodio ?: ""
        val userId = contenutoUtente.idUtente

        // Use the single, updated lookup method for all content types
        val existingContent = getContenutoUtenteByContenutoAndUtente(userId, contentIdentifier)

        val resultContenutoUtente: Contenuto_utente? = if (existingContent == null) {
            insertContenutoUtente(contenutoUtente)
            // After insertion, re-fetch to ensure you get the full object, especially if auto-generated ID is used
            getContenutoUtenteByContenutoAndUtente(userId, contentIdentifier)
        } else {
            existingContent.stato = contenutoUtente.stato
            updateContenutoUtente(existingContent)
            existingContent // Return the updated object
        }

        // --- NEW LOGIC: Handle Episodes for SerieTv (only if the main content is a SerieTv) ---
        // Make sure 'contenutoUtente.contenutoAssociato' is correctly set in ViewModel
        if (contenutoUtente.idSerieTv != null && contenutoUtente.contenutoAssociato is SerieTv) {
            val serieTvId = contenutoUtente.idSerieTv
            // Fetch all episodes for this SerieTv
            val episodes: List<Episodio> = episodioDao.getEpisodiBySerieTvId(serieTvId).firstOrNull() ?: emptyList()

            for (episode in episodes) {
                // Create a Contenuto_utente for each episode, mirroring the SerieTv's state
                val episodeContenutoUtente = Contenuto_utente(
                    stato = contenutoUtente.stato, // Use the same status as the parent SerieTv
                    idUtente = userId,
                    idFilm = null,
                    idSerieTv = null, // Ensure these are null for episode entries
                    idEpisodio = episode.id
                )

                // Use the general lookup, which now handles episodes
                val existingEpisodeContent = getContenutoUtenteByContenutoAndUtente(userId, episode.id)

                if (existingEpisodeContent == null) {
                    // If no existing entry, insert it
                    insertContenutoUtente(episodeContenutoUtente)
                } else {
                    // If an entry exists, update its state if different
                    if (existingEpisodeContent.stato != episodeContenutoUtente.stato) {
                        existingEpisodeContent.stato = episodeContenutoUtente.stato
                        updateContenutoUtente(existingEpisodeContent)
                    }
                    // If the state is the same, no action needed.
                }
            }
        }
        // --- END NEW LOGIC ---

        return resultContenutoUtente
    }
}