package com.alkewallet.data.remote

import com.alkewallet.data.remote.dto.LoginRequest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WalletApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: WalletApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WalletApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun login_returnsSuccessfulUserResponse() {
        val jsonResponse = """
            {
                "accessToken": "mock-token-123",
                "user": {
                    "id": 1,
                    "name": "Usuario Demo",
                    "email": "demo@alke.com",
                    "points": 1000,
                    "avatar": null
                }
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val call = apiService.login(LoginRequest("demo@alke.com", "123456"))
        val response = call.execute()

        assertTrue(response.isSuccessful)
        val body = response.body()
        assertNotNull(body)
        assertEquals("mock-token-123", body?.accessToken)
        assertEquals("Usuario Demo", body?.user?.name)
    }

    @Test
    fun getTransactions_returnsListOfTransactions() {
        val jsonResponse = """
            [
                {
                    "id": 101,
                    "amount": 150.50,
                    "concept": "Para: Juan",
                    "date": "2025-02-20",
                    "type": "send",
                    "to_user_id": 2,
                    "from_user_id": 1
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val call = apiService.getTransactions("Bearer mock-token")
        val response = call.execute()

        assertTrue(response.isSuccessful)
        val transactions = response.body()
        assertNotNull(transactions)
        assertEquals(1, transactions?.size)
        assertEquals("Para: Juan", transactions?.get(0)?.concept)
    }
}