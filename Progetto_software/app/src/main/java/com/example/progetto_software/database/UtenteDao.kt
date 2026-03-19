// file: com.example.progetto_software.database/UtenteDao.kt
package com.example.progetto_software.database

import androidx.room.*
import com.example.progetto_software.data.* // Assicurati che Utente sia qui
import kotlinx.coroutines.flow.Flow

@Dao
interface UtenteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUtente(utente: Utente)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContenutoUtente(contenutoUtente: Contenuto_utente)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilm(film: Film)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSerieTv(serieTv: SerieTv)

    @Query("SELECT * FROM utenti")
    fun getAllUtenti(): Flow<List<Utente>>

    @Query("SELECT * FROM serie_tv")
    fun getAllSerieTv(): Flow<List<SerieTv>>

    @Transaction
    @Query("SELECT * FROM utenti WHERE id = :idUtente")
    fun getUtenteConContenutiUtente(idUtente: Int): Flow<UtenteConContenutiUtente>

    @Transaction
    @Query("SELECT * FROM contenuti_utente")
    fun getAllContenutiUtenteConContenuto(): Flow<List<ContenutoUtenteConContenuto>>

    @Transaction
    @Query("SELECT * FROM utenti WHERE id = :idUtente")
    fun getUtenteConContenutiDettagliati(idUtente: Int): Flow<UtenteConContenutiDettagliati>

    @Query("""
        SELECT s.* FROM serie_tv s
        INNER JOIN contenuti_utente cu ON s.id = cu.idSerieTv
        WHERE cu.idUtente = :idUtente
    """)
    fun getSerieTvByUtenteId(idUtente: Int): Flow<List<SerieTv>>

    @Query("""
        SELECT s.*, cu.stato FROM serie_tv s
        INNER JOIN contenuti_utente cu ON s.id = cu.idSerieTv
        WHERE cu.idUtente = :idUtente AND cu.stato = :stato
    """)
    fun getSerieTvByUtenteIdAndStato(idUtente: Int, stato: String): Flow<List<SerieTv>>

    // --- IMPLEMENTAZIONE CONCRETA ---
    @Query("SELECT * FROM utenti WHERE username = :username AND hash_password = :hash_password LIMIT 1")
    suspend fun getUtenteByUsernameAndPassword(username: String, hash_password: String): Utente?

    // AGGIUNGI ANCHE QUESTO: Per la registrazione, per controllare se un username esiste già
    @Query("SELECT * FROM utenti WHERE username = :username LIMIT 1")
    suspend fun getUtenteByUsername(username: String): Utente?
}