package com.strv.movies.network.profile

import com.strv.movies.model.AddFavoriteBody
import com.strv.movies.model.AddFavoriteResponse
import com.strv.movies.model.PopularMoviesDTO
import com.strv.movies.model.ProfileDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProfileApi {
    @GET("account")
    suspend fun getAccountDetails(): ProfileDTO

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovies(@Path("account_id") accountId: Int): PopularMoviesDTO

    @POST("account/{account_id}/favorite")
    suspend fun addToFavorite(
        @Path("account_id") accountId: Int,
        @Body body: AddFavoriteBody
    ): AddFavoriteResponse

}