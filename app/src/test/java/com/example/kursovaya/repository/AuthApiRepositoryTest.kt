package com.example.kursovaya.repository

import com.example.kursovaya.api.AuthApi
import com.example.kursovaya.api.RetrofitClient
import com.example.kursovaya.model.api.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import retrofit2.Response

class AuthApiRepositoryTest {

    @Mock
    private lateinit var mockAuthApi: AuthApi

    private lateinit var authApiRepository: AuthApiRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        // Используем рефлексию для установки mock API
        // В реальном проекте лучше использовать dependency injection
        authApiRepository = AuthApiRepository()
    }

    @Test
    fun `login should return success when API call is successful`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val loginResponse = LoginResponse(
            token = "test-token",
            email = email,
            message = "Успешный вход"
        )
        val apiResponse = ApiResponse(
            success = true,
            status = 200,
            message = "Успешный вход",
            data = loginResponse
        )
        val response = Response.success(apiResponse)

        // Note: В реальном тесте нужно мокировать RetrofitClient
        // Это упрощенный пример структуры теста
    }

    @Test
    fun `login should return failure when API call fails`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "wrongpassword"
        val apiResponse = ApiResponse<LoginResponse>(
            success = false,
            status = 401,
            message = "Неверный email или пароль",
            data = null
        )
        val response = Response.success(apiResponse)

        // Note: В реальном тесте нужно мокировать RetrofitClient
        // Это упрощенный пример структуры теста
    }

    @Test
    fun `registerWithPatient should return success when registration is successful`() = runTest {
        // Arrange
        val email = "newuser@example.com"
        val phone = "+1234567890"
        val password = "password123"
        val confirmPassword = "password123"
        val fio = "Иван Иванов"
        val birthDate = "1990-01-01"
        val gender: Short = 1

        val registerResponse = RegisterWithPatientResponse(
            token = "new-token",
            email = email,
            message = "Регистрация успешна"
        )
        val apiResponse = ApiResponse(
            success = true,
            status = 200,
            message = "Регистрация успешна",
            data = registerResponse
        )
        val response = Response.success(apiResponse)

        // Note: В реальном тесте нужно мокировать RetrofitClient
        // Это упрощенный пример структуры теста
    }
}





