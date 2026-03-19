// file: com.example.progetto_software.database/ModeratoreDao.kt
package com.example.progetto_software.database

import androidx.room.*
import com.example.progetto_software.data.Moderatore
import kotlinx.coroutines.flow.Flow

@Dao
interface ModeratoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModeratore(moderatore: Moderatore)

    @Update
    suspend fun updateModeratore(moderatore: Moderatore)

    @Delete
    suspend fun deleteModeratore(moderatore: Moderatore)

    @Query("SELECT * FROM moderatori WHERE id = :id")
    fun getModeratoreById(id: Int): Flow<Moderatore>

    @Query("SELECT * FROM moderatori WHERE username = :username LIMIT 1")
    suspend fun getModeratoreByUsername(username: String): Moderatore?

    @Query("SELECT * FROM moderatori")
    fun getAllModeratori(): Flow<List<Moderatore>>


}