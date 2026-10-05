package com.alkewallet.data.model

sealed class WalletResult<out T> {
    object Loading : WalletResult<Nothing>()
    data class Success<out T>(val data: T) : WalletResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : WalletResult<Nothing>()
}