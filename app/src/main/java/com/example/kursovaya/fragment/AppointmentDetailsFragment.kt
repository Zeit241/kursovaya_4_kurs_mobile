package com.example.kursovaya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kursovaya.R
import com.example.kursovaya.model.Appointment
import com.example.kursovaya.model.AppointmentStatus
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AppointmentDetailsFragment : Fragment() {

    private val args: AppointmentDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appointment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appointment = args.appointment

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        bindAppointmentData(view, appointment)
    }

    private fun bindAppointmentData(view: View, appointment: Appointment) {
        val doctorImageView: ImageView = view.findViewById(R.id.doctorImageView)
        val doctorNameTextView: TextView = view.findViewById(R.id.doctorNameTextView)
        val doctorSpecialtyTextView: TextView = view.findViewById(R.id.doctorSpecialtyTextView)
        val doctorRatingBar: RatingBar = view.findViewById(R.id.doctorRatingBar)
        val doctorPhoneTextView: TextView = view.findViewById(R.id.doctorPhoneTextView)
        val doctorEmailTextView: TextView = view.findViewById(R.id.doctorEmailTextView)
        val appointmentDateTextView: TextView = view.findViewById(R.id.appointmentDateTextView)
        val appointmentTimeTextView: TextView = view.findViewById(R.id.appointmentTimeTextView)
        val appointmentLocationTextView: TextView = view.findViewById(R.id.appointmentLocationTextView)
        val appointmentRoomTextView: TextView = view.findViewById(R.id.appointmentRoomTextView)
        val appointmentFeeTextView: TextView = view.findViewById(R.id.appointmentFeeTextView)
        val symptomsTextView: TextView = view.findViewById(R.id.symptomsTextView)
        val diagnosisLayout: LinearLayout = view.findViewById(R.id.diagnosisLayout)
        val diagnosisTextView: TextView = view.findViewById(R.id.diagnosisTextView)
        val prescriptionLayout: LinearLayout = view.findViewById(R.id.prescriptionLayout)
        val prescriptionTextView: TextView = view.findViewById(R.id.prescriptionTextView)
        val notesTextView: TextView = view.findViewById(R.id.notesTextView)
        val upcomingButtonsLayout: LinearLayout = view.findViewById(R.id.upcomingButtonsLayout)
        val rateAndReviewButton: Button = view.findViewById(R.id.rateAndReviewButton)

        // Bind data
        doctorNameTextView.text = appointment.doctorName
        doctorSpecialtyTextView.text = appointment.specialty
        doctorRatingBar.rating = 4.5f // Dummy data
        doctorPhoneTextView.text = appointment.phone
        doctorEmailTextView.text = appointment.email
        appointmentDateTextView.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(appointment.date)
        appointmentTimeTextView.text = appointment.time
        appointmentLocationTextView.text = appointment.location
        appointmentRoomTextView.text = "Floor ${appointment.floor}, Room ${appointment.room}"
        appointmentFeeTextView.text = appointment.fee
        symptomsTextView.text = appointment.symptoms
        notesTextView.text = appointment.notes

        if (appointment.diagnosis != null) {
            diagnosisLayout.visibility = View.VISIBLE
            diagnosisTextView.text = appointment.diagnosis
        } else {
            diagnosisLayout.visibility = View.GONE
        }

        if (appointment.prescription != null) {
            prescriptionLayout.visibility = View.VISIBLE
            prescriptionTextView.text = appointment.prescription
        } else {
            prescriptionLayout.visibility = View.GONE
        }

        when (appointment.status) {
            AppointmentStatus.UPCOMING -> upcomingButtonsLayout.visibility = View.VISIBLE
            AppointmentStatus.COMPLETED -> rateAndReviewButton.visibility = View.VISIBLE
            else -> {}
        }

        rateAndReviewButton.setOnClickListener {
            showReviewDrawer(appointment)
        }
    }

    private fun showReviewDrawer(appointment: Appointment) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.drawer_review, null)
        bottomSheetDialog.setContentView(view)

        val doctorNameTextView: TextView = view.findViewById(R.id.doctorNameTextView)
        val doctorSpecialtyTextView: TextView = view.findViewById(R.id.doctorSpecialtyTextView)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val commentEditText: TextInputEditText = view.findViewById(R.id.commentEditText)
        val submitButton: Button = view.findViewById(R.id.submitReviewButton)

        doctorNameTextView.text = appointment.doctorName
        doctorSpecialtyTextView.text = appointment.specialty

        submitButton.setOnClickListener {
            val rating = ratingBar.rating
            val comment = commentEditText.text.toString()
            // Handle review submission
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }
}
