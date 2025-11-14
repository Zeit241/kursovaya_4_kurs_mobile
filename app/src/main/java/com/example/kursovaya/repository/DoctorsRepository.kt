package com.example.kursovaya.repository

import android.content.Context
import android.util.Log
import com.example.kursovaya.api.RetrofitClient
import com.example.kursovaya.model.api.DoctorApi
import com.example.kursovaya.model.api.ReviewApi
import com.example.kursovaya.model.api.Specialization

class DoctorsRepository(context: Context) {
    
    init {
        RetrofitClient.init(context)
    }
    
    private val doctorsApi = RetrofitClient.doctorsApi
    
    suspend fun getDoctors(
        query: String? = null,
        limit: Int? = null,
        offset: Int? = null,
        sortBy: String? = null,
        sortOrder: String? = null
    ): Result<List<DoctorApi>> {
        return try {
            val response = doctorsApi.getDoctors(
                query = query,
                limit = limit,
                offset = offset,
                sortBy = sortBy,
                sortOrder = sortOrder
            )
            
            if (response.isSuccessful) {
                val doctors = response.body()
                if (doctors != null) {
                    Result.success(doctors)
                } else {
                    Result.failure(Exception("Пустой ответ от сервера"))
                }
            } else {
                val errorMessage = "Ошибка получения списка врачей: ${response.code()}"
                Log.e("DoctorsRepository", errorMessage)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("DoctorsRepository", "Исключение при получении врачей", e)
            Result.failure(e)
        }
    }
    
    suspend fun getTopDoctorsByRating(limit: Int = 10): Result<List<DoctorApi>> {
        // Сортируем по рейтингу от большего к меньшему
        return getDoctors(
            limit = limit,
            sortBy = "rating",
            sortOrder = "desc"
        )
    }
    
    suspend fun getDoctorById(id: Long): Result<DoctorApi> {
        return try {
            val response = doctorsApi.getDoctorById(id)
            
            if (response.isSuccessful) {
                val doctor = response.body()
                if (doctor != null) {
                    Result.success(doctor)
                } else {
                    Result.failure(Exception("Пустой ответ от сервера"))
                }
            } else {
                val errorMessage = "Ошибка получения врача: ${response.code()}"
                Log.e("DoctorsRepository", errorMessage)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("DoctorsRepository", "Исключение при получении врача", e)
            Result.failure(e)
        }
    }
    
    suspend fun getSpecializations(): Result<List<Specialization>> {
        return try {
            val response = doctorsApi.getSpecializations()
            
            if (response.isSuccessful) {
                val specializations = response.body()
                if (specializations != null) {
                    Result.success(specializations)
                } else {
                    Result.failure(Exception("Пустой ответ от сервера"))
                }
            } else {
                val errorMessage = "Ошибка получения списка специализаций: ${response.code()}"
                Log.e("DoctorsRepository", errorMessage)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("DoctorsRepository", "Исключение при получении специализаций", e)
            Result.failure(e)
        }
    }
    
    suspend fun getDoctorReviews(doctorId: Long): Result<List<ReviewApi>> {
        return try {
            val response = doctorsApi.getDoctorReviews(doctorId)
            
            if (response.isSuccessful) {
                val reviews = response.body()
                if (reviews != null) {
                    Result.success(reviews)
                } else {
                    Result.failure(Exception("Пустой ответ от сервера"))
                }
            } else {
                val errorMessage = "Ошибка получения отзывов: ${response.code()}"
                Log.e("DoctorsRepository", errorMessage)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("DoctorsRepository", "Исключение при получении отзывов", e)
            Result.failure(e)
        }
    }
}

