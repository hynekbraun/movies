package com.strv.movies.ui.moviedetail.moviedetailutil

sealed class MovieDetailSnackbarManager(val message: String){
    object NetworkError: MovieDetailSnackbarManager(message = "Network Error")
    object AccountError: MovieDetailSnackbarManager(message = "Problem with account")
    object Success: MovieDetailSnackbarManager(message = "Movie Successfully saved to favorites")
    object TrailerError: MovieDetailSnackbarManager(message = "Could not find trailer")
    object GenericError: MovieDetailSnackbarManager(message = "Something went wrong")
}