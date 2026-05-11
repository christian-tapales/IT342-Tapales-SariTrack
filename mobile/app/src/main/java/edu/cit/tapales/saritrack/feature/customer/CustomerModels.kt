package edu.cit.tapales.saritrack.feature.customer

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

data class Customer(
    val id: Long? = null,
    val vendorId: Long,
    val fullName: String,
    val email: String?,
    val currentDebt: Double = 0.0,
    val status: String = "Unpaid",
    val lastUpdate: String? = null
)

data class DebtPayment(
    val id: Long? = null,
    val customerId: Long,
    val amount: Double,
    val timestamp: String? = null
)
