package com.example.finalproject.utils

import org.json.JSONObject
import retrofit2.Response

/**
 * ErrorParser — chuyển đổi lỗi server/network → chuỗi thân thiện với người dùng.
 *
 * NGUYÊN TẮC:
 *  - KHÔNG bao giờ hiển thị mã lỗi kỹ thuật (402, 500, NullPointerException...) cho người dùng.
 *  - Mọi Exception đều phải qua hàm này trước khi đưa lên UI.
 *  - Log đầy đủ thông tin kỹ thuật bằng AppLogger để dev debug.
 */
object ErrorParser {

    /**
     * Parse lỗi từ HTTP Response (khi server trả về body không null nhưng isSuccessful = false).
     * Thử đọc field "message" trong JSON body trước, nếu thất bại dùng fallback.
     */
    fun <T> fromResponse(response: Response<T>, fallback: String = "Có lỗi xảy ra, vui lòng thử lại"): String {
        return try {
            val errorJson = response.errorBody()?.string()
            AppLogger.network("ErrorParser", "HTTP ${response.code()} - raw body: $errorJson")

            if (!errorJson.isNullOrBlank()) {
                val json = JSONObject(errorJson)
                // Thử lần lượt các field phổ biến
                val msg = json.optString("message")
                    .ifBlank { json.optString("error") }
                    .ifBlank { json.optString("msg") }
                    .ifBlank { null }

                if (!msg.isNullOrBlank()) friendlyMessage(msg) else friendlyCode(response.code(), fallback)
            } else {
                friendlyCode(response.code(), fallback)
            }
        } catch (e: Exception) {
            AppLogger.e("ErrorParser", "Không parse được lỗi response", e)
            fallback
        }
    }

    /**
     * Parse Exception (network error, timeout, v.v.)
     */
    fun fromException(e: Exception, fallback: String = "Mất kết nối mạng, vui lòng thử lại"): String {
        AppLogger.e("ErrorParser", "Exception: ${e.javaClass.simpleName} — ${e.message}", e)
        return when (e) {
            is java.net.UnknownHostException,
            is java.net.ConnectException -> "⚡ Không thể kết nối máy chủ. Kiểm tra Wifi/4G nhé!"
            is java.net.SocketTimeoutException -> "⏱️ Kết nối quá chậm. Vui lòng thử lại!"
            is retrofit2.HttpException -> friendlyCode(e.code(), fallback)
            else -> fallback
        }
    }

    /**
     * Chuyển mã HTTP → chuỗi thân thiện.
     * KHÔNG bao giờ trả về chuỗi có chứa số mã lỗi kỹ thuật.
     */
    private fun friendlyCode(code: Int, fallback: String): String {
        AppLogger.network("ErrorParser", "HTTP error code: $code")
        return when (code) {
            400 -> "Thông tin không hợp lệ, vui lòng kiểm tra lại"
            401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại"
            402, 403 -> "Bạn không có quyền thực hiện thao tác này"
            404 -> "Không tìm thấy dữ liệu yêu cầu"
            408 -> "⏱️ Kết nối quá chậm. Vui lòng thử lại!"
            409 -> "Dữ liệu đã tồn tại, vui lòng kiểm tra lại"
            422 -> "Thông tin không đúng định dạng yêu cầu"
            429 -> "Bạn đang thao tác quá nhanh. Hãy đợi một chút!"
            in 500..599 -> "Máy chủ đang gặp sự cố. Vui lòng thử lại sau"
            else -> fallback
        }
    }

    /**
     * Lọc các message từ server: nếu chứa ký tự kỹ thuật → dùng fallback thân thiện.
     */
    fun friendlyMessage(serverMsg: String): String {
        // Các pattern kỹ thuật không được hiển thị cho user
        val technicalPatterns = listOf(
            "exception", "nullpointer", "stacktrace", "sql", "hibernate",
            "java.", "org.springframework", "error:", "500", "404", "403"
        )
        val lower = serverMsg.lowercase()
        return if (technicalPatterns.any { lower.contains(it) }) {
            AppLogger.w("ErrorParser", "Filtered technical message: $serverMsg")
            "Có lỗi xảy ra, vui lòng thử lại sau"
        } else {
            serverMsg
        }
    }
}
