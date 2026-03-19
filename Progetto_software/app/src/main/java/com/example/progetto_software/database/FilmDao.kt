package com.example.progetto_software.database

import androidx.room.*
import com.example.progetto_software.data.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilm(film: Film)

    @Update
    suspend fun updateFilm(film: Film)

    @Delete
    suspend fun deleteFilm(film: Film)

    @Query("UPDATE film SET valutazione_media = :newRating WHERE id = :idContent")
    suspend fun updateMediaRating(idContent: String, newRating: Float) // <-- Changed to Double

    @Query("SELECT * FROM film WHERE id = :filmId")
    suspend fun getFilmById(filmId: String): Film?

    @Query("SELECT * FROM film ORDER BY nome ASC")
    fun getAllFilms(): Flow<List<Film>>

    // Recupera un Film con tutte le sue recensioni associate.
    @Transaction
    @Query("SELECT * FROM film WHERE id = :filmId")
    fun getFilmConRecensioni(filmId: String): Flow<FilmConRecensioni?>

    // Recupera tutti i Film con le loro recensioni associate.
    @Transaction
    @Query("SELECT * FROM film ORDER BY nome ASC")
    fun getAllFilmsConRecensioni(): Flow<List<FilmConRecensioni>>
}