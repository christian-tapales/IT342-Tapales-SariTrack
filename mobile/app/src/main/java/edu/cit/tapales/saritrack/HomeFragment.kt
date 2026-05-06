package edu.cit.tapales.saritrack

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.LinearProgressIndicator

class HomeFragment : Fragment() {

    private var tvGreeting: TextView? = null
    private var tvTodaySales: TextView? = null
    private var tvOrders: TextView? = null
    private var tvTotalOutstanding: TextView? = null
    private var progressCollection: LinearProgressIndicator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        tvGreeting = view.findViewById(R.id.tvGreeting)
        tvTodaySales = view.findViewById(R.id.tvTodaySales)
        tvOrders = view.findViewById(R.id.tvOrders)
        tvTotalOutstanding = view.findViewById(R.id.tvTotalOutstanding)
        progressCollection = view.findViewById(R.id.progressCollection)
        
        val btnLogout: View? = view.findViewById(R.id.btnLogout)
        val btnThemeToggle: View? = view.findViewById(R.id.btnThemeToggle)
        
        val context = context ?: return view
        val sessionManager = SessionManager(context)
        val name = sessionManager.getUserName() ?: "Vendor"
        
        tvGreeting?.text = "Kumusta, $name!"
        
        btnLogout?.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // 🌗 Theme Toggle Logic
        btnThemeToggle?.setOnClickListener {
            val isCurrentlyDark = sessionManager.isDarkMode()
            val newMode = !isCurrentlyDark
            
            sessionManager.saveTheme(newMode)
            
            if (newMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tvGreeting = null
        tvTodaySales = null
        tvOrders = null
        tvTotalOutstanding = null
        progressCollection = null
    }
}
