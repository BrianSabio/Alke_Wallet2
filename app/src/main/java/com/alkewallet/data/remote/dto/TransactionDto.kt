package com.alkewallet.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TransactionDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("amount") val amount: Double,
    @SerializedName("concept") val concept: String,
    @SerializedName("date") val date: String? = null,
    @SerializedName("type") val type: String,
    @SerializedName("to_user_id") val toUserId: Int,
    @SerializedName("from_user_id") val fromUserId: Int,
    @SerializedName("user") val user: UserDto? = null
)