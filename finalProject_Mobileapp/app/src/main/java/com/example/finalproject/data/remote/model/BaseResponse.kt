package com.example.finalproject.data.remote.model

import com.google.gson.annotations.SerializedName

// Class này dùng chung cho toàn bộ các API trả về từ Spring Boot
data class BaseResponse<T>(
    @SerializedName("status")
    val status: Int,

    @SerializedName("message")
    val message: String?,

    // Thuộc tính 'data' này chính là cái mà ViewModel của bạn đang gọi tới
    @SerializedName("data")
    val data: T?
)