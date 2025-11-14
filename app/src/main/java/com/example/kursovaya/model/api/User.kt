package com.example.kursovaya.model.api

data class User(
    val id: Int,
    val email: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val createdAt: String,
    val updatedAt: String,
    val active: Boolean
)

