// file: com.example.progetto_software.viewmodel/UtenteViewModelFactory.kt
package com.example.progetto_software.controller

import android.app.Application // Importa Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.progetto_software.database.UtenteDao

class UtenteViewModelFactory(
    private val utenteDao: UtenteDao,
    private val application: Application // <--- AGGIUNGI QUESTO PARAMETRO
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UtenteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // <--- PASSA ANCHE 'application' AL COSTRUTTORE DI UtenteViewModel
            return UtenteViewModel(utenteDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}