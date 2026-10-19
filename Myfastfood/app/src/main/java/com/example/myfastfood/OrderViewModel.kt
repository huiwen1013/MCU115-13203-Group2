package com.example.myfastfood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel : ViewModel() {

    private val _selectedMainMeal = MutableLiveData<String?>()
    val selectedMainMeal: LiveData<String?> = _selectedMainMeal

    private val _selectedSideDishes = MutableLiveData<Set<String>>(emptySet())
    val selectedSideDishes: LiveData<Set<String>> = _selectedSideDishes

    private val _selectedDrink = MutableLiveData<String?>()
    val selectedDrink: LiveData<String?> = _selectedDrink

    private val _submittedOrder = MutableLiveData<Order?>()
    val submittedOrder: LiveData<Order?> = _submittedOrder

    fun setMainMeal(meal: String?) {
        _selectedMainMeal.value = meal
    }

    fun toggleSideDish(sideDish: String, isChecked: Boolean) {
        val currentSet = _selectedSideDishes.value.orEmpty().toMutableSet()
        if (isChecked) {
            currentSet.add(sideDish)
        } else {
            currentSet.remove(sideDish)
        }
        _selectedSideDishes.value = currentSet
    }

    fun setDrink(drink: String?) {
        _selectedDrink.value = drink
    }

    fun isOrderComplete(): Boolean {
        val main = _selectedMainMeal.value
        val sides = _selectedSideDishes.value.orEmpty()
        val drink = _selectedDrink.value
        return !main.isNullOrEmpty() && sides.isNotEmpty() && !drink.isNullOrEmpty()
    }

    fun submitOrder(): Boolean {
        val main = _selectedMainMeal.value
        val sides = _selectedSideDishes.value.orEmpty()
        val drink = _selectedDrink.value

        if (main.isNullOrEmpty() || sides.isEmpty() || drink.isNullOrEmpty()) {
            return false
        }

        val order = Order(
            mainMeal = main,
            sideDishes = sides,
            drink = drink,
            isSubmitted = true
        )
        _submittedOrder.value = order
        return true
    }

    fun getFormattedCurrentOrderSummary(): String {
        val main = _selectedMainMeal.value ?: "Not selected"
        val sides = if (_selectedSideDishes.value.isNullOrEmpty()) "None" else _selectedSideDishes.value!!.joinToString(", ")
        val drink = _selectedDrink.value ?: "Not selected"
        return "Main: $main\nSides: $sides\nDrink: $drink"
    }

    fun getFormattedDialogSummary(): String {
        val main = _selectedMainMeal.value ?: ""
        val sides = _selectedSideDishes.value.orEmpty().joinToString(", ")
        val drink = _selectedDrink.value ?: ""
        return "Main: $main\nSides: $sides\nDrink: $drink\n\nSubmit this order?"
    }
}
