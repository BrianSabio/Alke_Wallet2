package com.alkewallet.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("points") val points: Int? = 0,
    @SerializedName("avatar") val avatarUrl: String? = null
)