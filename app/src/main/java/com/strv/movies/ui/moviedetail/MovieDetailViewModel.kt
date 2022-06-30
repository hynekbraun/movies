package com.strv.movies.ui.moviedetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strv.movies.extension.fold
import com.strv.movies.network.AddToFavoriteError
import com.strv.movies.network.MovieRepository
import com.strv.movies.ui.moviedetail.moviedetailutil.MovieDetailSnackbarManager
import com.strv.movies.ui.moviedetail.moviedetailutil.MovieDetailViewState
import com.strv.movies.ui.navigation.MoviesNavArguments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val movieId =
        requireNotNull(savedStateHandle.get<Int>(MoviesNavArguments.MOVIE_ID_KEY)) {
            "Movie id is missing..."
        }

    private val _snackbarFlow = Channel<MovieDetailSnackbarManager>()
    val snackbarFlow = _snackbarFlow.receiveAsFlow()

    var viewState = MutableStateFlow(MovieDetailViewState(loading = true))
        private set

    init {
        Log.d("TAG", "ViewModel init triggered")
        getData()

        viewModelScope.launch {
            movieRepository.fetchMovieDetail(movieId).fold(
                { error ->
                    Log.d("TAG", "MovieDetailLoadingError: $error")
                    viewState.update {
                        MovieDetailViewState(
                            error = error
                        )
                    }
                },
                { movieTitle ->
                    Log.d("TAG", "MovieDetailLoaded: $movieTitle")
                }
            )
        }
    }


    private fun getData() {
        viewModelScope.launch {
            val movieDetailDeferred = async { getDetail() }
            val movieTrailerDeferred = async { fetchTrailer() }
            movieDetailDeferred.await()
            movieTrailerDeferred.await()
        }

    }

    private suspend fun fetchTrailer() {
        movieRepository.fetchMovieTrailer(movieId).fold(
            { error ->
                Log.d("TAG", "ViewModel MovieTrailer Error")
                _snackbarFlow.send(MovieDetailSnackbarManager.TrailerError)
            },
            {
                Log.d("TAG", "ViewModel MovieTrailer Success $it")
                viewState.value =
                    viewState.value.copy(trailer = it, loading = false, error = null)
            }
        )
    }

    private suspend fun getDetail() {
        movieRepository.observeMovieDetail(movieId).collect { detail ->
            Log.d("TAG", "ViewModel MovieDetail collected $detail")
            viewState.value =
                viewState.value.copy(movie = detail, loading = false, error = null)
        }
    }

    fun addMovieToFavorites() {
        viewModelScope.launch {
            Log.d("FAVORITE", "ViewModel addToFavorite Triggered")
            movieRepository.addToFavorite(movieId).fold({ error ->
                Log.d("FAVORITE", "ViewModel addToFavorite Error $error")
                when (error) {
                    AddToFavoriteError.NETWORK_ERROR -> _snackbarFlow.send(
                        MovieDetailSnackbarManager.NetworkError
                    )
                    AddToFavoriteError.CREDENTIALS_ERROR -> _snackbarFlow.send(
                        MovieDetailSnackbarManager.AccountError
                    )
                }
            },
                {
                    Log.d("FAVORITE", "ViewModel add to Favorite Success $it")
                    _snackbarFlow.send(MovieDetailSnackbarManager.Success)
                })
        }
    }

    fun updateVideoProgress(progress: Float) {
        viewState.update { it.copy(videoProgress = progress) }
    }

}
