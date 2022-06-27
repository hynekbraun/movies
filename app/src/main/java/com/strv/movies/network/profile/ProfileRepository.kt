package com.strv.movies.network.profile

import android.util.Log
import com.strv.movies.data.entity.toDomain
import com.strv.movies.extension.Either
import com.strv.movies.model.DeleteSessionBody
import com.strv.movies.model.Movie
import com.strv.movies.model.Profile
import com.strv.movies.model.toEntity
import com.strv.movies.model.toProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileApi: ProfileApi
) {
    suspend fun fetchProfileDetails(): Either<String, Profile> {
        Log.d("PROFILE", "Profile Repository fetch")
        return try {
            val profileDetails = profileApi.getAccountDetails()
            Log.d("PROFILE", "Profile repository: $profileDetails")
            Either.Value(profileDetails.toProfile())
        } catch (t: Throwable) {
            Log.d("PROFILE", "Profile Repository Error")
            Either.Error(t.localizedMessage ?: "Something went wrong")
        }
    }

    suspend fun fetchFavoriteMovies(accountId: Int): Either<String, List<Movie>> {
        Log.d("FAVORITE", "ProfileRepository: Fetch triggered")
        return try {
            val favoriteMovies = profileApi.getFavoriteMovies(accountId)
            Either.Value(favoriteMovies.results.map { it.toEntity().toDomain() })
        } catch (t: Throwable) {
            Log.d("FAVORITE", "ProfileRepository: Fetch error ${t.localizedMessage}")
            Either.Error(t.localizedMessage ?: "Something went wrong")
        }
    }
}

