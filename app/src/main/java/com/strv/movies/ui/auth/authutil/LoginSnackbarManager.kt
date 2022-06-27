package com.strv.movies.ui.auth.authutil

sealed class LoginSnackbarManager(val message: String) {
    object UsernameSnackbar : LoginSnackbarManager(message = "Please fill in the username")
    object PasswordSnackbar : LoginSnackbarManager(message = "Please fill in the password")
    object CredentialsError : LoginSnackbarManager(message = "Wrong credentials")
    object NetworkError : LoginSnackbarManager(message = "Network error")
    object FunctionNotSupported : LoginSnackbarManager(message = "Function not supported")
    object GenericError : LoginSnackbarManager(message = "Something went wrong")
}
