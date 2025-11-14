package com.example.kursovaya.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kursovaya.R
import com.example.kursovaya.activity.DoctorDetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateSpecialties(view)
        populateDoctorsList(view)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)

        view.findViewById<MaterialCardView>(R.id.cardBookAppointment).setOnClickListener {
            bottomNav.selectedItemId = R.id.nav_doctors
        }

        view.findViewById<MaterialCardView>(R.id.cardQueueStatus).setOnClickListener {
            bottomNav.selectedItemId = R.id.nav_queue
        }

        view.findViewById<TextView>(R.id.textViewViewAllDoctors).setOnClickListener {
            bottomNav.selectedItemId = R.id.nav_doctors
        }


        // TODO: Add navigation for All Appointments
    }

    private data class Doctor(
        val id: String,
        val name: String,
        val specialty: String,
        val rating: Double,
        val reviews: Int
    )

    private fun populateSpecialties(view: View) {
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupSpecialties)
        val specialties = listOf(
            "Кардиология", "Педиатрия", "Дерматология", "Неврология", "Ортопедия"
        )

        chipGroup.removeAllViews()

        for (specialty in specialties) {
            val chip = Chip(requireContext()).apply {
                text = specialty
                isClickable = true
                isCheckable = true // Optional: if you want toggle behavior
                // You can set a style from your theme if you have one
                // setChipStyle(R.style.Widget_MaterialComponents_Chip_Suggestion)
            }
//            chip.setOnClickListener {
//                val mapped = mapSpecialtyForDoctors(specialty)
//                val action = HomeFragmentDirections.actionNavHomeToDoctorsFragment(mapped)
//                findNavController().navigate(action)
//                // Синхронизируем выделение нижнего меню со сменой вкладки
//                val bottomNav = requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
//                bottomNav?.selectedItemId = R.id.nav_doctors
//            }
            chipGroup.addView(chip)
        }
    }

    private fun mapSpecialtyForDoctors(source: String): String {
        return when (source) {
            "Кардиология" -> "Кардиолог"
            "Педиатрия" -> "Педиатр"
            "Дерматология" -> "Дерматолог"
            "Неврология" -> "Невролог"
            "Ортопедия" -> "Ортопед"
            else -> source
        }
    }

    private fun populateDoctorsList(view: View) {
        val doctorsContainer = view.findViewById<LinearLayout>(R.id.containerDoctors)
        val inflater = LayoutInflater.from(requireContext())

        val doctors = listOf(
            Doctor("1", "Dr. Sarah Johnson", "Cardiologist", 4.9, 127),
            Doctor("2", "Д-р. Михаил Чен", "Невролог", 4.9, 127),
            Doctor("3", "Д-р. Эмили Родригез", "Педиатр", 4.8, 98),
            Doctor("4", "Д-р. Джеймс Уилсон", "Ортопед", 4.7, 84)
        )

        doctorsContainer.removeAllViews()

        for (doctor in doctors) {
            val doctorCardView =
                inflater.inflate(R.layout.list_item_doctor, doctorsContainer, false)

            val nameTextView = doctorCardView.findViewById<TextView>(R.id.doctorNameTextView)
            val specialtyTextView =
                doctorCardView.findViewById<TextView>(R.id.doctorSpecialtyTextView)
            val ratingTextView = doctorCardView.findViewById<TextView>(R.id.doctorRatingTextView)
            val reviewsTextView = doctorCardView.findViewById<TextView>(R.id.doctorReviewsTextView)
            val bookButton = doctorCardView.findViewById<Button>(R.id.bookButton)
            val doctorImageView = doctorCardView.findViewById<ImageView>(R.id.doctorImageView)

            nameTextView.text = doctor.name
            specialtyTextView.text = doctor.specialty
            ratingTextView.text = doctor.rating.toString()
            reviewsTextView.text = "(${doctor.reviews})"

            // Устанавливаем изображение. В реальности здесь будет загрузка по URL
            // doctorImageView.setImageResource(R.drawable.placeholder_doctor)

            doctorCardView.setOnClickListener {
                val intent = Intent(requireContext(), DoctorDetailActivity::class.java)
                intent.putExtra("DOCTOR_ID", doctor.id)
                startActivity(intent)
            }



            doctorsContainer.addView(doctorCardView)
        }
    }
}
