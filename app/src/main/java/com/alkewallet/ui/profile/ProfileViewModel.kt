package com.alkewallet.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.alkewallet.data.model.User
import com.alkewallet.data.repository.WalletRepository

class ProfileViewModel(private val repository: WalletRepository) : ViewModel() {

    private val currentUserId: Int = repository.getStoredUserId()
    val userLiveData: LiveData<User?> = repository.getUserById(currentUserId)

    fun logout(onComplete: () -> Unit) {
        repository.clearLocalSession {
            onComplete()
        }
    }
}