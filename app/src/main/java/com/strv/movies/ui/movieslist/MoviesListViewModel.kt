package com.strv.movies.ui.movieslist

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strv.movies.extension.fold
import com.strv.movies.network.MovieRepository
import com.strv.movies.ui.auth.authutil.LoginSnackbarManager
import com.strv.movies.ui.movieslist.movielistutil.MovieListSnackbarManager
import com.strv.movies.ui.movieslist.movielistutil.MoviesListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    var viewState = mutableStateOf(MoviesListViewState())
        private set

    private val _snackbarFlow = Channel<MovieListSnackbarManager>()
    val snackbarFlow = _snackbarFlow.receiveAsFlow()

    init {
        observePopularMovies(false)
    }

    private fun observePopularMovies(fromNetwork: Boolean) {
        viewState.value = viewState.value.copy(loading = true)
        viewModelScope.launch {
            movieRepository.fetchPopularMovies(fromNetwork).collect { response ->
                response.fold(
                    { error ->
                        Log.d("TAG", "PopularMovies Error: $error")
                        _snackbarFlow.send(MovieListSnackbarManager.NetworkError)
                        viewState.value = viewState.value.copy(loading = false, isRefreshing = false)
                    },
                    { list ->
                        viewState.value = viewState.value.copy(movies = list, loading = false, isRefreshing = false)
                    }
                )
            }

        }
    }

    fun refreshData() {
        Log.d("REFRESH", "MoviesViewModel refresh triggered")
        viewState.value = viewState.value.copy(isRefreshing = true)
        observePopularMovies(true)
    }

}