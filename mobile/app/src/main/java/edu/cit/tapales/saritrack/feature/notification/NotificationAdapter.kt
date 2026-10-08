package edu.cit.tapales.saritrack.feature.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.cit.tapales.saritrack.R

class NotificationAdapter(
    private var items: List<NotificationItem>,
    private val onItemClick: (NotificationItem) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    fun updateData(newItems: List<NotificationItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onItemClick)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivNotificationIcon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvNotificationTitle)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvNotificationTime)
        private val unreadDot: View = itemView.findViewById(R.id.viewUnreadDot)
        private val card: View = itemView.findViewById(R.id.cardNotification)

        fun bind(item: NotificationItem, onItemClick: (NotificationItem) -> Unit) {
            val context = itemView.context
            tvTitle.text = item.title
            tvMessage.text = item.message

            // Format time display
            tvTime.text = formatTimestamp(item.timestamp)

            // Unread indicator
            unreadDot.visibility = if (item.isRead) View.GONE else View.VISIBLE

            // Type styling
            when (item.type?.uppercase()) {
                "WARNING" -> {
                    ivIcon.setBackgroundResource(R.drawable.bg_circle_orange)
                    ivIcon.setImageResource(R.drawable.ic_bell)
                    ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary_orange))
                }
                "SUCCESS" -> {
                    ivIcon.setBackgroundResource(R.drawable.bg_circle_teal)
                    ivIcon.setImageResource(R.drawable.ic_success)
                    ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary_teal))
                }
                else -> {
                    ivIcon.setBackgroundResource(R.drawable.bg_circle_blue)
                    ivIcon.setImageResource(R.drawable.ic_bell)
                    ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary_blue))
                }
            }

            // Alpha/opacity styling for read vs unread
            card.alpha = if (item.isRead) 0.75f else 1.0f

            card.setOnClickListener {
                onItemClick(item)
            }
        }

        private fun formatTimestamp(timestamp: String?): String {
            if (timestamp.isNullOrBlank()) return "Recent"
            return try {
                if (timestamp.contains("T")) {
                    val parts = timestamp.split("T")
                    val date = parts[0]
                    val time = parts[1].substringBefore(".")
                    "$date $time"
                } else {
                    timestamp
                }
            } catch (e: Exception) {
                "Recent"
            }
        }
    }
}
