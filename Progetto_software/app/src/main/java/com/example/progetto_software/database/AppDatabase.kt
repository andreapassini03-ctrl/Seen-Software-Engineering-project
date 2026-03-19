package com.example.progetto_software.database

import android.app.Application
import android.content.Context

import androidx.room.*
import com.example.progetto_software.data.*

@Database(
    entities = [
        Utente::class,
        Contenuto_utente::class,
        Film::class,
        SerieTv::class,
        Episodio::class,
        Recensione::class,
        Segnalazione::class,
        Moderatore::class // --- NEW: Add Moderatore entity ---
    ],
    version = 5,
    exportSchema = false
)
// L'annotazione @TypeConverters include solo i convertitori che ti servono.
@TypeConverters(DateConverter::class, LocalDateTimeConverter::class,
    StatoConverter::class, SezioneConverter::class)

abstract class AppDatabase : RoomDatabase() {

    abstract fun utenteDao(): UtenteDao
    abstract fun moderatoreDao(): ModeratoreDao
    abstract fun recensioneDao(): RecensioneDao
    abstract fun segnalazioneDao(): SegnalazioneDao
    abstract fun serieTvDao(): SerieTvDao
    abstract fun filmDao(): FilmDao
    abstract fun episodioDao(): EpisodioDao
    abstract fun contenutoUtenteDao(): ContenutoUtenteDao

    // Ricorda di aggiungere i DAO per le altre entità man mano che li crei

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "your_database_name" // <--- Assicurati che il nome del database sia corretto
                )
                    .fallbackToDestructiveMigration() // <--- AGGIUNGI QUESTA RIGA QUI!
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
