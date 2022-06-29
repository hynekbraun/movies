package com.strv.movies.ui.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strv.movies.database.MoviesDataStore
import com.strv.movies.extension.fold
import com.strv.movies.network.auth.AuthRepository
import com.strv.movies.network.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val moviesDataStore: MoviesDataStore,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private var accountId: Int? = null
    val avatarPath = moviesDataStore.avatarPathFlow
    var state = mutableStateOf(ProfileState())
        private set

    init {
        fetchProfileData()
    }

    fun profileEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LogOut -> logout(onSuccess = event.onSuccess)
            is ProfileEvent.NewAvatar -> onNewAvatar(event.path)
            ProfileEvent.RemoveAvatar -> removeAvatar()
        }
    }

    private fun fetchFavoriteMovies() {
        viewModelScope.launch {
            Log.d("FAVORITE", "ProfileViewModel: Fetch favorite triggered $accountId")
            accountId?.let { id ->
                Log.d("FAVORITE", "ProfileViewModel: Fetch favorite triggered $id")
                profileRepository.fetchFavoriteMovies(id).fold({
                    Log.d("FAVORITE", "ProfileViewModel: Error fetching popular movies")
                }, {
                    Log.d("FAVORITE", "ProfileViewModel: Fetch successfull ${it.size}")
                    state.value = state.value.copy(favoriteMovies = it)
                })
            }
        }
    }

    private fun onNewAvatar(newAvatarPath: String) {
        viewModelScope.launch {
            moviesDataStore.avatarPathFlow.first()?.let {
                File(it).delete()
            }
            moviesDataStore.setAvatarPath(newAvatarPath)
        }
    }

    private fun removeAvatar() {
        viewModelScope.launch {
            moviesDataStore.removeAvatarPath()
        }
    }

    private fun fetchProfileData() {
        Log.d("PROFILE", "ProfileVM fetched")
        viewModelScope.launch {
            profileRepository.fetchProfileDetails().fold(
                { error ->
                    Log.d("PROFILE", "Profile error $error")
                }, { profileDetails ->
                    state.value = state.value.copy(
                        user = profileDetails.name,
                        userName = profileDetails.username
                    )
                    accountId = profileDetails.id
                    Log.d("PROFILE", "Profile data: $profileDetails")
                }
            )
            fetchFavoriteMovies()
        }
    }

    private fun logout(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            authRepository.logOut().fold(
                { Log.d("TAG", "Logout failed") },
                { onSuccess() }
            )
        }
    }
}