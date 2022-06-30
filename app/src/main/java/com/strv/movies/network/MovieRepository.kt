package com.strv.movies.network

import android.util.Log
import androidx.room.withTransaction
import com.strv.movies.data.dao.MoviesDao
import com.strv.movies.data.entity.toDomain
import com.strv.movies.database.AuthDataStore
import com.strv.movies.database.MoviesDatabase
import com.strv.movies.extension.Either
import com.strv.movies.model.AddFavoriteBody
import com.strv.movies.model.Movie
import com.strv.movies.model.MovieDetail
import com.strv.movies.model.MovieDetailDTO
import com.strv.movies.model.Profile
import com.strv.movies.model.Trailer
import com.strv.movies.model.toDomain
import com.strv.movies.model.toEntity
import com.strv.movies.network.auth.AuthError
import com.strv.movies.network.profile.ProfileApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

enum class AddToFavoriteError {
    CREDENTIALS_ERROR, NETWORK_ERROR
}

@Singleton
class MovieRepository @Inject constructor(
    private val moviesApi: MovieApi,
    private val profileApi: ProfileApi,
    private val moviesDao: MoviesDao,
    private val moviesDatabase: MoviesDatabase
) {

    suspend fun fetchMovieDetail(movieId: Int): Either<String, String> {
        return try {
            val movie = moviesApi.getMovieDetail(movieId)
            storeMovieDetail(movie)
            Either.Value(movie.title)
        } catch (exception: Throwable) {
            Either.Error(exception.localizedMessage ?: "Network error")
        }
    }

    suspend fun fetchMovieTrailer(movieId: Int): Either<String, Trailer> {
        Log.d("TAG", "Repository: Fetching trailer")
        return try {
            val trailer = moviesApi.getTrailer(movieId).trailers[0].toDomain()
            Log.d("TRAILER", "Repository: ${trailer.key}")
            Either.Value(trailer)
        } catch (t: Throwable) {
            Either.Error(t.localizedMessage ?: "Network error")
        }
    }

    fun observeMovieDetail(movieId: Int): Flow<MovieDetail?> =
        moviesDao.observeMovieDetail(movieId).map {
            it?.toDomain()
        }

    private suspend fun storeMovieDetail(movie: MovieDetailDTO) {
        moviesDatabase.withTransaction {
            moviesDao.insertMovieDetail(movie.toEntity())
            moviesDao.insertGenres(movie.genres.map { it.toEntity() })
            moviesDao.insertMovieGenres(movie.genres.map { it.toEntity(movie.id) })
        }
    }

    suspend fun addToFavorite(movieId: Int): Either<AddToFavoriteError, String> {
        Log.d("FAVORITE", "MovieRepository: Movie id $movieId")
        return try {
            val accountId = profileApi.getAccountDetails()
            Log.d("FAVORITE", "MovieRepository: Success Account id: ${accountId.id}")
            val movieBody =
                AddFavoriteBody(mediaType = "movie", mediaId = movieId, favourite = true)
            val response = profileApi.addToFavorite(accountId.id, movieBody)
            Log.d("FAVORITE", "MovieRepository: Success added to favorite: ${response}")
            Either.Value(response.statusMessage)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                Either.Error(AddToFavoriteError.CREDENTIALS_ERROR)
            } else Either.Error(AddToFavoriteError.NETWORK_ERROR)
        } catch (t: Throwable) {
            Log.d("FAVORITE", "MovieRepository: Error ${t.cause}")
            Either.Error(AddToFavoriteError.NETWORK_ERROR)
        }
    }

    //MOVIE LIST
    suspend fun fetchPopularMovies(fetchFromRemote: Boolean): Flow<Either<String, List<Movie>>> =
        flow {
            val isDbEmpty = moviesDao.observePopularMovies().isEmpty()
            if (fetchFromRemote || isDbEmpty) {
                try {
                    Log.d("REFRESH", "Repository: fetch movie from remote")
                    val response = moviesApi.getPopularMovies().results.map { it.toEntity() }
                    if (response.isNotEmpty()) {
                        moviesDao.deleteMovies()
                    }
                    moviesDao.insertPopularMovies(response)
                    val cachedMovies = moviesDao.observePopularMovies()
                        .map { it.toDomain() }
                        .sortedByDescending { it.popularity }
                    emit(Either.Value(cachedMovies))
                    Log.d("REFRESH", "Repository: Loaded from network")
                } catch (t: Throwable) {
                    emit(Either.Error(t.localizedMessage ?: "Network Error"))
                }
            } else {
                val localData = moviesDao.observePopularMovies()
                    .map { it.toDomain() }
                    .sortedByDescending { it.popularity }
                emit(Either.Value(localData))
                Log.d("REFRESH", "Repository: Loaded from cache")
            }
        }

}
