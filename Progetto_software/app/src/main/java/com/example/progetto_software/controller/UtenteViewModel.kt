// file: com.example.progetto_software.viewmodel/UtenteViewModel.kt
package com.example.progetto_software.controller // Cambiato da .Controller a .viewmodel per coerenza

import android.app.Application // Importa Application
import android.content.Context // Importa Context per SharedPreferences
import androidx.lifecycle.AndroidViewModel // Cambiato da ViewModel a AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.progetto_software.database.UtenteDao
import com.example.progetto_software.data.Utente // Assicurati che Utente sia importato
import com.example.progetto_software.database.MyApplicationClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Cambiato da ViewModel a AndroidViewModel e aggiunto il parametro 'application'
class UtenteViewModel(
    private val utenteDao: UtenteDao,
    application: Application // <--- AGGIUNTO QUESTO PARAMETRO
) : AndroidViewModel(application) { // <--- ESTENDE AndroidViewModel E PASSA 'application'

    // Per la registrazione
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState


    private val myApplication = application as MyApplicationClass
    // Espone l'utente loggato da MyApplicationClass
    val loggedInUser: StateFlow<Utente?> = myApplication.loggedInUser

    // Inizializza il ViewModel
    init {
        // Tenta di caricare l'utente all'avvio del ViewModel
        loadLoggedInUserFromPreferences()
    }

    // Metodo per recuperare l'utente dal database in base all'username (esempio)
    fun loadUserByUsername(username: String) {
        viewModelScope.launch {
            val user = utenteDao.getUtenteByUsername(username)
            // Imposta l'utente loggato sia qui che in MyApplicationClass
            myApplication.setLoggedInUser(user)
            if (user != null) {
                saveUserToPreferences(user.username) // Salva l'username se trovato
            } else {
                removeUserFromPreferences() // Rimuovi se l'utente non è più valido
            }
        }
    }

    private fun loadLoggedInUserFromPreferences() {
        viewModelScope.launch {
            val sharedPrefs = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val savedUsername = sharedPrefs.getString("logged_in_username", null)
            if (savedUsername != null) {
                val user = utenteDao.getUtenteByUsername(savedUsername)
                myApplication.setLoggedInUser(user) // Imposta l'utente loggato in MyApplicationClass
            }
        }
    }

    // Nuovo metodo per salvare l'utente in SharedPreferences
    private fun saveUserToPreferences(username: String) {
        // Usa getApplication<Application>() per ottenere il contesto dell'applicazione
        val sharedPrefs = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString("logged_in_username", username)
            apply()
        }
    }

    private fun removeUserFromPreferences() {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            remove("logged_in_username")
            apply()
        }
    }

    fun logoutUser() {
        myApplication.setLoggedInUser(null) // Rimuovi l'utente da MyApplicationClass
        removeUserFromPreferences() // Rimuovi da SharedPreferences
    }


    fun registerUtente(utente: Utente) {
        _registrationState.value = RegistrationState.Loading // Indica che l'operazione è in corso

        viewModelScope.launch {
            try {
                // 1. Controlla se l'username esiste già
                val existingUtente = utenteDao.getUtenteByUsername(utente.username)
                if (existingUtente != null) {
                    _registrationState.value =
                        RegistrationState.Error("Username già esistente. Per favore, scegli un altro username.")
                    return@launch // Esci dalla coroutine
                }

                // 2. Hashing della password (ESSENZIALE PER LA SICUREZZA)
                // In una vera app, NON salvare la password in chiaro.
                // Dovresti usare una libreria per fare l'hashing (es. BCrypt).
                // Per esempio, potresti avere una funzione come 'val hashedPassword = hashPassword(utente.hashPassword)'
                // E poi passare 'hashedPassword' al posto di 'utente.hashPassword'
                val utenteToInsert =
                    utente.copy(hashPassword = utente.hashPassword) // Assicurati che il campo sia passwordHash, non hashPassword

                // 3. Inserisci l'utente nel database
                utenteDao.insertUtente(utenteToInsert)
                _registrationState.value = RegistrationState.Success
            } catch (e: Exception) {
                _registrationState.value =
                    RegistrationState.Error("Errore durante la registrazione: ${e.localizedMessage ?: "Sconosciuto"}")
            }
        }
    }

    fun resetRegistrationState() {
        _registrationState.value = RegistrationState.Idle
    }
}

// Spostato RegistrationState all'interno dello stesso file o in un file separato nel package viewmodel
sealed class RegistrationState {
    object Idle : RegistrationState() // Stato iniziale o dopo un'operazione
    object Loading : RegistrationState() // Registrazione in corso
    object Success : RegistrationState() // Registrazione riuscita
    data class Error(val message: String) : RegistrationState() // Errore durante la registrazione
}