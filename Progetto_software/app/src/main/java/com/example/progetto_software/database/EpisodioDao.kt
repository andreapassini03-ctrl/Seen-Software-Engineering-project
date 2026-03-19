package com.example.progetto_software.database

import androidx.room.*
import com.example.progetto_software.data.*
import com.example.progetto_software.database.EpisodioConRecensioni // Import the relation class
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodio(episodio: Episodio)

    @Update
    suspend fun updateEpisodio(episodio: Episodio)

    @Delete
    suspend fun deleteEpisodio(episodio: Episodio)

    @Query("UPDATE episodi SET valutazione_media = :newRating WHERE id = :episodioId")
    suspend fun updateMediaRating(episodioId: String, newRating: Float)

    @Query("SELECT * FROM episodi WHERE id = :episodioId")
    suspend fun getEpisodioById(episodioId: String): Episodio?

    @Query("SELECT * FROM episodi WHERE id_serie_di_appartenenza = :serieTvId")
    fun getEpisodiBySerieTvId(serieTvId: String): Flow<List<Episodio>>

    @Query("SELECT * FROM episodi ORDER BY id_serie_di_appartenenza ASC")
    fun getAllEpisodi(): Flow<List<Episodio>>

    // Recupera un Episodio con tutte le sue recensioni associate.
    @Transaction
    @Query("SELECT * FROM episodi WHERE id = :episodioId")
    fun getEpisodioConRecensioni(episodioId: String): Flow<EpisodioConRecensioni?>

}