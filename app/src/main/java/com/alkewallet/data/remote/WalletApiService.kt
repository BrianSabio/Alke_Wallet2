package com.alkewallet.data.remote

import com.alkewallet.data.remote.dto.LoginRequest
import com.alkewallet.data.remote.dto.LoginResponse
import com.alkewallet.data.remote.dto.SignupRequest
import com.alkewallet.data.remote.dto.TransactionDto
import com.alkewallet.data.remote.dto.UserDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface WalletApiService {

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("users")
    fun signup(@Body request: SignupRequest): Call<UserDto>

    @GET("transactions")
    fun getTransactions(
        @Header("Authorization") token: String
    ): Call<List<TransactionDto>>

    @POST("transactions")
    fun createTransaction(
        @Header("Authorization") token: String,
        @Body transaction: TransactionDto
    ): Call<TransactionDto>
}