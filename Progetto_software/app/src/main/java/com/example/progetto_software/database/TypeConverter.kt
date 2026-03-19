package com.example.progetto_software.database

import android.os.Build
import androidx.annotation.RequiresApi

import androidx.room.*

import com.example.progetto_software.data.*

import java.time.* // Questo import è ancora utile se usi LocalDateTime nel tuo codice anche senza TypeConverter
import java.util.*


// --- TypeConverter per LocalDateTime ---
class LocalDateTimeConverter {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it / 1000, 0, ZoneOffset.UTC) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneOffset.UTC)?.toEpochSecond()?.times(1000)
    }
}

// --- NUOVO: TypeConverter per java.util.Date (per Recensione) ---
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}

// --- TypeConverter per Stato ---
class StatoConverter {
    @TypeConverter
    fun fromStato(stato: Stato): String {
        return stato.name
    }

    @TypeConverter
    fun toStato(name: String): Stato {
        return Stato.valueOf(name)
    }
}

// --- TypeConverter per Sezione ---
class SezioneConverter {
    @TypeConverter
    fun fromSezione(sezione: Sezione): String {
        return sezione.name
    }

    @TypeConverter
    fun toSezione(name: String): Sezione {
        return Sezione.valueOf(name)
    }
}

