package com.example.kursovaya.fragment

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kursovaya.adapter.ReviewsAdapter
import com.example.kursovaya.databinding.FragmentReviewsBinding
import com.example.kursovaya.model.DoctorProfile
import com.example.kursovaya.model.Education
import com.example.kursovaya.model.Review
import kotlin.collections.get

class ReviewsFragment : Fragment() {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var reviewsAdapter: ReviewsAdapter

    // В реальном приложении эти данные будут приходить из репозитория или ViewModel
    private val mockDoctorData = mapOf(
        "1" to DoctorProfile(
            id = "1",
            name = "Dr. Sarah Johnson",
            specialty = "Cardiologist",
            rating = 4.9,
            reviewCount = 127,
            experience = "15 years",
            location = "Downtown Medical Center",
            availability = "Available Today",
            image = "placeholder_doctor",
            consultationFee = "$150",
            about = "Dr. Sarah Johnson is a board-certified cardiologist...",
            education = listOf(
                Education("MD in Cardiology", "Harvard Medical School", "2008"),
                Education("MBBS", "Johns Hopkins University", "2004")
            ),
            reviews = listOf(
                Review(
                    "John Smith",
                    5,
                    "2 weeks ago",
                    "Dr. Johnson is amazing! She took the time to explain everything and made me feel comfortable."
                ),
                Review(
                    "Maria Garcia",
                    5,
                    "1 month ago",
                    "Very professional and knowledgeable. Highly recommend!"
                ),
                Review(
                    "David Lee",
                    4,
                    "2 months ago",
                    "Great doctor, but the wait time was a bit long."
                )
            )
        )
        // Сюда можно добавить других докторов по их ID
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Получаем ID доктора, переданный из Activity
        val doctorId = arguments?.getString("DOCTOR_ID")
        val doctor = mockDoctorData[doctorId] ?: mockDoctorData.values.first()

        // Передаем список отзывов в адаптер
        reviewsAdapter.submitList(doctor.reviews)
    }

    private fun setupRecyclerView() {
        reviewsAdapter = ReviewsAdapter()
        binding.reviewsRecyclerView.adapter = reviewsAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
