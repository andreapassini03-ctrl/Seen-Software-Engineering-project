
package com.example.progetto_software.database

import androidx.room.*
import com.example.progetto_software.data.*
import kotlinx.coroutines.flow.Flow

// DAO per l'entità Segnalazione
@Dao
interface SegnalazioneDao {

    // Inserisce una nuova segnalazione nel database.
    // In caso di conflitto (es. stessa chiave primaria), la sostituisce.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSegnalazione(segnalazione: Segnalazione)

    // Aggiorna una segnalazione esistente nel database.
    @Update
    suspend fun updateSegnalazione(segnalazione: Segnalazione)

    // Elimina una segnalazione dal database.
    @Delete
    suspend fun deleteSegnalazione(segnalazione: Segnalazione)

    // Recupera una segnalazione per il suo ID.
    @Query("SELECT * FROM segnalazioni WHERE id = :segnalazioneId")
    suspend fun getSegnalazioneById(segnalazioneId: Int): Segnalazione?

    // Recupera tutte le segnalazioni dal database, ordinate per timestamp decrescente.
    // Restituisce un Flow per osservare i cambiamenti in tempo reale.
    @Query("SELECT * FROM segnalazioni ORDER BY timestamp DESC")
    fun getAllSegnalazioni(): Flow<List<Segnalazione>>

    // Recupera tutte le segnalazioni fatte da un utente specifico.
    @Query("SELECT * FROM segnalazioni WHERE idUtente = :idUtente ORDER BY timestamp DESC")
    fun getSegnalazioniByUtenteId(idUtente: Int): Flow<List<Segnalazione>>

    // Recupera tutte le segnalazioni per una recensione specifica.
    @Query("SELECT * FROM segnalazioni WHERE idRecensione = :idRecensione ORDER BY timestamp DESC")
    fun getSegnalazioniByRecensioneId(idRecensione: Int): Flow<List<Segnalazione>>

    // Esempio di query con @Transaction per ottenere una segnalazione con l'utente e la recensione associata.
    /*
    @Transaction
    @Query("SELECT * FROM segnalazioni WHERE id = :segnalazioneId")
    fun getSegnalazioneConUtenteERecensione(segnalazioneId: Int): Flow<SegnalazioneConUtenteERecensione>
    */
}