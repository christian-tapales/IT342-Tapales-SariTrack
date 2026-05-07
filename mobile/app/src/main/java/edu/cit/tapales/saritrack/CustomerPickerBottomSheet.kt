package edu.cit.tapales.saritrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerPickerBottomSheet(
    private val onCustomerSelected: (Customer) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var rvPicker: RecyclerView
    private lateinit var adapter: CustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_customer_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPicker = view.findViewById(R.id.rvCustomerPicker)
        rvPicker.layoutManager = LinearLayoutManager(context)
        
        adapter = CustomerAdapter(emptyList()) { customer ->
            onCustomerSelected(customer)
            dismiss()
        }
        rvPicker.adapter = adapter

        fetchCustomers()
    }

    private fun fetchCustomers() {
        val context = context ?: return
        val vendorId = SessionManager(context).getUserId()
        
        RetrofitClient.getCustomerService(context).getCustomers(vendorId)
            .enqueue(object : Callback<List<Customer>> {
                override fun onResponse(call: Call<List<Customer>>, response: Response<List<Customer>>) {
                    if (response.isSuccessful) {
                        adapter.updateCustomers(response.body() ?: emptyList())
                    }
                }

                override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
                    Toast.makeText(context, "Error loading customers", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog
}
