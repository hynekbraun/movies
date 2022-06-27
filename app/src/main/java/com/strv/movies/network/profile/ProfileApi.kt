package com.strv.movies.network.profile

import com.strv.movies.model.PopularMoviesDTO
import com.strv.movies.model.ProfileDTO
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProfileApi {
    @GET("account")
    suspend fun getAccountDetails(): ProfileDTO

    @GET("account/{accountId}/favorite/movies")
    suspend fun getFavoriteMovies(@Path("accountId")accountId: Int): PopularMoviesDTO

}