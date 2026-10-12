package com.example.myfastfood

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myfastfood.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        observeViewModel()
        setupBackHandler()
    }

    private fun setupNavigation() {
        binding.btnNavMainMeal.setOnClickListener { navigateToMainMeal() }
        binding.btnNavSideDish.setOnClickListener { navigateToSideDish() }
        binding.btnNavDrink.setOnClickListener { navigateToDrink() }
        binding.btnNavConfirm.setOnClickListener { navigateToConfirm() }
    }

    fun navigateToMainMeal() { showFragment(MainMealFragment()) }
    fun navigateToSideDish() { showFragment(SideDishFragment()) }
    fun navigateToDrink() { showFragment(DrinkFragment()) }
    fun navigateToConfirm() { showFragment(ConfirmFragment()) }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.fragmentContainer.visibility == View.VISIBLE) {
                    closeFragment()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun observeViewModel() {
        // Observe current unsubmitted selections
        orderViewModel.selectedMainMeal.observe(this) { updateCurrentSummary() }
        orderViewModel.selectedSideDishes.observe(this) { updateCurrentSummary() }
        orderViewModel.selectedDrink.observe(this) { updateCurrentSummary() }

        // Observe submitted order results
        orderViewModel.submittedOrder.observe(this) { order ->
            if (order != null && order.isSubmitted) {
                val main = order.mainMeal ?: "None"
                val sides = if (order.sideDishes.isEmpty()) "None" else order.sideDishes.joinToString(", ")
                val drink = order.drink ?: "None"
                binding.tvMainSubmittedSummary.text = "Main: $main\nSides: $sides\nDrink: $drink"
            } else {
                binding.tvMainSubmittedSummary.text = "No order submitted yet."
            }
        }
    }

    private fun updateCurrentSummary() {
        binding.tvMainCurrentSummary.text = orderViewModel.getFormattedCurrentOrderSummary()
    }

    fun showFragment(fragment: Fragment) {
        binding.fragmentContainer.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun closeFragment() {
        binding.fragmentContainer.visibility = View.GONE
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(currentFragment)
                .commit()
        }
    }
}
