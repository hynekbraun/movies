package com.strv.movies.ui.movieslist.movielistutil

sealed class MovieListSnackbarManager(val message: String) {
    object NetworkError: MovieListSnackbarManager(message = "Please check your network")
}
