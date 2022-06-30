package com.strv.movies.ui.movieslist.movielistutil

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.strv.movies.R

sealed class MovieListSnackbarManager(@StringRes val resId: Int) {
    object NetworkError : MovieListSnackbarManager(R.string.networkError)

    @Composable
    fun asStringCompose(): String {
        return stringResource(id = this.resId)
    }

    fun asString(context: Context): String {
        return context.getString(this.resId)
    }
}
