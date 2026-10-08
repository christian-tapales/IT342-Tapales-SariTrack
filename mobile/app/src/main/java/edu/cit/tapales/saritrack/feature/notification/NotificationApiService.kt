package edu.cit.tapales.saritrack.feature.notification

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {
    @GET("/api/notifications")
    fun getNotifications(@Query("vendorId") vendorId: Long): Call<List<NotificationItem>>

    @POST("/api/notifications/sync")
    fun syncNotifications(@Query("vendorId") vendorId: Long): Call<Void>

    @POST("/api/notifications/{id}/read")
    fun markAsRead(@Path("id") id: Long): Call<Void>

    @POST("/api/notifications/read-all")
    fun markAllAsRead(@Query("vendorId") vendorId: Long): Call<Void>
}
