package com.example.finalproject.data.remote.model

import com.google.gson.annotations.SerializedName
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class DocumentDto(
    val id: Long,
    val title: String,
    val description: String?,
    @SerializedName("file_path") val filePath: String, // "/api/documents/files/..."
    @SerializedName("file_size") val fileSize: Long?,
    @SerializedName("created_at") val createdAt: String? // Chuỗi ISO date
) {
    // Helper: Format dung lượng file (VD: 1024 -> "1 KB")
    fun getFormattedSize(): String {
        if (fileSize == null) return "Unknown"
        val df = DecimalFormat("#.##")
        val kb = fileSize / 1024.0
        val mb = kb / 1024.0
        return when {
            mb > 1 -> "${df.format(mb)} MB"
            kb > 1 -> "${df.format(kb)} KB"
            else -> "$fileSize B"
        }
    }

    // Helper: Format ngày tháng (VD: "25/01/2026")
    fun getFormattedDate(): String {
        if (createdAt == null) return ""
        return try {
            // Cần xử lý parse chuỗi ISO từ BE
            val parsed = LocalDateTime.parse(createdAt)
            parsed.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            ""
        }
    }
}