package com.example.myfastfood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myfastfood.databinding.FragmentDrinkBinding

class DrinkFragment : Fragment() {

    private var _binding: FragmentDrinkBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe current drink selection from ViewModel to restore selection
        orderViewModel.selectedDrink.observe(viewLifecycleOwner) { drink ->
            updateUi(drink)
        }

        // Handle RadioGroup selection
        binding.rgDrinks.setOnCheckedChangeListener { _, checkedId ->
            val selectedDrink = when (checkedId) {
                R.id.rb_cola -> "Cola"
                R.id.rb_sprite -> "Sprite"
                R.id.rb_iced_tea -> "Iced Tea"
                else -> null
            }
            if (selectedDrink != null) {
                orderViewModel.setDrink(selectedDrink)
            }
        }

        // Next button -> navigate directly to Confirm Fragment
        binding.btnNextDrink.setOnClickListener {
            (activity as? MainActivity)?.navigateToConfirm()
        }

        // Back to Home button
        binding.btnBackDrink.setOnClickListener {
            (activity as? MainActivity)?.closeFragment()
        }
    }

    private fun updateUi(drink: String?) {
        when (drink) {
            "Cola" -> if (binding.rgDrinks.checkedRadioButtonId != R.id.rb_cola) binding.rbCola.isChecked = true
            "Sprite" -> if (binding.rgDrinks.checkedRadioButtonId != R.id.rb_sprite) binding.rbSprite.isChecked = true
            "Iced Tea" -> if (binding.rgDrinks.checkedRadioButtonId != R.id.rb_iced_tea) binding.rbIcedTea.isChecked = true
            else -> binding.rgDrinks.clearCheck()
        }

        binding.tvSelectedDrink.text = if (drink != null) {
            "Selected Drink: $drink"
        } else {
            "Selected Drink: None"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
