package com.example.kursovaya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.kursovaya.R
import com.example.kursovaya.adapter.AppointmentAdapter
import com.example.kursovaya.model.Appointment
import com.example.kursovaya.model.AppointmentStatus
import com.google.android.material.tabs.TabLayout
import java.util.*

class AppointmentHistoryFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout

    private lateinit var appointmentAdapter: AppointmentAdapter
    private val allAppointments = getDummyAppointments()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appointment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        tabLayout = view.findViewById(R.id.tabLayout)
        appointmentsRecyclerView = view.findViewById(R.id.appointmentsRecyclerView)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)

        setupTabs()
        setupRecyclerView()

        filterAppointments("All")
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"))
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"))
        tabLayout.addTab(tabLayout.newTab().setText("Completed"))
        tabLayout.addTab(tabLayout.newTab().setText("Cancelled"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                filterAppointments(tab?.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        appointmentAdapter = AppointmentAdapter(emptyList()) { appointment ->
            val action = AppointmentHistoryFragmentDirections.actionAppointmentHistoryFragmentToAppointmentDetailsFragment(appointment)
            findNavController().navigate(action)
        }
        appointmentsRecyclerView.adapter = appointmentAdapter
    }

    private fun filterAppointments(status: String) {
        val filteredList = when (status) {
            "Upcoming" -> allAppointments.filter { it.status == AppointmentStatus.UPCOMING }
            "Completed" -> allAppointments.filter { it.status == AppointmentStatus.COMPLETED }
            "Cancelled" -> allAppointments.filter { it.status == AppointmentStatus.CANCELLED }
            else -> allAppointments
        }

        appointmentAdapter.updateAppointments(filteredList)

        if (filteredList.isEmpty()) {
            appointmentsRecyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            appointmentsRecyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
        }
    }

    private fun getDummyAppointments(): List<Appointment> {
        return listOf(
            Appointment("1", "1", "Dr. Sarah Johnson", "Cardiologist", Date(), "10:00 AM", "Downtown Medical Center", "203", 2, AppointmentStatus.UPCOMING, "", "$150", "+1 (555) 123-4567", "s.johnson@clinic.com", "Please arrive 15 minutes early for check-in. Bring your insurance card and ID.", "Chest pain, shortness of breath"),
            Appointment("2", "2", "Dr. Michael Chen", "Neurologist", Date(), "2:30 PM", "City Hospital", "301", 3, AppointmentStatus.UPCOMING, "", "$180", "+1 (555) 765-4321", "m.chen@clinic.com", "", "Headaches, dizziness"),
            Appointment("3", "3", "Dr. Emily Rodriguez", "Pediatrician", Date(), "11:00 AM", "Children's Clinic", "105", 1, AppointmentStatus.COMPLETED, "", "$120", "+1 (555) 234-5678", "e.rodriguez@clinic.com", "Routine checkup completed successfully.", "Annual checkup", "Healthy development, all vitals normal", "Multivitamin supplements recommended"),
            Appointment("4", "4", "Dr. James Wilson", "Orthopedist", Date(), "3:00 PM", "Sports Medicine Center", "410", 4, AppointmentStatus.COMPLETED, "", "$160", "+1 (555) 876-5432", "j.wilson@clinic.com", "", "Knee pain"),
            Appointment("5", "5", "Dr. Lisa Anderson", "Dermatologist", Date(), "9:30 AM", "Skin Care Clinic", "112", 1, AppointmentStatus.CANCELLED, "", "$140", "+1 (555) 345-6789", "l.anderson@clinic.com", "", "Skin rash"),
        )
    }
}
