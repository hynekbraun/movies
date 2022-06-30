package com.strv.movies.ui.moviedetail.moviedetailutil

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.strv.movies.R


sealed class MovieDetailSnackbarManager(@StringRes val resId: Int) {
    object NetworkError : MovieDetailSnackbarManager(
        R.string.networkError
    )
    object AccountError :
        MovieDetailSnackbarManager(R.string.movieDetailSnackbar_accountError)
    object Success :
        MovieDetailSnackbarManager(R.string.movieDetailSnackbar_savedToFavorites)
    object GenericError :
        MovieDetailSnackbarManager(R.string.genericError)

    @Composable
    fun asStringCompose(): String {
        return stringResource(id = this.resId)
    }
    fun asString(context: Context): String{
        return context.getString(this.resId)
    }
}