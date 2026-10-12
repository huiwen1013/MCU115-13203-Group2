package com.example.myfastfood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myfastfood.databinding.FragmentSideDishBinding

class SideDishFragment : Fragment() {

    private var _binding: FragmentSideDishBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSideDishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe side dishes from ViewModel to restore checked states
        orderViewModel.selectedSideDishes.observe(viewLifecycleOwner) { sideDishes ->
            updateUi(sideDishes)
        }

        // Set up CheckBox listeners
        binding.cbFrenchFries.setOnCheckedChangeListener { _, isChecked ->
            orderViewModel.toggleSideDish("French Fries", isChecked)
        }

        binding.cbChickenNuggets.setOnCheckedChangeListener { _, isChecked ->
            orderViewModel.toggleSideDish("Chicken Nuggets", isChecked)
        }

        binding.cbOnionRings.setOnCheckedChangeListener { _, isChecked ->
            orderViewModel.toggleSideDish("Onion Rings", isChecked)
        }

        // Next button -> navigate directly to Drink Fragment
        binding.btnNextSideDish.setOnClickListener {
            (activity as? MainActivity)?.navigateToDrink()
        }

        // Back to Home button
        binding.btnBackSideDish.setOnClickListener {
            (activity as? MainActivity)?.closeFragment()
        }
    }

    private fun updateUi(sideDishes: Set<String>) {
        if (binding.cbFrenchFries.isChecked != sideDishes.contains("French Fries")) {
            binding.cbFrenchFries.isChecked = sideDishes.contains("French Fries")
        }
        if (binding.cbChickenNuggets.isChecked != sideDishes.contains("Chicken Nuggets")) {
            binding.cbChickenNuggets.isChecked = sideDishes.contains("Chicken Nuggets")
        }
        if (binding.cbOnionRings.isChecked != sideDishes.contains("Onion Rings")) {
            binding.cbOnionRings.isChecked = sideDishes.contains("Onion Rings")
        }

        binding.tvSelectedSideDishes.text = if (sideDishes.isNotEmpty()) {
            "Selected Side Dishes: ${sideDishes.joinToString(", ")}"
        } else {
            "Selected Side Dishes: None"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
