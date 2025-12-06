package com.example.kursovaya.api

import com.example.kursovaya.model.api.ApiResponse
import com.example.kursovaya.model.api.PatientInfo
import com.example.kursovaya.model.api.UpdatePatientRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface PatientApi {
    
    @PUT("api/patients/{id}")
    suspend fun updatePatient(
        @Path("id") id: Long,
        @Body request: UpdatePatientRequest
    ): Response<ApiResponse<PatientInfo>>
}

