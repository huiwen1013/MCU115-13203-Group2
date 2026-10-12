package com.example.myfastfood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myfastfood.databinding.FragmentMainMealBinding

class MainMealFragment : Fragment() {

    private var _binding: FragmentMainMealBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe current main meal selection from ViewModel to restore selection
        orderViewModel.selectedMainMeal.observe(viewLifecycleOwner) { mainMeal ->
            updateUi(mainMeal)
        }

        // Handle RadioGroup selection
        binding.rgMainMeals.setOnCheckedChangeListener { _, checkedId ->
            val selectedMeal = when (checkedId) {
                R.id.rb_beef_burger -> "Beef Burger"
                R.id.rb_chicken_burger -> "Chicken Burger"
                R.id.rb_fish_burger -> "Fish Burger"
                else -> null
            }
            if (selectedMeal != null) {
                orderViewModel.setMainMeal(selectedMeal)
            }
        }

        // Next button -> navigate directly to Side Dish Fragment
        binding.btnNextMainMeal.setOnClickListener {
            (activity as? MainActivity)?.navigateToSideDish()
        }

        // Back to Home button
        binding.btnBackMainMeal.setOnClickListener {
            (activity as? MainActivity)?.closeFragment()
        }
    }

    private fun updateUi(mainMeal: String?) {
        when (mainMeal) {
            "Beef Burger" -> if (binding.rgMainMeals.checkedRadioButtonId != R.id.rb_beef_burger) binding.rbBeefBurger.isChecked = true
            "Chicken Burger" -> if (binding.rgMainMeals.checkedRadioButtonId != R.id.rb_chicken_burger) binding.rbChickenBurger.isChecked = true
            "Fish Burger" -> if (binding.rgMainMeals.checkedRadioButtonId != R.id.rb_fish_burger) binding.rbFishBurger.isChecked = true
            else -> binding.rgMainMeals.clearCheck()
        }

        binding.tvSelectedMainMeal.text = if (mainMeal != null) {
            "Selected Main Meal: $mainMeal"
        } else {
            "Selected Main Meal: None"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
