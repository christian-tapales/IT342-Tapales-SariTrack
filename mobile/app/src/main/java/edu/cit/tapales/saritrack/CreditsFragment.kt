package edu.cit.tapales.saritrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreditsFragment : Fragment() {

    private lateinit var rvCustomers: RecyclerView
    private lateinit var adapter: CustomerAdapter
    private lateinit var tvTotalDebt: TextView
    private var allCustomers = listOf<Customer>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_credits, container, false)

        rvCustomers = view.findViewById(R.id.rvCustomers)
        tvTotalDebt = view.findViewById(R.id.tvTotalDebt)

        rvCustomers.layoutManager = LinearLayoutManager(context)
        adapter = CustomerAdapter(
            customers = emptyList(),
            onHistoryClick = { customer ->
                val intent = android.content.Intent(context, CustomerHistoryActivity::class.java)
                intent.putExtra("CUSTOMER_ID", customer.id)
                intent.putExtra("CUSTOMER_NAME", customer.fullName)
                intent.putExtra("CURRENT_DEBT", customer.currentDebt)
                startActivity(intent)
            },
            onPayClick = { customer ->
                val paymentSheet = PaymentBottomSheet(customer) {
                    fetchCustomers()
                }
                paymentSheet.show(parentFragmentManager, "PaymentSheet")
            }
        )
        rvCustomers.adapter = adapter

        fetchCustomers()

        return view
    }

    private fun fetchCustomers() {
        val context = context ?: return
        val vendorId = SessionManager(context).getUserId()
        
        RetrofitClient.getCustomerService(context).getCustomers(vendorId)
            .enqueue(object : Callback<List<Customer>> {
                override fun onResponse(call: Call<List<Customer>>, response: Response<List<Customer>>) {
                    if (response.isSuccessful) {
                        allCustomers = response.body() ?: emptyList()
                        adapter.updateCustomers(allCustomers)
                        
                        val total = allCustomers.sumOf { it.currentDebt }
                        tvTotalDebt.text = "₱${String.format("%.2f", total)}"
                    }
                }

                override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
