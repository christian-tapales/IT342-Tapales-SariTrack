package edu.cit.tapales.saritrack

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarAddProduct)
        toolbar.setNavigationOnClickListener { finish() }

        val etProductName = findViewById<TextInputEditText>(R.id.etProductName)
        val etProductBarcode = findViewById<TextInputEditText>(R.id.etProductBarcode)
        val etProductPrice = findViewById<TextInputEditText>(R.id.etProductPrice)
        val etProductStock = findViewById<TextInputEditText>(R.id.etProductStock)
        val btnSaveProduct = findViewById<MaterialButton>(R.id.btnSaveProduct)

        btnSaveProduct.setOnClickListener {
            val name = etProductName.text.toString().trim()
            val barcode = etProductBarcode.text.toString().trim()
            val priceStr = etProductPrice.text.toString().trim()
            val stockStr = etProductStock.text.toString().trim()

            if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull() ?: 0.0
            val stock = stockStr.toIntOrNull() ?: 0
            val vendorId = SessionManager(this).getUserId()

            if (vendorId == -1L) {
                Toast.makeText(this, "Error: Vendor ID not found. Please login again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newProduct = Product(
                id = 0,
                vendorId = vendorId,
                name = name,
                barcode = barcode,
                price = price,
                stockQuantity = stock,
                category = "General", // Default or you can add a field for it later
                imageUrl = null
            )

            btnSaveProduct.isEnabled = false
            btnSaveProduct.text = "Saving..."

            RetrofitClient.getProductService(this).addProduct(newProduct)
                .enqueue(object : Callback<Product> {
                    override fun onResponse(call: Call<Product>, response: Response<Product>) {
                        btnSaveProduct.isEnabled = true
                        btnSaveProduct.text = "Save Product"
                        
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddProductActivity, "Product added successfully!", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@AddProductActivity, "Failed to add product: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Product>, t: Throwable) {
                        btnSaveProduct.isEnabled = true
                        btnSaveProduct.text = "Save Product"
                        Toast.makeText(this@AddProductActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
