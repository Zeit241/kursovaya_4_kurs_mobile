package com.example.kursovaya.repository

import android.content.Context
import android.util.Log
import com.example.kursovaya.api.PatientApi
import com.example.kursovaya.api.RetrofitClient
import com.example.kursovaya.model.api.PatientInfo
import com.example.kursovaya.model.api.UpdatePatientRequest

class PatientRepository(context: Context) {
    
    init {
        RetrofitClient.init(context)
    }
    
    private val patientApi: PatientApi
        get() = RetrofitClient.patientApi
    
    private val userRepository = UserRepository(context)
    
    suspend fun updatePatient(patientId: Long, request: UpdatePatientRequest): Result<PatientInfo> {
        return try {
            Log.d("PatientRepository", "Обновление данных пациента $patientId...")
            val response = patientApi.updatePatient(patientId, request)
            
            Log.d("PatientRepository", "Update Response code: ${response.code()}")
            Log.d("PatientRepository", "Update Response isSuccessful: ${response.isSuccessful}")
            Log.d("PatientRepository", "Update Response body: ${response.body()}")
            
            // Код 200 означает успех
            if (response.code() == 200 || (response.isSuccessful && response.body()?.isSuccessful() == true)) {
                val patientInfo = response.body()?.data
                if (patientInfo != null) {
                    Log.d("PatientRepository", "Данные пациента обновлены успешно")
                    // Перезагружаем данные пользователя с сервера для обновления кэша
                    userRepository.getCurrentUser()
                    Result.success(patientInfo)
                } else {
                    // Даже если data пустой, но код 200 - считаем успехом и перезагружаем данные
                    Log.d("PatientRepository", "Ответ успешен, но data пустой. Перезагружаем данные пользователя...")
                    userRepository.getCurrentUser()
                    Result.success(PatientInfo(0, null, null, null, "", ""))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("PatientRepository", "Error body (update): $errorBody")
                val errorMessage = response.body()?.message ?: errorBody ?: "Ошибка обновления данных пациента: ${response.code()}"
                Log.e("PatientRepository", errorMessage)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("PatientRepository", "Исключение при обновлении данных пациента", e)
            Result.failure(e)
        }
    }
}
