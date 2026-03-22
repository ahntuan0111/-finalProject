package com.example.finalproject.data.remote.model

data class ValidationSubmissionRequest(
    val base64Image: String
)

data class ValidationSubmissionResponse(
    val isValid: Boolean,
    val reason: String?
)

data class SaveSubmissionRequest(
    val imageUrl: String,
    val aiReason: String?
)

data class UploadUrlResponse(
    val uploadUrl: String,
    val fileName: String,
    val publicUrl: String
)
