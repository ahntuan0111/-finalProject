package com.example.finalproject.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

object FirebaseStorageHelper {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    /**
     * Tải lên ảnh Avatar lên Firebase Storage
     *
     * @param localUri Đường dẫn Uri của ảnh (content://...)
     * @return Download URL của ảnh sau khi upload thành công, hoặc null nếu thất bại
     */
    suspend fun uploadAvatar(localUri: Uri): String? {
        return try {
            // Tạo unique ID cho filename
            val filename = UUID.randomUUID().toString() + ".jpg"
            val avatarRef = storageRef.child("avatar").child(filename)
            
            // Upload ảnh lên Firebase
            val uploadTaskSnapshot = avatarRef.putFile(localUri).await()
            
            // Lấy Download URL
            val downloadUrl = avatarRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Upload bằng Signed URL (dùng OkHttp để PUT trực tiếp lên Google Cloud Storage)
     */
    suspend fun uploadImageWithSignedUrl(context: android.content.Context, localUri: Uri, signedUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(localUri)
                val bytes = inputStream?.readBytes() ?: return@withContext false
                inputStream.close()

                val client = OkHttpClient()
                val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
                val request = Request.Builder()
                    .url(signedUrl)
                    .put(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                response.isSuccessful
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
