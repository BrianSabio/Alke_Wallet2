package com.alkewallet.ui.auth

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

class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: WalletRepository
    private lateinit var viewModel: AuthViewModel

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
        viewModel = AuthViewModel(repository)
    }

    @Test
    fun login_withEmptyEmail_setsErrorState() {
        viewModel.login("", "123456")
        val state = viewModel.authState.value
        assertTrue(state is WalletResult.Error)
        assertEquals("Por favor completa todos los campos", (state as WalletResult.Error).message)
    }

    @Test
    fun login_withEmptyPassword_setsErrorState() {
        viewModel.login("test@alke.com", "")
        val state = viewModel.authState.value
        assertTrue(state is WalletResult.Error)
        assertEquals("Por favor completa todos los campos", (state as WalletResult.Error).message)
    }

    @Test
    fun login_withValidFields_invokesRepositoryLoginUser() {
        viewModel.login("demo@alke.com", "password123")
        verify(repository).loginUser(eqObj("demo@alke.com"), eqObj("password123"), anyObject())
    }

    @Test
    fun signup_withEmptyFields_setsErrorState() {
        viewModel.signup("", "demo@alke.com", "123456")
        val state = viewModel.authState.value
        assertTrue(state is WalletResult.Error)
        assertEquals("Por favor completa todos los campos", (state as WalletResult.Error).message)
    }

    @Test
    fun signup_withValidFields_invokesRepositoryRegisterUser() {
        viewModel.signup("Demo User", "demo@alke.com", "password123")
        verify(repository).registerUser(eqObj("Demo User"), eqObj("demo@alke.com"), eqObj("password123"), anyObject())
    }
}