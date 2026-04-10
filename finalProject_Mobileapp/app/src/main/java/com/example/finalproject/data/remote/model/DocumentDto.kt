package com.example.finalproject.data.remote.model

import com.google.gson.annotations.SerializedName
import java.text.DecimalFormat

/**
 * DTO nhận từ BE DocumentResponse.
 *
 * BE trả về MỘT ngôn ngữ đã được localized (dựa theo Accept-Language header):
 *   - "title"       → tiêu đề đã dịch
 *   - "description" → mô tả đã dịch
 *   - "examType"    → loại đề đã dịch
 *   - "filePath"    → đường dẫn file
 *   - "fileSize"    → dung lượng (bytes)
 *   - "subjectCode" → mã môn ("toan", "khtn", ...)
 *   - "grade"       → khối lớp (6, 7, 8, 9)
 *   - "createdAt"   → thời gian tạo (ISO format)
 */
data class DocumentDto(
    val id: Long,

    // BE đã localize sẵn → nhận 1 field "title" duy nhất
    val title: String?,

    val description: String?,

    @SerializedName("subjectCode") val subjectCode: String?,
    val grade: Int?,

    @SerializedName("examType") val examType: String?,

    @SerializedName("filePath")  val filePath: String?,
    @SerializedName("fileSize")  val fileSize: Long?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
) {
    /**
     * Lấy tiêu đề hiển thị. Vì BE đã localize, chỉ cần trả title trực tiếp.
     */
    fun getDisplayTitle(): String = title?.takeIf { it.isNotBlank() } ?: "Không có tên"

    /**
     * Lấy mô tả hiển thị.
     */
    fun getDisplayDescription(): String? = description?.takeIf { it.isNotBlank() }

    /**
     * Helper: Format dung lượng file (VD: 1,05 MB, 512 KB)
     */
    fun getFormattedSize(): String {
        if (fileSize == null || fileSize <= 0) return "Unknown"
        val df = DecimalFormat("#,##")
        val kb = fileSize / 1024.0
        val mb = kb / 1024.0
        return when {
            mb > 1 -> "${df.format(mb)} MB"
            kb > 1 -> "${df.format(kb)} KB"
            else   -> "$fileSize B"
        }
    }

    /**
     * Helper: Format ngày tháng từ chuỗi ISO "2026-03-30T13:50:30" → "30/03/2026"
     */
    fun getFormattedDate(): String {
        val dateStr = createdAt ?: return ""
        return try {
            val parts = dateStr.substringBefore("T").split("-")
            if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else ""
        } catch (e: Exception) {
            ""
        }
    }
}