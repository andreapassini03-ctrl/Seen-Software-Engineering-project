// File: com/example/progetto_software/controller/CatalogoViewModel.kt
package com.example.progetto_software.controller

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.progetto_software.data.Film
import com.example.progetto_software.data.SerieTv
import com.example.progetto_software.database.AppDatabase
import com.example.progetto_software.database.MyApplicationClass
import com.example.progetto_software.database.FilmDao
import com.example.progetto_software.database.SerieTvDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class CatalogoViewModel(application: MyApplicationClass) : AndroidViewModel(application) {

    private val filmDao: FilmDao = application.database.filmDao()
    private val serieTvDao: SerieTvDao = application.database.serieTvDao()

    private val _allFilms = MutableStateFlow<List<Film>>(emptyList())
    val allFilms: StateFlow<List<Film>> = _allFilms.asStateFlow()

    private val _allSerieTv = MutableStateFlow<List<SerieTv>>(emptyList())
    val allSerieTv: StateFlow<List<SerieTv>> = _allSerieTv.asStateFlow()

    private val _ageFilter = MutableStateFlow<Int?>(null)
    private val _ratingFilter = MutableStateFlow<Float?>(null)
    private val _genreFilter = MutableStateFlow<String?>(null) // This will store the selected genre (e.g., "Azione")


    init {
        viewModelScope.launch {
            // Combine all relevant flows for filtering
            combine(
                filmDao.getAllFilms(),
                serieTvDao.getAllSerieTv(),
                _ageFilter,
                _ratingFilter,
                _genreFilter
            ) { films, series, ageFilter, ratingFilter, genreFilter ->

                // Apply Age Filter
                val ageFilteredFilms = if (ageFilter != null) {
                    films.filter { film -> film.eta <= ageFilter }
                } else {
                    films
                }

                val ageFilteredSeries = if (ageFilter != null) {
                    series.filter { serie -> serie.eta <= ageFilter }
                } else {
                    series
                }

                // Apply Rating Filter to age-filtered content
                val ratingAgeFilteredFilms = if (ratingFilter != null) {
                    ageFilteredFilms.filter { film -> film.valutazioneMedia >= ratingFilter }
                } else {
                    ageFilteredFilms
                }

                val ratingAgeFilteredSeries = if (ratingFilter != null) {
                    ageFilteredSeries.filter { serie -> serie.valutazioneMedia >= ratingFilter }
                } else {
                    ageFilteredSeries
                }

                // Apply Genre Filter to rating-age-filtered content
                val finalFilteredFilms = if (genreFilter != null) {
                    ratingAgeFilteredFilms.filter { film ->
                        // Split the film's genre string by comma and check if it contains the selected genre
                        film.genere.split(",").map { it.trim() }.contains(genreFilter)
                    }
                } else {
                    ratingAgeFilteredFilms
                }
                _allFilms.value = finalFilteredFilms

                val finalFilteredSeries = if (genreFilter != null) {
                    ratingAgeFilteredSeries.filter { serie ->
                        // Split the serie's genre string by comma and check if it contains the selected genre
                        serie.genere.split(",").map { it.trim() }.contains(genreFilter)
                    }
                } else {
                    ratingAgeFilteredSeries
                }
                _allSerieTv.value = finalFilteredSeries

            }.collect { /* No-op, values are set inside the combine block */ }
        }
    }

    fun filterContentByAge(age: Int?) {
        _ageFilter.value = age
    }

    fun clearAgeFilter() {
        _ageFilter.value = null
    }

    fun filterContentByRating(minRating: Float?) {
        _ratingFilter.value = minRating
    }

    fun clearRatingFilter() {
        _ratingFilter.value = null
    }

    fun filterContentByGenre(genre: String?) {
        _genreFilter.value = genre
    }

    fun clearGenreFilter() {
        _genreFilter.value = null
    }

    class CatalogoViewModelFactory(private val application: MyApplicationClass) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CatalogoViewModel::class.java)) {
                return CatalogoViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}