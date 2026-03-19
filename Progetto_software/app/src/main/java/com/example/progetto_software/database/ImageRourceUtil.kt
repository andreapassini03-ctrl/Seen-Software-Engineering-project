package com.example.progetto_software.database

// Esempio di funzione helper (puoi metterla dove è più comodo, es. in un Util.kt o in un ViewModel)
import android.content.Context
import androidx.annotation.DrawableRes

object ImageResourceUtil {
    @DrawableRes
    fun getDrawableResIdByName(context: Context, resName: String?): Int {
        if (resName.isNullOrEmpty()) {
            return 0 // O un ID di una drawable placeholder/errore
        }
        return context.resources.getIdentifier(
            resName, "drawable", context.packageName
        )
    }
}