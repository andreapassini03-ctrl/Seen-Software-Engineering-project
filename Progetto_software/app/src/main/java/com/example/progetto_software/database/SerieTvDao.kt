package com.example.progetto_software.database

import androidx.room.*
import com.example.progetto_software.data.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SerieTvDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSerieTv(serieTv: SerieTv)

    @Update
    suspend fun updateSerieTv(serieTv: SerieTv)

    @Delete
    suspend fun deleteSerieTv(serieTv: SerieTv)

    @Query("UPDATE serie_tv SET valutazione_media = :newRating WHERE id = :serieTvId")
    suspend fun updateMediaRating(serieTvId: String, newRating: Float)

    @Query("SELECT * FROM serie_tv WHERE id = :serieTvId")
    suspend fun getSerieTvById(serieTvId: String): SerieTv?

    @Query("SELECT * FROM serie_tv ORDER BY nome ASC")
    fun getAllSerieTv(): Flow<List<SerieTv>>

    // Recupera una SerieTv con tutti i suoi episodi.
    @Transaction
    @Query("SELECT * FROM serie_tv WHERE id = :serieTvId")
    fun getSerieTvConEpisodi(serieTvId: String): Flow<SerieTvConEpisodi?>

    // Recupera tutte le SerieTv con i loro episodi.
    @Transaction
    @Query("SELECT * FROM serie_tv ORDER BY nome ASC")
    fun getAllSerieTvConEpisodi(): Flow<List<SerieTvConEpisodi>>

    // Recupera una SerieTv con tutte le sue recensioni associate.
    @Transaction
    @Query("SELECT * FROM serie_tv WHERE id = :serieTvId")
    fun getSerieTvConRecensioni(serieTvId: String): Flow<SerieTvConRecensioni?>

    // Recupera tutte le SerieTv con le loro recensioni associate.
    @Transaction
    @Query("SELECT * FROM serie_tv ORDER BY nome ASC")
    fun getAllSerieTvConRecensioni(): Flow<List<SerieTvConRecensioni>>
}