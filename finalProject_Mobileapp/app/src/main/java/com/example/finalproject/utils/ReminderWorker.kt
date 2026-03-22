package com.example.finalproject.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalproject.MainActivity
import com.example.finalproject.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val type = inputData.getString("type") ?: return Result.failure()
        
        val title = when (type) {
            "daily" -> "Đến giờ học rồi! 📚"
            "streak" -> "Bảo vệ Streak của bạn! 🔥"
            "exam" -> "Có kỳ thi sắp tới! 📅"
            else -> "Thông báo học tập"
        }
        
        val message = when (type) {
            "daily" -> "Vào ôn tập lý thuyết ngay để nắm chắc kiến thức nhé."
            "streak" -> "Bạn chưa hoàn thành bài học hôm nay. Đừng để mất chuỗi ngày chăm chỉ!"
            "exam" -> "Nhắc nhở bạn lịch thi đang đến rất gần. Chuẩn bị thật tốt nhé!"
            else -> "Vào ứng dụng để tiếp tục học hành."
        }

        showNotification(context, title, message, type)

        return Result.success()
    }

    private fun showNotification(context: Context, title: String, message: String, type: String) {
        val channelId = "reminder_channel_id"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo channel cho Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nhắc nhở học tập",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh thông báo nhắc nhở lịch học và bảo vệ streak"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            when(type){ "daily" -> 1 "streak" -> 2 else -> 3 }, // Request code độc lập
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Dùng tạm ic_launcher
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationId = when(type) { "daily" -> 101; "streak" -> 102; else -> 103 }
        notificationManager.notify(notificationId, notification)
    }
}
