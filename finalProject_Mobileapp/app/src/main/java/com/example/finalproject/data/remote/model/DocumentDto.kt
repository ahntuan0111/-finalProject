package com.example.finalproject.data.remote.model

import com.google.gson.annotations.SerializedName
import java.text.DecimalFormat

/**
 * DTO nhận từ BE DocumentResponse.
 * BE trả về camelCase: titleVi, titleEn, descriptionVi, filePath, v.v.
 *
 * Quy tắc subject_code chuẩn: "toan", "khtn" (lowercase, không dấu)
 * grade: 6, 7, 8, 9 (tương ứng grades.id trong DB)
 */
data class DocumentDto(
    val id: Long,

    // BE trả về "titleVi" và "titleEn" — KHÔNG phải "title"
    @SerializedName("titleVi")   val titleVi: String?,
    @SerializedName("titleEn")   val titleEn: String?,

    // BE trả về "descriptionVi" và "descriptionEn"
    @SerializedName("descriptionVi") val descriptionVi: String?,
    @SerializedName("descriptionEn") val descriptionEn: String?,

    // Thông tin phân loại
    @SerializedName("subjectCode") val subjectCode: String?,  // "toan", "khtn"
    val grade: Int?,                                          // 6, 7, 8, 9

    // Loại đề: "Giữa kỳ", "Cuối kỳ", "Chuyên đề / Lý thuyết"
    @SerializedName("examTypeVi") val examTypeVi: String?,
    @SerializedName("examTypeEn") val examTypeEn: String?,

    @SerializedName("filePath")  val filePath: String?,
    @SerializedName("fileSize")  val fileSize: Long?,
    @SerializedName("createdAt") val createdAt: String?
) {
    /**
     * Trả về tiêu đề dựa trên ngôn ngữ, fallback sang tiêu đề còn lại nếu null.
     * @param lang "vi" (mặc định) hoặc "en"
     */
    fun getTitle(lang: String = "vi"): String {
        return when (lang) {
            "en" -> titleEn ?: titleVi ?: "Untitled"
            else -> titleVi ?: titleEn ?: "Không có tên"
        }
    }

    /**
     * Trả về mô tả dựa trên ngôn ngữ.
     */
    fun getDescription(lang: String = "vi"): String? {
        return when (lang) {
            "en" -> descriptionEn ?: descriptionVi
            else -> descriptionVi ?: descriptionEn
        }
    }

    /**
     * Helper: Format dung lượng file (VD: 1.05 MB, 512 KB)
     */
    fun getFormattedSize(): String {
        if (fileSize == null || fileSize <= 0) return "Unknown"
        val df = DecimalFormat("#.##")
        val kb = fileSize / 1024.0
        val mb = kb / 1024.0
        return when {
            mb > 1 -> "${df.format(mb)} MB"
            kb > 1 -> "${df.format(kb)} KB"
            else   -> "$fileSize B"
        }
    }

    /**
     * Helper: Format ngày tháng từ chuỗi ISO "2026-03-30T13:50:30"
     */
    fun getFormattedDate(): String {
        if (createdAt == null) return ""
        return try {
            // createdAt từ BE: "2026-03-30T13:50:30" → parse LocalDateTime
            val parts = createdAt.substringBefore("T").split("-")
            if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else ""
        } catch (e: Exception) {
            ""
        }
    }
}