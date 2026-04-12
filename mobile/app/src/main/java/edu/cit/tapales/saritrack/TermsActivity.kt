package edu.cit.tapales.saritrack

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException

class TermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val tvTermsContent = findViewById<TextView>(R.id.tvTermsContent)

        // Reading the file from assets
        try {
            val inputStream = assets.open("terms.txt")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            val content = String(buffer)
            tvTermsContent.text = content
        } catch (e: IOException) {
            tvTermsContent.text = "Error loading terms."
        }
    }
}