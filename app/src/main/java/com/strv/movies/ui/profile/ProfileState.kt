package com.strv.movies.ui.profile

import com.strv.movies.model.Movie

data class ProfileState(
    val user: String = "",
    val userName: String = "",
    val favoriteMovies: List<Movie> = emptyList()

)
