package com.example.kursovaya.model.api

import com.google.gson.annotations.SerializedName

data class ReviewApi(
    val id: Int,
    val doctor: DoctorApi?,
    val patient: Patient?,
    val rating: Int,
    @SerializedName("reviewText")
    val reviewText: String?,
    val createdAt: String
)

