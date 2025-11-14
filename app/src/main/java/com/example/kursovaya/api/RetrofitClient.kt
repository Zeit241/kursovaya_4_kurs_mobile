package com.example.kursovaya.api

import android.content.Context
import android.util.Log
import com.example.kursovaya.model.AuthState
import com.example.kursovaya.repository.AuthRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private const val BASE_URL = "http://10.0.2.2:8085/" // Для эмулятора Android
    // Для реального устройства используйте: "http://YOUR_IP:8085/"
    
    private var authRepository: AuthRepository? = null
    private var retrofitInstance: Retrofit? = null
    
    fun init(context: Context) {
        authRepository = AuthRepository(context)
        // Пересоздаем Retrofit при инициализации, чтобы interceptor получил актуальный authRepository
        retrofitInstance = null
    }
    
    // Interceptor для добавления JWT токена в заголовки
    private fun createAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            
            // Получаем токен каждый раз при запросе
            val token = authRepository?.getAuthState()?.let { state ->
                if (state is AuthState.Authenticated) {
                    Log.d("RetrofitClient", "Токен найден: ${state.token.take(20)}...")
                    state.token
                } else {
                    Log.w("RetrofitClient", "Пользователь не авторизован")
                    null
                }
            } ?: run {
                Log.w("RetrofitClient", "authRepository is null или токен не найден")
                null
            }
            
            val newRequest = if (token != null) {
                val requestWithAuth = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                Log.d("RetrofitClient", "Добавлен заголовок Authorization для ${originalRequest.url}")
                requestWithAuth
            } else {
                Log.w("RetrofitClient", "Запрос без токена: ${originalRequest.url}")
                originalRequest
            }
            
            chain.proceed(newRequest)
        }
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val retrofit: Retrofit
        get() {
            if (retrofitInstance == null) {
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(createAuthInterceptor())
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
                
                retrofitInstance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofitInstance!!
        }
    
    val authApi: AuthApi
        get() = retrofit.create(AuthApi::class.java)
    
    val doctorsApi: DoctorsApi
        get() = retrofit.create(DoctorsApi::class.java)
}

