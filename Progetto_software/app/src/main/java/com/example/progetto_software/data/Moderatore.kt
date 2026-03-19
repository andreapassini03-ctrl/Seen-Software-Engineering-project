// file: com.example.progetto_software.data/Moderatore.kt
package com.example.progetto_software.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore // Needed if you add other properties not for Room

    @Entity(tableName = "moderatori")
    data class Moderatore(
        @PrimaryKey(autoGenerate = true) val id: Int = 0, // Unique ID for each moderator
        @ColumnInfo(name = "username") val username: String, // Ensure username is not nullable

    ) {

}