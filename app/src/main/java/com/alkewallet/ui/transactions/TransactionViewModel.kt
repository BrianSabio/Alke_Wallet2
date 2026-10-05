package com.alkewallet.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alkewallet.data.model.Transaction
import com.alkewallet.data.model.WalletResult
import com.alkewallet.data.repository.WalletRepository

class TransactionViewModel(private val repository: WalletRepository) : ViewModel() {

    private val _transactionState = MutableLiveData<WalletResult<Transaction>>()
    val transactionState: LiveData<WalletResult<Transaction>> get() = _transactionState

    fun sendMoney(amountStr: String, concept: String, toUserId: Int, type: String) {
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _transactionState.value = WalletResult.Error("Ingresa un monto válido")
            return
        }

        _transactionState.value = WalletResult.Loading
        val currentUserId = repository.getStoredUserId()

        repository.sendRemoteTransaction(amount, concept, type, toUserId, currentUserId) { result ->
            _transactionState.postValue(result)
        }
    }
}