package com.example.kursovaya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kursovaya.R
import com.example.kursovaya.adapter.ProfileViewPagerAdapter
import com.example.kursovaya.databinding.FragmentDoctorProfileBinding
import com.example.kursovaya.model.DoctorProfile
import com.example.kursovaya.model.Education
import com.example.kursovaya.model.Review
import com.google.android.material.tabs.TabLayoutMediator

class DoctorProfileFragment : Fragment() {

    private var _binding: FragmentDoctorProfileBinding? = null
    private val binding get() = _binding!!

    private val mockDoctorData = mapOf(
        "1" to DoctorProfile(
            id = "1",
            name = "Dr. Sarah Johnson",
            specialty = "Cardiologist",
            rating = 4.9,
            education = listOf(
                Education("MD in Cardiology", "Harvard Medical School", "2008"),
                Education("MBBS", "Johns Hopkins University", "2004")
            ),
            reviews = listOf(
                Review(
                    authorName = "John Doe",
                    rating = 5,
                    relativeTimeDescription = "1 week ago",
                    text = "Excellent and caring doctor. Explained everything clearly."
                ),
                Review(
                    authorName = "Maria Garcia",
                    rating = 5,
                    relativeTimeDescription = "1 month ago",
                    text = "Very professional and knowledgeable. Highly recommend!"
                ),
                Review(
                    authorName = "David Lee",
                    rating = 4,
                    relativeTimeDescription = "2 months ago",
                    text = "Great doctor, but the wait time was a bit long."
                )
            ),
            reviewCount = 3, 
            experience = "15 years", 
            location = "Central Clinic, Springfield", 
            availability = "Mon-Fri", 
            image = "doctor_image_placeholder",
            consultationFee = "150", 
            about = "Dr. Sarah Johnson is a renowned cardiologist with over 15 years of experience..."
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        val doctorId = arguments?.getString("DOCTOR_ID")
        val adapter = ProfileViewPagerAdapter(this, doctorId)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "О враче"
                1 -> "Расписание"
                2 -> "Отзывы"
                else -> null
            }
        }.attach()

        loadDoctorData(doctorId)

        binding.bookAppointmentButton.setOnClickListener {
            val bundle = bundleOf("DOCTOR_ID" to doctorId)
            findNavController().navigate(R.id.action_doctorProfileFragment_to_bookingFragment, bundle)
        }
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
    }

    private fun loadDoctorData(doctorId: String?) {
        val doctor = mockDoctorData[doctorId] ?: mockDoctorData.values.first()

        binding.doctorNameTextView.text = doctor.name
        binding.doctorSpecialtyTextView.text = doctor.specialty
        binding.doctorRatingTextView.text = doctor.rating.toString()
        binding.doctorReviewsTextView.text = "(${doctor.reviewCount} отзывов)"

        val imageResId = resources.getIdentifier(doctor.image, "drawable", requireContext().packageName)
        if (imageResId != 0) {
            binding.doctorImageView.setImageResource(imageResId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}