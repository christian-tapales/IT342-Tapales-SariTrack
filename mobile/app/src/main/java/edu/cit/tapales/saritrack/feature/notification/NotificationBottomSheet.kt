package edu.cit.tapales.saritrack.feature.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import edu.cit.tapales.saritrack.R
import edu.cit.tapales.saritrack.core.api.RetrofitClient
import edu.cit.tapales.saritrack.core.auth.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationBottomSheet(
    private val onUnreadCountChanged: ((Int) -> Unit)? = null
) : BottomSheetDialogFragment() {

    private var adapter: NotificationAdapter? = null
    private var notificationsList: MutableList<NotificationItem> = mutableListOf()

    private var tvUnreadCountBadge: TextView? = null
    private var btnMarkAllRead: TextView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var pbLoading: ProgressBar? = null
    private var layoutEmptyState: View? = null
    private var rvNotifications: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_notifications, container, false)

        tvUnreadCountBadge = view.findViewById(R.id.tvUnreadCountBadge)
        btnMarkAllRead = view.findViewById(R.id.btnMarkAllRead)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        pbLoading = view.findViewById(R.id.pbLoading)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        rvNotifications = view.findViewById(R.id.rvNotifications)

        rvNotifications?.layoutManager = LinearLayoutManager(context)
        adapter = NotificationAdapter(notificationsList) { item ->
            if (!item.isRead) {
                markNotificationAsRead(item)
            }
        }
        rvNotifications?.adapter = adapter

        swipeRefreshLayout?.setColorSchemeResources(R.color.primary_teal)
        swipeRefreshLayout?.setOnRefreshListener {
            loadNotifications(sync = true)
        }

        btnMarkAllRead?.setOnClickListener {
            markAllAsRead()
        }

        loadNotifications(sync = true)

        return view
    }

    private fun loadNotifications(sync: Boolean) {
        val context = context ?: return
        val vendorId = SessionManager(context).getUserId()
        if (vendorId <= 0) return

        if (!swipeRefreshLayout?.isRefreshing!!) {
            pbLoading?.visibility = View.VISIBLE
        }

        val service = RetrofitClient.getNotificationService(context)

        if (sync) {
            service.syncNotifications(vendorId).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    fetchNotificationsFromApi(vendorId)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    fetchNotificationsFromApi(vendorId)
                }
            })
        } else {
            fetchNotificationsFromApi(vendorId)
        }
    }

    private fun fetchNotificationsFromApi(vendorId: Long) {
        val context = context ?: return
        val service = RetrofitClient.getNotificationService(context)

        service.getNotifications(vendorId).enqueue(object : Callback<List<NotificationItem>> {
            override fun onResponse(
                call: Call<List<NotificationItem>>,
                response: Response<List<NotificationItem>>
            ) {
                pbLoading?.visibility = View.GONE
                swipeRefreshLayout?.isRefreshing = false

                if (response.isSuccessful && response.body() != null) {
                    notificationsList.clear()
                    notificationsList.addAll(response.body()!!)
                    adapter?.updateData(notificationsList)
                    updateUI()
                } else {
                    updateUI()
                }
            }

            override fun onFailure(call: Call<List<NotificationItem>>, t: Throwable) {
                pbLoading?.visibility = View.GONE
                swipeRefreshLayout?.isRefreshing = false
                updateUI()
            }
        })
    }

    private fun markNotificationAsRead(item: NotificationItem) {
        val context = context ?: return
        val service = RetrofitClient.getNotificationService(context)

        service.markAsRead(item.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                val index = notificationsList.indexOfFirst { it.id == item.id }
                if (index != -1) {
                    notificationsList[index] = notificationsList[index].copy(isRead = true)
                    adapter?.updateData(notificationsList)
                    updateUI()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Silently keep current state or toast
            }
        })
    }

    private fun markAllAsRead() {
        val context = context ?: return
        val vendorId = SessionManager(context).getUserId()
        if (vendorId <= 0) return

        pbLoading?.visibility = View.VISIBLE
        RetrofitClient.getNotificationService(context).markAllAsRead(vendorId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    pbLoading?.visibility = View.GONE
                    notificationsList = notificationsList.map { it.copy(isRead = true) }.toMutableList()
                    adapter?.updateData(notificationsList)
                    updateUI()
                    Toast.makeText(context, "All notifications marked as read", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    pbLoading?.visibility = View.GONE
                    Toast.makeText(context, "Failed to mark all as read", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUI() {
        val unreadCount = notificationsList.count { !it.isRead }

        if (unreadCount > 0) {
            tvUnreadCountBadge?.visibility = View.VISIBLE
            tvUnreadCountBadge?.text = "$unreadCount new"
        } else {
            tvUnreadCountBadge?.visibility = View.GONE
        }

        onUnreadCountChanged?.invoke(unreadCount)

        if (notificationsList.isEmpty()) {
            layoutEmptyState?.visibility = View.VISIBLE
            rvNotifications?.visibility = View.GONE
        } else {
            layoutEmptyState?.visibility = View.GONE
            rvNotifications?.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tvUnreadCountBadge = null
        btnMarkAllRead = null
        swipeRefreshLayout = null
        pbLoading = null
        layoutEmptyState = null
        rvNotifications = null
    }

    companion object {
        const val TAG = "NotificationBottomSheet"
        fun newInstance(onUnreadCountChanged: ((Int) -> Unit)? = null): NotificationBottomSheet {
            return NotificationBottomSheet(onUnreadCountChanged)
        }
    }
}
