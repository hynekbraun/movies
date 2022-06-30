package com.strv.movies.ui.movieslist.movielistutil

import com.strv.movies.model.Movie

data class MoviesListViewState(
    val movies: List<Movie> = emptyList(),
    val loading: Boolean = false,
    val isRefreshing: Boolean = false
)
