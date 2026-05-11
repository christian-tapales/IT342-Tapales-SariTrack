package edu.cit.tapales.saritrack.feature.payment

import edu.cit.tapales.saritrack.feature.auth.*
import edu.cit.tapales.saritrack.feature.transaction.*
import edu.cit.tapales.saritrack.feature.customer.*
import edu.cit.tapales.saritrack.core.auth.*
import edu.cit.tapales.saritrack.R
import edu.cit.tapales.saritrack.feature.pos.*
import edu.cit.tapales.saritrack.feature.dashboard.*
import edu.cit.tapales.saritrack.feature.payment.*
import edu.cit.tapales.saritrack.core.ui.*
import edu.cit.tapales.saritrack.feature.inventory.*
import edu.cit.tapales.saritrack.core.api.*

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentBottomSheet(
    private val customer: Customer,
    private val onPaymentSuccess: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_payment, container, false)

        val tvName = view.findViewById<TextView>(R.id.tvSheetCustomerName)
        val tvDebt = view.findViewById<TextView>(R.id.tvSheetCurrentDebt)
        val etAmount = view.findViewById<EditText>(R.id.etPaymentAmount)
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btnConfirmPayment)

        tvName.text = customer.fullName
        tvDebt.text = "Current Debt: ₱${String.format("%.2f", customer.currentDebt)}"

        btnConfirm.setOnClickListener {
            val amountStr = etAmount.text.toString()
            if (amountStr.isBlank()) {
                etAmount.error = "Enter amount"
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull() ?: 0.0
            if (amount <= 0) {
                etAmount.error = "Invalid amount"
                return@setOnClickListener
            }

            confirmPayment(customer.id!!, amount)
        }

        return view
    }

    private fun confirmPayment(customerId: Long, amount: Double) {
        val payload = mapOf("amount" to amount)
        RetrofitClient.getCustomerService(requireContext())
            .recordPayment(customerId, payload)
            .enqueue(object : Callback<Customer> {
                override fun onResponse(call: Call<Customer>, response: Response<Customer>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Payment recorded!", Toast.LENGTH_SHORT).show()
                        onPaymentSuccess()
                        dismiss()
                    } else {
                        Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Customer>, t: Throwable) {
                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog
}
