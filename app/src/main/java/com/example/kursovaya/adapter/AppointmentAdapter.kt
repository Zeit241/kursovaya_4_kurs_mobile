package com.example.kursovaya.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kursovaya.R
import com.example.kursovaya.model.Appointment
import com.example.kursovaya.model.AppointmentStatus
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

class AppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onAppointmentClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int = appointments.size

    fun updateAppointments(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val doctorImageView: ImageView = itemView.findViewById(R.id.doctorImageView)
        private val doctorNameTextView: TextView = itemView.findViewById(R.id.doctorNameTextView)
        private val doctorSpecialtyTextView: TextView = itemView.findViewById(R.id.doctorSpecialtyTextView)
        private val statusChip: Chip = itemView.findViewById(R.id.statusChip)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        private val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        private val upcomingButtonsLayout: LinearLayout = itemView.findViewById(R.id.upcomingButtonsLayout)
        private val writeReviewButton: Button = itemView.findViewById(R.id.writeReviewButton)

        fun bind(appointment: Appointment) {
            itemView.setOnClickListener { onAppointmentClick(appointment) }

            // Bind data to views
            doctorNameTextView.text = appointment.doctorName
            doctorSpecialtyTextView.text = appointment.specialty
            statusChip.text = appointment.status.value
            dateTextView.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(appointment.date)
            timeTextView.text = appointment.time
            locationTextView.text = appointment.location

            // Set status chip color
            when (appointment.status) {
                AppointmentStatus.UPCOMING -> statusChip.setChipBackgroundColorResource(R.color.upcoming_status)
                AppointmentStatus.COMPLETED -> statusChip.setChipBackgroundColorResource(R.color.completed_status)
                AppointmentStatus.CANCELLED -> statusChip.setChipBackgroundColorResource(R.color.cancelled_status)
            }

            // Show/hide buttons based on status
            upcomingButtonsLayout.visibility = if (appointment.status == AppointmentStatus.UPCOMING) View.VISIBLE else View.GONE
            writeReviewButton.visibility = if (appointment.status == AppointmentStatus.COMPLETED) View.VISIBLE else View.GONE
        }
    }
}
