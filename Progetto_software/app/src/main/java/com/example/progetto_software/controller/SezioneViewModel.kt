package com.example.progetto_software.controller

import android.app.Application
import android.content.Context
import android.util.Log
// import androidx.compose.ui.text.toLowerCase // REMOVE THIS IMPORT - it's for Compose Text, not general String operations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.progetto_software.data.* // Import all necessary data classes
import com.example.progetto_software.database.ContenutoUtenteDao
import com.example.progetto_software.database.UtenteDao
import com.example.progetto_software.database.FilmDao
import com.example.progetto_software.database.SerieTvDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.progetto_software.data.Contenuto_utente
import com.example.progetto_software.data.Film
import com.example.progetto_software.data.SerieTv
import com.example.progetto_software.database.MyApplicationClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import com.example.progetto_software.data.Utente

class SezioneViewModel(
    private val contenutoUtenteDao: ContenutoUtenteDao,
    private val utenteDao: UtenteDao,
    private val filmDao: FilmDao,
    private val serieTvDao: SerieTvDao,
    private val application: Application
) : ViewModel() {

    // Internal MutableStateFlow to hold all user content, WITH associated content
    private val _contenutiUtente = MutableStateFlow<List<Contenuto_utente>>(emptyList())

    // Expose filtered lists for Films
    private val _filmNonVisti = MutableStateFlow<List<Contenuto_utente>>(emptyList())
    val filmNonVisti: StateFlow<List<Contenuto_utente>> = _filmNonVisti.asStateFlow()

    private val _filmVisti = MutableStateFlow<List<Contenuto_utente>>(emptyList())
    val filmVisti: StateFlow<List<Contenuto_utente>> = _filmVisti.asStateFlow()

    private val _filmPreferiti = MutableStateFlow<List<Contenuto_utente>>(emptyList())
    val filmPreferiti: StateFlow<List<Contenuto_utente>> = _filmPreferiti.asStateFlow()

    // Expose filtered lists for SerieTv
    private val _serieTvNonViste = MutableStateFlow<List<Contenuto_utente>>(emptyList())
    val serieTvNonViste: StateFlow<List<Contenuto_utente>> = _serieTvNonViste.asStateFlow()

    private val _serieTvIniziate = MutableStateFlow<List<Contenuto_utente>>(emptyList())
    val serieTvIniziate: StateFlow<List<Contenuto_utente>> = _serieTvIniziate.asStateFlow()

    private val _serieTvViste = MutableStateFlow<List<Contenuto_utente>>(emptyList())
    val serieTvViste: StateFlow<List<Contenuto_utente>> = _serieTvViste.asStateFlow()

    private val _serieTvPreferite = MutableStateFlow<List<Contenuto_utente>>(emptyList())
    val serieTvPreferite: StateFlow<List<Contenuto_utente>> = _serieTvPreferite.asStateFlow()

    private val myApplication = application as MyApplicationClass
    // Espone l'utente loggato da MyApplicationClass
    val loggedInUser: StateFlow<Utente?> = myApplication.loggedInUser

    init {
        // 1. Tenta di caricare l'utente dalle preferenze all'avvio del ViewModel
        // Questo aggiornerà myApplication.loggedInUser (e di conseguenza loggedInUser qui)
        loadLoggedInUserFromPreferences()

        // 2. Osserva il loggedInUser StateFlow e reagisci quando cambia
        viewModelScope.launch {
            loggedInUser.collectLatest { user ->
                if (user != null) {
                    Log.d("SezioneViewModel", "Logged in user detected: ${user.id}. Loading content.")
                    // Usa l'ID dell'utente effettivo
                    getPopulatedContenutiUtente(user.id).collectLatest { populatedList ->
                        Log.d("SezioneViewModel", "Data loaded into _contenutiUtente. New size: ${populatedList.size}")
                        _contenutiUtente.value = populatedList
                    }
                } else {
                    Log.e("SezioneViewModel", "No logged in user found or user logged out. Clearing content.")
                    _contenutiUtente.value = emptyList() // Clear content if no user is logged in
                    // Anche le liste filtrate dovrebbero essere svuotate
                    _filmNonVisti.value = emptyList()
                    _filmVisti.value = emptyList()
                    _filmPreferiti.value = emptyList()
                    _serieTvNonViste.value = emptyList()
                    _serieTvIniziate.value = emptyList()
                    _serieTvViste.value = emptyList()
                    _serieTvPreferite.value = emptyList()
                }
            }
        }

        // 3. Observe changes in _contenutiUtente and update filtered lists
        // Questo blocco rimane come prima, reagirà quando _contenutiUtente.value viene aggiornato
        viewModelScope.launch {
            _contenutiUtente.collectLatest { allUserContent ->
                Log.d("SezioneViewModel", "Second collector received data. allUserContent size: ${allUserContent.size}")

                _filmNonVisti.value = allUserContent.filter { cu ->
                    cu.contenutoAssociato is Film && try {
                        Stato.valueOf(cu.stato) == Stato.NON_VISTO
                    } catch (e: IllegalArgumentException) {
                        Log.e("SezioneViewModel", "Mismatch Stato for Film NON_VISTO. DB value: '${cu.stato}'. Error: ${e.message}")
                        false
                    }
                }
                Log.d("SezioneViewModel", "Film Non Visti count: ${_filmNonVisti.value.size}")


                _filmVisti.value = allUserContent.filter { cu ->
                    cu.contenutoAssociato is Film && try {
                        Stato.valueOf(cu.stato) == Stato.VISTO
                    } catch (e: IllegalArgumentException) {
                        Log.e("SezioneViewModel", "Mismatch Stato for Film VISTO. DB value: '${cu.stato}'. Error: ${e.message}")
                        false
                    }
                }
                Log.d("SezioneViewModel", "Film Visti count: ${_filmVisti.value.size}")


                _filmPreferiti.value = allUserContent.filter { cu ->
                    cu.contenutoAssociato is Film && try {
                        Stato.valueOf(cu.stato) == Stato.PREFERITO
                    } catch (e: IllegalArgumentException) {
                        Log.e("SezioneViewModel", "Mismatch Stato for Film PREFERITO. DB value: '${cu.stato}'. Error: ${e.message}")
                        false
                    }
                }
                Log.d("SezioneViewModel", "Film Preferiti count: ${_filmPreferiti.value.size}")


                _serieTvNonViste.value = allUserContent.filter { cu ->
                    cu.contenutoAssociato is SerieTv && try {
                        Stato.valueOf(cu.stato) == Stato.NON_VISTO
                    } catch (e: IllegalArgumentException) {
                        Log.e("SezioneViewModel", "Mismatch Stato for SerieTv NON_VISTO. DB value: '${cu.stato}'. Error: ${e.message}")
                        false
                    }
                }
                Log.d("SezioneViewModel", "SerieTv Non Viste count: ${_serieTvNonViste.value.size}")


                _serieTvIniziate.value = allUserContent.filter { cu ->
                    cu.contenutoAssociato is SerieTv && try {
                        Stato.valueOf(cu.stato) == Stato.INIZIATO
                    } catch (e: IllegalArgumentException) {
                        Log.e("SezioneViewModel", "Mismatch Stato for SerieTv INIZIATO. DB value: '${cu.stato}'. Error: ${e.message}")
                        false
                    }
                }
                Log.d("SezioneViewModel", "SerieTv Iniziate count: ${_serieTvIniziate.value.size}")


                _serieTvViste.value = allUserContent.filter { cu ->
                    cu.contenutoAssociato is SerieTv && try {
                        Stato.valueOf(cu.stato) == Stato.VISTO
                    } catch (e: IllegalArgumentException) {
                        Log.e("SezioneViewModel", "Mismatch Stato for SerieTv VISTO. DB value: '${cu.stato}'. Error: ${e.message}")
                        false
                    }
                }
                Log.d("SezioneViewModel", "SerieTv Viste count: ${_serieTvViste.value.size}")


                _serieTvPreferite.value = allUserContent.filter { cu ->
                    cu.contenutoAssociato is SerieTv && try {
                        Stato.valueOf(cu.stato) == Stato.PREFERITO
                    } catch (e: IllegalArgumentException) {
                        Log.e("SezioneViewModel", "Mismatch Stato for SerieTv PREFERITO. DB value: '${cu.stato}'. Error: ${e.message}")
                        false
                    }
                }
                Log.d("SezioneViewModel", "SerieTv Preferite count: ${_serieTvPreferite.value.size}")
            }
        }
    }

    /**
     * Retrieves all Contenuto_utente for a given user,
     * with their associated Film or SerieTv content populated.
     */
    private fun getPopulatedContenutiUtente(userId: String): Flow<List<Contenuto_utente>> {
        return contenutoUtenteDao.getContenutiUtentePerUtente(userId).map { userContentList ->
            Log.d("SezioneViewModel", "Raw user content list size: ${userContentList.size}")
            userContentList.map { cu ->
                val mutableCu = cu.copy()
                when {
                    mutableCu.idFilm != null -> {
                        val film = filmDao.getFilmById(mutableCu.idFilm)
                        if (film == null) {
                            Log.e(
                                "SezioneViewModel",
                                "Film with ID ${mutableCu.idFilm} not found for Contenuto_utente "
                            )
                        }
                        mutableCu.contenutoAssociato = film
                    }

                    mutableCu.idSerieTv != null -> {
                        val serieTv = serieTvDao.getSerieTvById(mutableCu.idSerieTv)
                        if (serieTv == null) {
                            Log.e(
                                "SezioneViewModel",
                                "SerieTv with ID ${mutableCu.idSerieTv} not found for Contenuto_utente "
                            )
                        }
                        mutableCu.contenutoAssociato = serieTv
                    }
                }
                mutableCu
            }
        }
    }

    private fun loadLoggedInUserFromPreferences() {
        viewModelScope.launch {
            // FIX: Use the 'application' property directly
            val sharedPrefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val savedUsername = sharedPrefs.getString("logged_in_username", null)
            if (savedUsername != null) {
                val user = utenteDao.getUtenteByUsername(savedUsername)
                myApplication.setLoggedInUser(user) // Imposta l'utente loggato in MyApplicationClass
            }
        }
    }

    // Factory for SezioneViewModel
    class SezioneViewModelFactory(
        private val contenutoUtenteDao: ContenutoUtenteDao,
        private val utenteDao: UtenteDao,
        private val filmDao: FilmDao,
        private val serieTvDao: SerieTvDao,
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SezioneViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SezioneViewModel(
                    contenutoUtenteDao,
                    utenteDao,
                    filmDao,
                    serieTvDao,
                    application
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}