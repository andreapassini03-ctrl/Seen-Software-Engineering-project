
// file: com.example.progetto_software.viewmodel/LoginViewModelFactory.kt
package com.example.progetto_software.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.progetto_software.database.UtenteDao

class LoginViewModelFactory(private val utenteDao: UtenteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(utenteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}