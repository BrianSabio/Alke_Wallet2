package com.alkewallet

import android.app.Application
import com.alkewallet.data.local.SessionManager
import com.alkewallet.data.local.WalletDatabase
import com.alkewallet.data.remote.RetrofitClient
import com.alkewallet.data.repository.WalletRepository

/**
 * Clase Application de Alke Wallet.
 * Mantiene la instancia Singleton de [WalletRepository] disponible para la inyección en ViewModels.
 */
class WalletApplication : Application() {

    private val database by lazy { WalletDatabase.getDatabase(this) }
    private val sessionManager by lazy { SessionManager(this) }

    val repository by lazy {
        WalletRepository(
            apiService = RetrofitClient.instance,
            userDao = database.userDao(),
            transactionDao = database.transactionDao(),
            sessionManager = sessionManager
        )
    }
}