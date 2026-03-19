package com.example.progetto_software.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.progetto_software.data.Contenuto
import com.example.progetto_software.data.Contenuto_utente
import com.example.progetto_software.data.Film
import com.example.progetto_software.data.SerieTv
import com.example.progetto_software.data.Recensione
import com.example.progetto_software.data.Stato
import com.example.progetto_software.database.FilmDao
import com.example.progetto_software.database.SerieTvDao
import com.example.progetto_software.database.RecensioneDao
import com.example.progetto_software.database.ContenutoUtenteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import android.util.Log
import com.example.progetto_software.data.Episodio
import com.example.progetto_software.database.EpisodioDao
import com.example.progetto_software.database.AppDatabase
import androidx.room.withTransaction // Import this extension function

class SchedaContenutoPersonaleViewModel(
    private val filmDao: FilmDao,
    private val serieTvDao: SerieTvDao,
    private val recensioneDao: RecensioneDao,
    private val contenutoUtenteDao: ContenutoUtenteDao,
    private val episodioDao: EpisodioDao,
    private val database: AppDatabase // <-- Ensure AppDatabase is injected
) : ViewModel() {

    private val _contenuto = MutableStateFlow<Contenuto?>(null)
    val contenuto: StateFlow<Contenuto?> = _contenuto.asStateFlow()

    private val _contenutoUtenteState = MutableStateFlow<Contenuto_utente?>(null)
    val contenutoUtenteState: StateFlow<Contenuto_utente?> = _contenutoUtenteState.asStateFlow()

    private val _reviewAddedSuccessfully = MutableStateFlow(false)
    val reviewAddedSuccessfully: StateFlow<Boolean> = _reviewAddedSuccessfully.asStateFlow()

    // Stato per gli stati degli episodi di una Serie TV, utile per il ricalcolo
    private val _episodiStati = MutableStateFlow<Map<String, Stato>>(emptyMap())

    private val _loggedInUser = MutableStateFlow("")
    val loggedInUser: StateFlow<String> = _loggedInUser.asStateFlow()

    fun getEpisodiStati(): StateFlow<Map<String, Stato>> = _episodiStati.asStateFlow()

    /**
     * Carica i dettagli di un contenuto (Film, SerieTv o Episodio) e il suo stato specifico per l'utente loggato.
     *
     * @param contentId L'ID del contenuto da caricare.
     * @param contentType Il tipo di contenuto ("film", "serieTv" o "episodio").
     * @param userId L'ID dell'utente attualmente loggato.
     */
    fun loadContent(contentId: String, contentType: String, userId: String) {
        viewModelScope.launch {
            Log.d("SchedaContenutoVM", "Attempting to load content: ID=$contentId, Type=$contentType, UserID=$userId")
            try {
                // 1. Carica il contenuto principale (Film, SerieTv o Episodio)
                val fetchedContent: Contenuto? = when (contentType) {
                    "film" -> filmDao.getFilmById(contentId)
                    "serieTv" -> serieTvDao.getSerieTvById(contentId)
                    "episodio" -> episodioDao.getEpisodioById(contentId)
                    else -> null
                }
                Log.d("SchedaContenutoVM", "Fetched content (Film/SerieTv/Episodio): $fetchedContent")

                if (fetchedContent != null) {
                    // Always re-fetch reviews and episodes to ensure the latest data
                    // (especially after adding reviews or series/episode statuses)
                    val reviews: List<Recensione> = when (fetchedContent) {
                        is Film -> recensioneDao.getRecensioniByFilmId(contentId).first()
                        is SerieTv -> recensioneDao.getRecensioniBySerieTvId(contentId).first()
                        is Episodio -> recensioneDao.getRecensioniByEpisodioId(contentId).first()
                        else -> emptyList()
                    }
                    fetchedContent.recensioni.clear()
                    fetchedContent.recensioni.addAll(reviews)
                    Log.d("SchedaContenutoVM", "Fetched reviews count: ${reviews.size}")

                    if (fetchedContent is SerieTv) {
                        val episodes = episodioDao.getEpisodiBySerieTvId(contentId).first()
                        fetchedContent.episodi.clear()
                        fetchedContent.episodi.addAll(episodes)
                        Log.d("SchedaContenutoVM", "Fetched episodes count: ${episodes.size}")

                        // Load episode states for SerieTv to populate _episodiStati
                        loadEpisodeStatesForSerieTv(fetchedContent.id, userId)
                    }

                    _contenuto.value = fetchedContent
                    Log.d("SchedaContenutoVM", "_contenuto.value updated: ${_contenuto.value?.id}")

                    // 3. Carica lo stato specifico dell'utente per questo contenuto
                    val userContent = contenutoUtenteDao.getContenutoUtenteByContenutoAndUtente(userId, contentId)

                    userContent?.contenutoAssociato = fetchedContent
                    _contenutoUtenteState.value = userContent
                    Log.d("SchedaContenutoVM", "Fetched user content state: $userContent")
                    Log.d("SchedaContenutoVM", "_contenutoUtenteState.value updated: ${_contenutoUtenteState.value?.stato}")

                } else {
                    _contenuto.value = null
                    _contenutoUtenteState.value = null
                    Log.w("SchedaContenutoVM", "Content not found for ID=$contentId, Type=$contentType")
                }
            } catch (e: Exception) {
                _contenuto.value = null
                _contenutoUtenteState.value = null
                Log.e("SchedaContenutoVM", "Error loading content for personal screen: ${e.message}", e)
            }
        }
    }


    /**
     * Salva o aggiorna un oggetto Contenuto_utente nel database e aggiorna lo stato del ViewModel.
     * Questo metodo è centrale per la persistenza dello stato utente di un contenuto.
     *
     * @param contenutoUtente L'oggetto Contenuto_utente da salvare o aggiornare.
     */
    fun saveContenutoUtente(contenutoUtente: Contenuto_utente) {
        viewModelScope.launch {
            try {
                Log.d("SchedaContenutoVM", "Attempting to upsert ContenutoUtente: $contenutoUtente")
// **FIX**: Pass episodioDao here!
                val updatedContent = contenutoUtenteDao.upsertContenutoUtente(contenutoUtente, episodioDao)
// Ensure the associated content is set to the current content in ViewModel
                updatedContent?.contenutoAssociato = _contenuto.value
                _contenutoUtenteState.value = updatedContent
                Log.d("SchedaContenutoVM", "ContenutoUtente upserted: $updatedContent")
            } catch (e: Exception) {
                Log.e("SchedaContenutoVM", "Error saving/updating Contenuto_utente: ${e.message}", e)
            }
        }
    }


    /**
     * Carica gli stati di tutti gli episodi per una data Serie TV e li memorizza localmente.
     */
    private suspend fun loadEpisodeStatesForSerieTv(serieTvId: String, userId: String) {
        // Recupera tutti gli episodi di questa Serie TV
        val episodi = episodioDao.getEpisodiBySerieTvId(serieTvId).first() // Use .first() to get the list
        val statesMap = mutableMapOf<String, Stato>()
        for (episodio in episodi) {
            val userEpisodeContent = contenutoUtenteDao.getContenutoUtenteByContenutoAndUtente(userId, episodio.id)
            statesMap[episodio.id] = userEpisodeContent?.statoEnum ?: Stato.NON_VISTO
        }
        _episodiStati.value = statesMap
        Log.d("SchedaContenutoVM", "Loaded episode states for SerieTv $serieTvId: $_episodiStati")
    }

    /**
     * Salva o aggiorna un oggetto Contenuto_utente nel database e aggiorna lo stato del ViewModel.
     * Questo metodo è centrale per la persistenza dello stato utente di un contenuto.
     *
     * @param contenutoUtente L'oggetto Contenuto_utente da salvare o aggiornare.
     */
    private suspend fun saveContenutoUtenteInternal(contenutoUtente: Contenuto_utente) {
        Log.d("SchedaContenutoVM", "Attempting to upsert ContenutoUtente internally: $contenutoUtente")
        // Use withTransaction if you need multiple DAO operations to be atomic
        database.withTransaction {
            val updatedContent = contenutoUtenteDao.upsertContenutoUtente(
                contenutoUtente, episodioDao
            )
            // Ensure the associated content is set to the current content in ViewModel
            updatedContent?.contenutoAssociato = _contenuto.value
            _contenutoUtenteState.value = updatedContent
            Log.d("SchedaContenutoVM", "ContenutoUtente upserted: $updatedContent")
        }
    }


    /**
     * Modifica lo stato di un contenuto per l'utente specificato.
     * Questo metodo crea o aggiorna un oggetto Contenuto_utente nel database.
     *
     * @param newStato Il nuovo stato da assegnare (Visto, NonVisto, Preferito).
     * @param contentId L'ID del contenuto (Film, SerieTv o Episodio) di cui si vuole modificare lo stato.
     * @param contentType Il tipo di contenuto ("film", "serieTv" o "episodio").
     * @param userId L'ID dell'utente attualmente loggato.
     */
    fun modificaStato(newStato: Stato, contentId: String, contentType: String, userId: String) {
        viewModelScope.launch {
            Log.d("SchedaContenutoVM", "Modifying status to $newStato for content ID=$contentId, user ID=$userId, type=$contentType")

            // Create or update Contenuto_utente object
            val contenutoUtenteToSave = Contenuto_utente(
                stato = newStato.name, // Use .name for enum
                idUtente = userId,
                idFilm = if (contentType == "film") contentId else null,
                idSerieTv = if (contentType == "serieTv") contentId else null,
                idEpisodio = if (contentType == "episodio") contentId else null
            )

            // Persist the Contenuto_utente
            saveContenutoUtenteInternal(contenutoUtenteToSave)

        }
    }


    /**
     * Rimuove un oggetto Contenuto_utente dal database e aggiorna lo stato del ViewModel.
     *
     * @param contenutoUtente L'oggetto Contenuto_utente da eliminare (deve avere un 'cuId' valido).
     */
    fun rimuoviContenutoUtente(contenutoUtente: Contenuto_utente) {
        viewModelScope.launch {
            try {
                Log.d("SchedaContenutoVM", "Attempting to remove ContenutoUtente: ${contenutoUtente.cuId}")
                database.withTransaction {
                    contenutoUtenteDao.deleteContenutoUtente(contenutoUtente)
                }
                _contenutoUtenteState.value = null
                Log.d("SchedaContenutoVM", "ContenutoUtente removed. _contenutoUtenteState set to null.")

                // Se stavi rimuovendo un episodio, ricalcola lo stato della serie TV
                if (contenutoUtente.contenutoAssociato is Episodio) {
                    val episodio = episodioDao.getEpisodioById(contenutoUtente.idEpisodio!!) // Use non-null assert if you're sure it's an episode
                    episodio?.idSerieDiAppartenenza?.let { serieTvId ->
                        // Dopo la rimozione di un episodio, il suo stato implicito diventa NON_VISTO
                        _episodiStati.value = _episodiStati.value.toMutableMap().apply { put(contenutoUtente.idEpisodio, Stato.NON_VISTO) }
                        updateSerieTvStateBasedOnEpisodes(serieTvId, contenutoUtente.idUtente)
                    }
                }
            } catch (e: Exception) {
                Log.e("SchedaContenutoVM", "Error removing Contenuto_utente: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    /**
     * Ricalcola e aggiorna lo stato di una Serie TV basandosi sullo stato dei suoi episodi.
     */
    private suspend fun updateSerieTvStateBasedOnEpisodes(serieTvId: String, userId: String) {
        Log.d("SchedaContenutoVM", "Starting updateSerieTvStateBasedOnEpisodes for SerieTv $serieTvId")
        val serieTv = serieTvDao.getSerieTvById(serieTvId) ?: run {
            Log.w("SchedaContenutoVM", "SerieTv $serieTvId not found. Cannot determine state.")
            return // Non possiamo determinare lo stato se la Serie TV non esiste
        }
        val allEpisodesOfSerie = episodioDao.getEpisodiBySerieTvId(serieTvId).first()

        if (allEpisodesOfSerie.isEmpty()) {
            Log.w("SchedaContenutoVM", "No episodes found for SerieTv $serieTvId. Cannot determine state.")
            return // Non possiamo determinare lo stato se non ci sono episodi
        }

        // Recupera tutti gli stati degli episodi per questa serie per l'utente loggato
        val episodeStates = allEpisodesOfSerie.map { episode ->
            contenutoUtenteDao.getContenutoUtenteByContenutoAndUtente(userId, episode.id)?.statoEnum ?: Stato.NON_VISTO
        }
        Log.d("SchedaContenutoVM", "Current episode states for SerieTv $serieTvId: $episodeStates")

        // Controlla le condizioni
        val allVisto = episodeStates.all { it == Stato.VISTO }
        val allNonVisto = episodeStates.all { it == Stato.NON_VISTO }
        val hasStarted = episodeStates.any { it == Stato.VISTO || it == Stato.INIZIATO } // Assicurati che IN_CORSO sia gestito per la serie TV

        // Recupera lo stato attuale della Serie TV per l'utente
        val currentSerieTvUserContent = contenutoUtenteDao.getContenutoUtenteByContenutoAndUtente(userId, serieTvId)
        val currentSerieTvStato = currentSerieTvUserContent?.statoEnum

        var newSerieTvStato: Stato? = null

        // REGOLA 1: Una serie TV che non ha né tutti gli episodi marcati come “visti”,
        // né marcati come “non visti”, deve essere spostata nella sezione “iniziata”
        if (!allVisto && !allNonVisto) {
            newSerieTvStato = Stato.INIZIATO
            Log.d("SchedaContenutoVM", "SerieTv $serieTvId moved to INIZIATO (Rule 1)")
        }
        // REGOLA 2: Una Serie TV contrassegnata come “iniziata”, qualora si trovasse
        // ad avere tutti gli episodi marcati come “visti” verrà automaticamente spostata nella sezione “visto”
        else if (allVisto) {
            newSerieTvStato = Stato.VISTO
            Log.d("SchedaContenutoVM", "SerieTv $serieTvId moved to VISTO (Rule 2)")
        }
        // REGOLA 3: Una Serie TV contrassegnata come “iniziata”, qualora si trovasse
        // ad avere tutti gli episodi marcati come “non visti” verrà automaticamente spostata nella sezione “non vista”
        else if (allNonVisto) {
            newSerieTvStato = Stato.NON_VISTO
            Log.d("SchedaContenutoVM", "SerieTv $serieTvId moved to NON_VISTO (Rule 3)")
        }

        // Aggiorna lo stato della Serie TV nel database solo se è cambiato rispetto al suo stato attuale
        // ed escludi lo stato INIZIATO se il currentStato è già INIZIATO e la nuova logica suggerisce ancora INIZIATO
        if (newSerieTvStato != null && newSerieTvStato != currentSerieTvStato) {
            val serieTvContenutoUtente = currentSerieTvUserContent ?: Contenuto_utente(
                stato = Stato.NON_VISTO.name,
                idUtente = userId,
                idFilm = null,
                idSerieTv = serieTvId,
                idEpisodio = null
            )
            val updatedSerieTvContenutoUtente = serieTvContenutoUtente.copy(stato = newSerieTvStato.name)
            saveContenutoUtenteInternal(updatedSerieTvContenutoUtente)
            Log.d("SchedaContenutoVM", "SerieTv $serieTvId state updated to $newSerieTvStato in DB.")
        } else {
            Log.d("SchedaContenutoVM", "No state change needed for SerieTv $serieTvId. Current: $currentSerieTvStato, Suggested: $newSerieTvStato")
        }
    }


    /**
     * Aggiunge una nuova recensione al database e aggiorna la lista delle recensioni nel ViewModel.
     *
     * @param recensione La recensione da aggiungere.
     */
    fun aggiungiRecensione(recensione: Recensione, idContent: String) {
        viewModelScope.launch {
            try {
                Log.d("SchedaContenutoVM", "Attempting to add review: ${recensione.commento}")

                // La logica di transazione è già nel DAO se @Transaction è presente.
                // Assicurati che insertReviewAndUpdateRating sia una @Transaction method nel tuo RecensioneDao.
                recensioneDao.insertReviewAndUpdateRating(recensione, filmDao, serieTvDao, episodioDao)

                Log.d("SchedaContenutoVM", "Review added successfully and rating updated.")

                // After updating, re-fetch the content and its reviews to ensure UI is updated
                val currentContentType = when {
                    recensione.idFilm != null -> "film"
                    recensione.idSerieTv != null -> "serieTv"
                    recensione.idEpisodio != null -> "episodio"
                    else -> throw IllegalArgumentException("Recensione must be associated with a Film, SerieTv, or Episodio.")
                }

                // Re-load the content to refresh its state in the ViewModel, including updated rating and new review
                val userId = recensione.idUtente
                if (userId.isNotEmpty()) {
                    loadContent(idContent, currentContentType, userId) // Pass user ID to loadContent
                } else {
                    Log.e("SchedaContenutoVM", "User ID is empty for review, cannot reload content.")
                }
                _reviewAddedSuccessfully.value = true
            } catch (e: Exception) {
                Log.e("SchedaContenutoVM", "Error adding review: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    // Add a function to reset the state after it's consumed (e.g., after navigation).
    fun resetReviewAddedSuccessfully() {
        _reviewAddedSuccessfully.value = false
    }

    // Factory per l'istanziazione del ViewModel
    class SchedaContenutoPersonaleViewModelFactory(
        private val filmDao: FilmDao,
        private val serieTvDao: SerieTvDao,
        private val recensioneDao: RecensioneDao,
        private val contenutoUtenteDao: ContenutoUtenteDao,
        private val episodioDao: EpisodioDao,
        private val database: AppDatabase // <-- Keep database here
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SchedaContenutoPersonaleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SchedaContenutoPersonaleViewModel(
                    filmDao,
                    serieTvDao,
                    recensioneDao,
                    contenutoUtenteDao,
                    episodioDao,
                    database // <-- Pass database to ViewModel constructor
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}