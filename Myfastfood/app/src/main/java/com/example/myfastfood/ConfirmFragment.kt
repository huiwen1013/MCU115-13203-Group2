package com.example.myfastfood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myfastfood.databinding.FragmentConfirmBinding

class ConfirmFragment : Fragment() {

    private var _binding: FragmentConfirmBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe ViewModel to keep summary display updated
        orderViewModel.selectedMainMeal.observe(viewLifecycleOwner) { updateSummary() }
        orderViewModel.selectedSideDishes.observe(viewLifecycleOwner) { updateSummary() }
        orderViewModel.selectedDrink.observe(viewLifecycleOwner) { updateSummary() }

        // Confirm Order Action
        binding.btnConfirmOrderAction.setOnClickListener {
            handleConfirmOrder()
        }

        // Back to Home button
        binding.btnBackConfirm.setOnClickListener {
            (activity as? MainActivity)?.closeFragment()
        }
    }

    private fun updateSummary() {
        binding.tvConfirmSummary.text = orderViewModel.getFormattedCurrentOrderSummary()
    }

    private fun handleConfirmOrder() {
        if (!orderViewModel.isOrderComplete()) {
            // Requirement B: Toast with LENGTH_LONG if any category is missing
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_incomplete_order),
                Toast.LENGTH_LONG
            ).show()
        } else {
            // Requirement C: Show AlertDialog when all items selected
            val dialogMessage = orderViewModel.getFormattedDialogSummary()

            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_title_submit_order))
                .setMessage(dialogMessage)
                .setPositiveButton(getString(R.string.dialog_btn_submit)) { dialog, _ ->
                    // Requirement E: Submit Order
                    dialog.dismiss()
                    orderViewModel.submitOrder()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_order_submitted),
                        Toast.LENGTH_SHORT
                    ).show()
                    (activity as? MainActivity)?.closeFragment()
                }
                .setNegativeButton(getString(R.string.dialog_btn_cancel)) { dialog, _ ->
                    // Requirement D: Cancel closes dialog without clearing selections
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
