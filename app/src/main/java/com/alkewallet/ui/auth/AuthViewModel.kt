package com.alkewallet.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alkewallet.data.model.User
import com.alkewallet.data.model.WalletResult
import com.alkewallet.data.repository.WalletRepository

class AuthViewModel(private val repository: WalletRepository) : ViewModel() {

    private val _authState = MutableLiveData<WalletResult<User>>()
    val authState: LiveData<WalletResult<User>> get() = _authState

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = WalletResult.Error("Por favor completa todos los campos")
            return
        }
        _authState.value = WalletResult.Loading
        repository.loginUser(email, pass) { result ->
            _authState.postValue(result)
        }
    }

    fun signup(name: String, email: String, pass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _authState.value = WalletResult.Error("Por favor completa todos los campos")
            return
        }
        _authState.value = WalletResult.Loading
        repository.registerUser(name, email, pass) { result ->
            _authState.postValue(result)
        }
    }
}