package com.strv.movies.ui.auth.authutil

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.strv.movies.R

sealed class LoginSnackbarManager(@StringRes val resId: Int) {
    object UsernameSnackbar : LoginSnackbarManager(R.string.loginSnackbar_missingUserName)
    object PasswordSnackbar : LoginSnackbarManager(R.string.loginSnackbar_missingPassword)
    object CredentialsError : LoginSnackbarManager(R.string.loginSnackbar_wrongCredentials)
    object NetworkError : LoginSnackbarManager(R.string.networkError)
    object FunctionNotSupported : LoginSnackbarManager(R.string.loginSnackbar_functionNotSupported)
    object GenericError : LoginSnackbarManager(R.string.genericError)

    @Composable
    fun asStringCompose(): String {
        return stringResource(id = this.resId)
    }

    fun asString(context: Context): String {
        return context.getString(this.resId)
    }
}
