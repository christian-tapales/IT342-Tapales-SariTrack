package edu.cit.tapales.saritrack

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AddProductActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var editingProduct: Product? = null
    private lateinit var ivProductPreview: ImageView
    private lateinit var layoutUploadOverlay: View
    private lateinit var etProductImageUrl: TextInputEditText

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImageUri = result.data?.data
            ivProductPreview.setImageURI(selectedImageUri)
            layoutUploadOverlay.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        // Check if we are in Edit Mode
        val productId = intent.getLongExtra("PRODUCT_ID", -1L)
        val productName = intent.getStringExtra("PRODUCT_NAME")
        val productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0)
        val productStock = intent.getIntExtra("PRODUCT_STOCK", 0)
        val productBarcode = intent.getStringExtra("PRODUCT_BARCODE")
        val productCategory = intent.getStringExtra("PRODUCT_CATEGORY")
        val productImageUrl = intent.getStringExtra("PRODUCT_IMAGE_URL")

        if (productId != -1L) {
            editingProduct = Product(productId, SessionManager(this).getUserId(), productName ?: "", productBarcode ?: "", productPrice, productStock, productCategory ?: "General", productImageUrl)
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarAddProduct)
        toolbar.setNavigationOnClickListener { finish() }
        
        if (editingProduct != null) {
            toolbar.title = "Edit Product"
        }

        val etProductName = findViewById<TextInputEditText>(R.id.etProductName)
        val etProductBarcode = findViewById<TextInputEditText>(R.id.etProductBarcode)
        val etProductPrice = findViewById<TextInputEditText>(R.id.etProductPrice)
        val etProductStock = findViewById<TextInputEditText>(R.id.etProductStock)
        etProductImageUrl = findViewById<TextInputEditText>(R.id.etProductImageUrl)
        ivProductPreview = findViewById(R.id.ivProductPreview)
        layoutUploadOverlay = findViewById(R.id.layoutUploadOverlay)
        val frameImagePicker = findViewById<FrameLayout>(R.id.frameImagePicker)
        val btnSaveProduct = findViewById<MaterialButton>(R.id.btnSaveProduct)
        val btnDeleteProduct = findViewById<MaterialButton>(R.id.btnDeleteProduct)

        // Populate fields if editing
        editingProduct?.let {
            etProductName.setText(it.name)
            etProductBarcode.setText(it.barcode)
            etProductPrice.setText(it.price.toString())
            etProductStock.setText(it.stockQuantity.toString())
            etProductImageUrl.setText(it.imageUrl)
            if (!it.imageUrl.isNullOrEmpty()) {
                Glide.with(this).load(it.imageUrl).into(ivProductPreview)
                layoutUploadOverlay.visibility = View.GONE
            }
            btnDeleteProduct.visibility = View.VISIBLE
            btnSaveProduct.text = "Update Product"
        }

        frameImagePicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        btnDeleteProduct.setOnClickListener {
            val vendorId = SessionManager(this).getUserId()
            RetrofitClient.getProductService(this).deleteProduct(editingProduct!!.id!!, vendorId)
                .enqueue(object : Callback<okhttp3.ResponseBody> {
                    override fun onResponse(call: Call<okhttp3.ResponseBody>, response: Response<okhttp3.ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddProductActivity, "Product deleted", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                    override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                        Toast.makeText(this@AddProductActivity, "Error deleting: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        val tilBarcode = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilProductBarcode)
        
        tilBarcode.setEndIconOnClickListener {
            // Check Camera Permission first
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openScannerBottomSheet(etProductBarcode, etProductName)
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }

        btnSaveProduct.setOnClickListener {
            val name = etProductName.text.toString().trim()
            val barcode = etProductBarcode.text.toString().trim()
            val priceStr = etProductPrice.text.toString().trim()
            val stockStr = etProductStock.text.toString().trim()
            val imageUrlManual = etProductImageUrl.text.toString().trim()

            if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSaveProduct.isEnabled = false
            btnSaveProduct.text = "Processing..."

            if (selectedImageUri != null) {
                uploadImageToSupabase(selectedImageUri!!) { uploadedUrl ->
                    saveProductToBackend(name, barcode, priceStr, stockStr, uploadedUrl ?: imageUrlManual)
                }
            } else {
                saveProductToBackend(name, barcode, priceStr, stockStr, imageUrlManual.ifEmpty { editingProduct?.imageUrl })
            }
        }
    }

    private fun saveProductToBackend(name: String, barcode: String, priceStr: String, stockStr: String, imageUrl: String?) {
        val price = priceStr.toDoubleOrNull() ?: 0.0
        val stock = stockStr.toIntOrNull() ?: 0
        val vendorId = SessionManager(this).getUserId()
        val btnSaveProduct = findViewById<MaterialButton>(R.id.btnSaveProduct)

        val product = Product(
            id = editingProduct?.id,
            vendorId = vendorId,
            name = name,
            barcode = barcode,
            price = price,
            stockQuantity = stock,
            category = "General",
            imageUrl = imageUrl
        )

        val service = RetrofitClient.getProductService(this)
        val call = if (editingProduct == null) {
            service.addProduct(product)
        } else {
            service.updateProduct(editingProduct!!.id!!, product, vendorId)
        }

        call.enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                btnSaveProduct.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(this@AddProductActivity, "Product saved!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@AddProductActivity, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                btnSaveProduct.isEnabled = true
                Toast.makeText(this@AddProductActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Permission granted! Click the scanner again.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Camera permission is required for scanning.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openScannerBottomSheet(etBarcode: TextInputEditText, etName: TextInputEditText) {
        val scanner = ScannerBottomSheet { code ->
            etBarcode.setText(code)
            // If Name is empty, try to lookup
            if (etName.text.isNullOrBlank()) {
                lookupProductName(code, etName)
            }
        }
        scanner.show(supportFragmentManager, "ScannerBottomSheet")
    }

    private fun lookupProductName(barcode: String, etName: TextInputEditText) {
        RetrofitClient.getProductService(this).lookupProduct(barcode)
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    if (response.isSuccessful) {
                        val name = response.body()?.get("productName")
                        if (!name.isNullOrEmpty() && name != "Product Not Found") {
                            etName.setText(name)
                            Toast.makeText(this@AddProductActivity, "Auto-filled name: $name", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    // Fail silently for lookup
                }
            })
    }

    private fun uploadImageToSupabase(uri: Uri, callback: (String?) -> Unit) {
        val fileName = "product_${System.currentTimeMillis()}.jpg"
        val supabaseUrl = "https://eppmobyckusswlunzxdq.supabase.co/storage/v1/object/product-images/$fileName"
        val anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVwcG1vYnlja3Vzc3dsdW56eGRxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzMwMDY4MTgsImV4cCI6MjA4ODU4MjgxOH0.lRWT0rXR5B32FmchVKN5j8Rcr27lIzFCBKKTCgP1xYE"

        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return callback(null)

        val client = OkHttpClient()
        val mediaType = MediaType.parse("image/jpeg")
        val requestBody = RequestBody.create(mediaType, bytes)
        
        val request = Request.Builder()
            .url(supabaseUrl)
            .header("Authorization", "Bearer $anonKey")
            .header("apikey", anonKey)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread { callback(null) }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val publicUrl = "https://eppmobyckusswlunzxdq.supabase.co/storage/v1/object/public/product-images/$fileName"
                    runOnUiThread { callback(publicUrl) }
                } else {
                    runOnUiThread { callback(null) }
                }
            }
        })
    }
}
