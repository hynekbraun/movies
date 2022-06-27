package com.strv.movies.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strv.movies.extension.fold
import com.strv.movies.network.auth.AuthError
import com.strv.movies.network.auth.AuthRepository
import com.strv.movies.network.auth.LoginEvent
import com.strv.movies.ui.auth.authutil.LoginSnackbarManager
import com.strv.movies.ui.auth.authutil.LoginViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _errorFlow = Channel<LoginSnackbarManager>()
    val errorFlow = _errorFlow.receiveAsFlow()

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState = _viewState.asStateFlow()


    fun loginEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> login(onSuccess = event.onSuccess)
            is LoginEvent.UpdatePassword -> updatePassword(event.input)
            is LoginEvent.UpdateUsername -> updateName(event.input)
            LoginEvent.OnClickNotImplemented -> {
                viewModelScope.launch {
                    _errorFlow.send(LoginSnackbarManager.FunctionNotSupported)
                }
            }
        }
    }

    private fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logIn(
                username = _viewState.value.user,
                password = _viewState.value.password
            ).fold(
                { error ->
                    if (_viewState.value.user.isBlank()) {
                        _errorFlow.send(LoginSnackbarManager.UsernameSnackbar)
                    } else if (_viewState.value.password.isBlank()) {
                        _errorFlow.send(LoginSnackbarManager.PasswordSnackbar)
                    } else if (error == AuthError.NETWORK_ERROR) {

                        _errorFlow.send(LoginSnackbarManager.NetworkError)
                    } else if (error == AuthError.INVALID_CREDENTIALS) {
                        _errorFlow.send(LoginSnackbarManager.CredentialsError)
                    } else {
                        _errorFlow.send(LoginSnackbarManager.GenericError)
                    }
                    Log.d("TAG", "Login failed - ${error.name}")
                },
                {
                    onSuccess()
                }
            )
        }
    }

    private fun updateName(input: String) {
        _viewState.update {
            it.copy(user = input)
        }
    }

    private fun updatePassword(input: String) {
        _viewState.update {
            it.copy(password = input)
        }
    }
}