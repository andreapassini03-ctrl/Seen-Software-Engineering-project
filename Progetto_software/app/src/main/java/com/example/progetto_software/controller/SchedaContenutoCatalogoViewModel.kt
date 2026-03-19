// File: com/example/progetto_software/controller/SchedaContenutoCatalogoViewModel.kt
package com.example.progetto_software.controller

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.progetto_software.data.Film
import com.example.progetto_software.data.SerieTv
import com.example.progetto_software.data.Recensione
import com.example.progetto_software.data.Contenuto_utente // Importa Contenuto_utente
import com.example.progetto_software.data.Stato
import com.example.progetto_software.database.FilmDao
import com.example.progetto_software.database.SerieTvDao
import com.example.progetto_software.database.RecensioneDao
import com.example.progetto_software.database.ContenutoUtenteDao // Importa ContenutoUtenteDao

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date // Per la data di aggiunta in Contenuto_utente

// Sealed class per gestire i diversi stati del dettaglio del contenuto (Existing)
sealed class ContenutoDettaglio {
    object Loading : ContenutoDettaglio()
    data class FilmDetail(val film: Film) : ContenutoDettaglio()
    data class SerieTvDetail(val serieTv: SerieTv) : ContenutoDettaglio()
    object NotFound : ContenutoDettaglio()
    data class Error(val message: String) : ContenutoDettaglio()
}

class SchedaContenutoCatalogoViewModel(
    private val filmDao: FilmDao,
    private val serieTvDao: SerieTvDao,
    private val recensioneDao: RecensioneDao,
    private val contenutoUtenteDao: ContenutoUtenteDao
) : ViewModel() {

    private val _contenuto = MutableStateFlow<ContenutoDettaglio>(ContenutoDettaglio.Loading)
    val contenuto: StateFlow<ContenutoDettaglio> = _contenuto.asStateFlow()

    private val _reviews = MutableStateFlow<List<Recensione>>(emptyList())
    val reviews: StateFlow<List<Recensione>> = _reviews.asStateFlow()

    private val _contenutoUtenteState = MutableStateFlow<Contenuto_utente?>(null)
    val contenutoUtenteState: StateFlow<Contenuto_utente?> = _contenutoUtenteState.asStateFlow()


    private var currentContentId: String? = null
    private var currentContentType: String? = null
    private var currentLoggedInUserId: String? = null

    fun loadContenuto(contentId: String, contentType: String, loggedInUserId: String?) {
        _contenuto.value = ContenutoDettaglio.Loading
        this.currentContentId = contentId
        this.currentContentType = contentType
        this.currentLoggedInUserId = loggedInUserId

        viewModelScope.launch {
            try {
                val fetchedContent = when (contentType) {
                    "film" -> filmDao.getFilmById(contentId)
                    "serieTv" -> serieTvDao.getSerieTvById(contentId)
                    else -> null
                }

                if (fetchedContent != null) {
                    _contenuto.value = when (fetchedContent) {
                        is Film -> ContenutoDettaglio.FilmDetail(fetchedContent)
                        is SerieTv -> ContenutoDettaglio.SerieTvDetail(fetchedContent)
                        else -> ContenutoDettaglio.NotFound
                    }

                    when (contentType) {
                        "film" -> loadReviewsForFilm(contentId)
                        "serieTv" -> loadReviewsForSerieTv(contentId)
                    }

                    loggedInUserId?.let { userId ->
                        val userContent = contenutoUtenteDao.getContenutoUtenteByContenutoAndUtente(userId, contentId)
                        _contenutoUtenteState.value = userContent
                    } ?: run {
                        _contenutoUtenteState.value = null
                    }

                } else {
                    _contenuto.value = ContenutoDettaglio.NotFound
                    _contenutoUtenteState.value = null
                }
            } catch (e: Exception) {
                _contenuto.value = ContenutoDettaglio.Error("Errore nel caricamento: ${e.message}")
                _contenutoUtenteState.value = null
            }
        }
    }

    private fun loadReviewsForFilm(filmId: String) {
        viewModelScope.launch {
            recensioneDao.getRecensioniByFilmId(filmId).collect { reviewsList ->
                _reviews.value = reviewsList
            }
        }
    }

    private fun loadReviewsForSerieTv(serieTvId: String) {
        viewModelScope.launch {
            recensioneDao.getRecensioniBySerieTvId(serieTvId).collect { reviewsList ->
                _reviews.value = reviewsList
            }
        }
    }

    private fun loadReviewsForEpisodio(episodioId: String) {
        viewModelScope.launch {
            recensioneDao.getRecensioniByEpisodioId(episodioId).collect { reviewsList ->
                _reviews.value = reviewsList
            }
        }
    }

    // NUOVO: Funzione per aggiungere/rimuovere il contenuto dalla lista dell'utente (CORRETTA)
    fun insertContenutoInUserList() {
        val contentId = currentContentId ?: return
        val userId = currentLoggedInUserId ?: return
        val contentType = currentContentType ?: return // Necessario per distinguere Film/SerieTv

        viewModelScope.launch {
            // 1. Recupera l'elemento esistente dal database
            val existingContenutoUtente = contenutoUtenteDao
                .getContenutoUtenteByContenutoAndUtente(contentId, userId)

            if (existingContenutoUtente == null) {
                // 2. Se NON esiste nel database, inseriscilo
                val newContenutoUtente = Contenuto_utente(
                    idUtente = userId,
                    // Imposta l'ID del film o della serie TV in base al tipo di contenuto
                    idFilm = if (contentType == "Film") contentId else null,
                    idSerieTv = if (contentType == "SerieTv") contentId else null,
                    idEpisodio = null,
                    stato = Stato.NON_VISTO.name // Usa il nome dell'enum come Stringa per il DB
                )
                contenutoUtenteDao.insertContenutoUtente(newContenutoUtente)
                // Lo stato _contenutoUtenteState verrà aggiornato automaticamente
                // grazie alla collectLatest in loadContenuto
            } else {
                // 3. Se ESISTE nel database, eliminalo (questo gestisce la parte "rimuovi")
                contenutoUtenteDao.deleteContenutoUtente(existingContenutoUtente)
                // Lo stato _contenutoUtenteState verrà aggiornato automaticamente
            }
        }
    }


    class SchedaContenutoCatalogoViewModelFactory(
        private val filmDao: FilmDao,
        private val serieTvDao: SerieTvDao,
        private val recensioneDao: RecensioneDao,
        private val contenutoUtenteDao: ContenutoUtenteDao
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Line 171 in your file is likely around this area:
            if (modelClass.isAssignableFrom(SchedaContenutoCatalogoViewModel::class.java)) { // <--- Check this line
                return SchedaContenutoCatalogoViewModel(
                    filmDao = filmDao,
                    serieTvDao = serieTvDao,
                    recensioneDao = recensioneDao,
                    contenutoUtenteDao = contenutoUtenteDao
                ) as T
            }
            // This is the line that throws the exception if the above condition is false:
            throw IllegalArgumentException("Unknown ViewModel class") // <--- This is the source of your crash
        }
    }



}


