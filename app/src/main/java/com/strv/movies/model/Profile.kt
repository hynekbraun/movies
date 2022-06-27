package com.strv.movies.model

import com.squareup.moshi.Json

data class ProfileDTO(
    @Json(name = "name") val name: String,
    @Json(name = "username") val username: String,
    @Json(name = "id") val id: Int
)

data class Profile(
    val name: String,
    val username: String,
    val id: Int
)

fun ProfileDTO.toProfile(): Profile {
    return Profile(
        name = name,
        username = username,
        id = id
    )
}