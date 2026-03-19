package com.example.progetto_software.database

import android.app.Application
import android.util.Log
import com.example.progetto_software.data.Utente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyApplicationClass : Application() {
    // THIS IS THE CORRECT PLACE for the 'database' property
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this) // 'this' here correctly refers to the Application context
    }
    // You can optionally expose DAOs directly if preferred
    // val utenteDao by lazy { database.utenteDao() }

    private val _loggedInUser = MutableStateFlow<Utente?>(null)
    val loggedInUser: StateFlow<Utente?> = _loggedInUser.asStateFlow()

    // Questo metodo è essenziale per il SezioneViewModel
    fun setLoggedInUser(user: Utente?) { // Ora accetta null per il logout
        _loggedInUser.value = user
    }

}