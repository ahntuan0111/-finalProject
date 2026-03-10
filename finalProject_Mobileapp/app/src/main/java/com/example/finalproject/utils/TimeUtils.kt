package com.example.finalproject.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TimeUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRelativeTime(createdAt: String): String {
        try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val past = LocalDateTime.parse(createdAt, formatter)
            val now = LocalDateTime.now(ZoneId.systemDefault())

            val seconds = ChronoUnit.SECONDS.between(past, now)
            val minutes = ChronoUnit.MINUTES.between(past, now)
            val hours = ChronoUnit.HOURS.between(past, now)
            val days = ChronoUnit.DAYS.between(past, now)

            return when {
                seconds < 60 -> "Vừa xong"
                minutes < 60 -> "$minutes phút trước"
                hours < 24 -> "$hours giờ trước"
                days == 1L -> "Hôm qua"
                days < 7 -> "$days ngày trước"
                else -> {
                    val dateOnlyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    past.format(dateOnlyFormatter)
                }
            }
        } catch (e: Exception) {
            return createdAt // Fallback nếu parse lỗi
        }
    }
}