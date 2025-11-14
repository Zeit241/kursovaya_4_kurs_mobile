package com.example.kursovaya.model.api

data class DoctorApi(
    val id: Int,
    val user: User,
    val bio: String?,
    val experienceYears: Int,
    val photoUrl: String?,
    val rating: Double?,
    val reviewCount: Int?,
    val specializations: List<Specialization>?,
    val createdAt: String,
    val updatedAt: String
)

