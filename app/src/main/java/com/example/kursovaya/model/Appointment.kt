package com.example.kursovaya.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Appointment(
    val id: String,
    val doctorId: String,
    val doctorName: String,
    val specialty: String,
    val date: Date,
    val time: String,
    val location: String,
    val room: String,
    val floor: Int,
    val status: AppointmentStatus,
    val image: String,
    val fee: String,
    val phone: String,
    val email: String,
    val notes: String,
    val symptoms: String,
    val diagnosis: String? = null,
    val prescription: String? = null
): Parcelable
