// file: com.example.progetto_software.viewmodel/LoginViewModel.kt
package com.example.progetto_software.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progetto_software.database.UtenteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val utenteDao: UtenteDao) : ViewModel() {

    // Usiamo StateFlow per comunicare lo stato del login alla UI
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun authenticate(username: String, passwordEntered: String) {
        _loginState.value = LoginState.Loading // Indica che il login è in corso

        viewModelScope.launch {
            // In una vera app, la password dovrebbe essere hashata prima del confronto
            // Qui assumiamo che tu stia confrontando la password hashata salvata con quella inserita
            val user = utenteDao.getUtenteByUsernameAndPassword(username, passwordEntered)

            if (user != null) {
                _loginState.value = LoginState.Success(user.username)
            } else {
                _loginState.value = LoginState.Error("Username o password non validi.")
            }
        }
    }

    // Resetta lo stato del login per tentativi futuri o dopo la navigazione
    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }
}

// Definisci gli stati possibili per il login
sealed class LoginState {
    object Idle : LoginState() // Stato iniziale
    object Loading : LoginState() // Login in corso
    data class Success(val username: String) : LoginState() // Login riuscito, passiamo l'username
    data class Error(val message: String) : LoginState() // Login fallito, con messaggio di errore
}

