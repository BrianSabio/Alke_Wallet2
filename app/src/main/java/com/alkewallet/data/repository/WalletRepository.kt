package com.alkewallet.data.repository

import androidx.lifecycle.LiveData
import com.alkewallet.data.local.SessionManager
import com.alkewallet.data.local.TransactionDao
import com.alkewallet.data.local.UserDao
import com.alkewallet.data.model.Transaction
import com.alkewallet.data.model.User
import com.alkewallet.data.model.WalletResult
import com.alkewallet.data.remote.WalletApiService
import com.alkewallet.data.remote.dto.LoginRequest
import com.alkewallet.data.remote.dto.LoginResponse
import com.alkewallet.data.remote.dto.SignupRequest
import com.alkewallet.data.remote.dto.TransactionDto
import com.alkewallet.data.remote.dto.UserDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalletRepository(
    private val apiService: WalletApiService,
    private val userDao: UserDao,
    private val transactionDao: TransactionDao,
    private val sessionManager: SessionManager
) {

    fun getStoredUserId(): Int {
        return sessionManager.getUserId()
    }

    fun getUserById(userId: Int): LiveData<User?> {
        return userDao.getUserById(userId)
    }

    fun getTransactionsByUserId(userId: Int): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByUserId(userId)
    }

    fun loginUser(email: String, pass: String, callback: (WalletResult<User>) -> Unit) {
        val request = LoginRequest(email = email, password = pass)
        apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val userDto = body?.user
                    val token = body?.accessToken

                    if (body != null && userDto != null && !token.isNullOrEmpty()) {
                        val user = User(
                            id = userDto.id,
                            name = userDto.name,
                            email = userDto.email,
                            points = userDto.points ?: 0,
                            avatarUrl = userDto.avatarUrl
                        )

                        sessionManager.saveSession(token, user.id)
                        CoroutineScope(Dispatchers.IO).launch {
                            userDao.insertUser(user)
                            withContext(Dispatchers.Main) {
                                callback(WalletResult.Success(user))
                            }
                        }
                    } else {
                        callback(WalletResult.Error("Respuesta del servidor incompleta"))
                    }
                } else {
                    callback(WalletResult.Error("Credenciales inválidas o error de autenticación (${response.code()})"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(WalletResult.Error("Error de conexión a la red: ${t.localizedMessage}", t))
            }
        })
    }

    fun registerUser(name: String, email: String, pass: String, callback: (WalletResult<User>) -> Unit) {
        val request = SignupRequest(name = name, email = email, password = pass)
        apiService.signup(request).enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful && response.body() != null) {
                    val dto = response.body()!!
                    val user = User(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        points = dto.points ?: 0,
                        avatarUrl = dto.avatarUrl
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.insertUser(user)
                        withContext(Dispatchers.Main) {
                            callback(WalletResult.Success(user))
                        }
                    }
                } else {
                    callback(WalletResult.Error("No se pudo completar el registro (${response.code()})"))
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                callback(WalletResult.Error("Error de red al registrar usuario: ${t.localizedMessage}", t))
            }
        })
    }

    /**
     * Sincroniza las transacciones remotas del usuario con Room.
     * Filtra los DTOs cuyo ID ya exista localmente para prevenir duplicidad.
     */
    fun fetchRemoteTransactions(userId: Int) {
        val token = sessionManager.getToken() ?: return
        val bearerToken = "Bearer $token"

        apiService.getTransactions(bearerToken).enqueue(object : Callback<List<TransactionDto>> {
            override fun onResponse(call: Call<List<TransactionDto>>, response: Response<List<TransactionDto>>) {
                if (response.isSuccessful && response.body() != null) {
                    val dtoList = response.body()!!

                    CoroutineScope(Dispatchers.IO).launch {
                        // Mecanismo anti-duplicados: consultar remote_ids locales existentes
                        val existingRemoteIds = transactionDao.getExistingRemoteIds(userId).toSet()

                        val newTransactions = dtoList.filter { dto ->
                            dto.id == null || !existingRemoteIds.contains(dto.id)
                        }.map { dto ->
                            Transaction(
                                remoteId = dto.id,
                                amount = dto.amount,
                                concept = dto.concept,
                                date = dto.date ?: "Fecha no disponible",
                                type = dto.type,
                                userId = userId
                            )
                        }

                        if (newTransactions.isNotEmpty()) {
                            transactionDao.insertAllTransactions(newTransactions)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<TransactionDto>>, t: Throwable) {
                // Fallo silencioso de sincronización remota: la app conserva y muestra los datos locales de Room
            }
        })
    }

    /**
     * Envía o solicita una transacción remota y actualiza tanto la tabla de transacciones
     * como la entidad [User] en Room si la respuesta incluye el saldo actualizado.
     */
    fun sendRemoteTransaction(
        amount: Double,
        concept: String,
        type: String,
        toUserId: Int,
        fromUserId: Int,
        callback: (WalletResult<Transaction>) -> Unit
    ) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            callback(WalletResult.Error("Sesión no válida o token expirado"))
            return
        }

        val bearerToken = "Bearer $token"
        val dto = TransactionDto(
            amount = amount,
            concept = concept,
            type = type,
            toUserId = toUserId,
            fromUserId = fromUserId
        )

        apiService.createTransaction(bearerToken, dto).enqueue(object : Callback<TransactionDto> {
            override fun onResponse(call: Call<TransactionDto>, response: Response<TransactionDto>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseDto = response.body()!!
                    val localTransaction = Transaction(
                        remoteId = responseDto.id,
                        amount = responseDto.amount,
                        concept = responseDto.concept,
                        date = responseDto.date ?: "Hoy",
                        type = responseDto.type,
                        userId = fromUserId
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        transactionDao.insertTransaction(localTransaction)

                        // 1. Si la respuesta incluye el UserDto actualizado, guardarlo en Room
                        val updatedUserDto = responseDto.user
                        if (updatedUserDto != null) {
                            val updatedUser = User(
                                id = updatedUserDto.id,
                                name = updatedUserDto.name,
                                email = updatedUserDto.email,
                                points = updatedUserDto.points ?: 0,
                                avatarUrl = updatedUserDto.avatarUrl
                            )
                            userDao.insertUser(updatedUser)
                        }

                        withContext(Dispatchers.Main) {
                            callback(WalletResult.Success(localTransaction))
                        }
                    }
                } else {
                    callback(WalletResult.Error("Error al procesar la transacción (${response.code()})"))
                }
            }

            override fun onFailure(call: Call<TransactionDto>, t: Throwable) {
                callback(WalletResult.Error("Error de conexión al realizar la transacción: ${t.localizedMessage}", t))
            }
        })
    }

    fun clearLocalSession(onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.clearUser()
            transactionDao.clearTransactions()
            sessionManager.clearSession()
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}