package com.alkewallet.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.alkewallet.data.model.Transaction
import com.alkewallet.data.model.User
import com.alkewallet.data.repository.WalletRepository

class HomeViewModel(private val repository: WalletRepository) : ViewModel() {

    val currentUserId: Int = repository.getStoredUserId()

    val userLiveData: LiveData<User?> = repository.getUserById(currentUserId)
    val transactionsLiveData: LiveData<List<Transaction>> = repository.getTransactionsByUserId(currentUserId)

    fun refreshData() {
        repository.fetchRemoteTransactions(currentUserId)
    }
}