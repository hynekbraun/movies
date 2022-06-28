package com.strv.movies.model

import com.squareup.moshi.Json

data class AddFavoriteBody(
    @Json(name = "media_type")
    val mediaType: String,
    @Json(name = "media_id")
    val mediaId: Int,
    @Json(name = "favorite")
    val favourite: Boolean
)

data class AddFavoriteResponse(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "status_message")
    val statusMessage: String
)
