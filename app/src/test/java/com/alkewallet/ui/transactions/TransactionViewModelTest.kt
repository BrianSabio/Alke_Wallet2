package com.alkewallet.ui.transactions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.alkewallet.data.model.WalletResult
import com.alkewallet.data.repository.WalletRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class TransactionViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: WalletRepository
    private lateinit var viewModel: TransactionViewModel

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    private fun <T> eqObj(value: T): T {
        eq(value)
        return value
    }

    @Before
    fun setUp() {
        repository = mock(WalletRepository::class.java)
        `when`(repository.getStoredUserId()).thenReturn(1)
        viewModel = TransactionViewModel(repository)
    }

    @Test
    fun sendMoney_withInvalidAmount_setsErrorState() {
        viewModel.sendMoney("abc", "Notas", 0, "send")
        val state = viewModel.transactionState.value
        assertTrue(state is WalletResult.Error)
        assertEquals("Ingresa un monto válido", (state as WalletResult.Error).message)
    }

    @Test
    fun sendMoney_withZeroOrNegativeAmount_setsErrorState() {
        viewModel.sendMoney("0", "Notas", 0, "send")
        val state = viewModel.transactionState.value
        assertTrue(state is WalletResult.Error)
        assertEquals("Ingresa un monto válido", (state as WalletResult.Error).message)
    }

    @Test
    fun sendMoney_withValidAmount_invokesRepositorySendRemoteTransaction() {
        viewModel.sendMoney("100.50", "Para: Juan", 0, "send")
        verify(repository).sendRemoteTransaction(eqObj(100.50), eqObj("Para: Juan"), eqObj("send"), eqObj(0), eqObj(1), anyObject())
    }
}