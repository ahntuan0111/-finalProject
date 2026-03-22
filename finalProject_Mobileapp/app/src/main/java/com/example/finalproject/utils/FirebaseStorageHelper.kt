package com.example.finalproject.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Upload ảnh lên Firebase Storage thông qua Signed URL từ Backend.
 * Không cần Firebase Auth hay Firebase SDK trên client.
 */
object FirebaseStorageHelper {

    suspend fun uploadImageWithSignedUrl(context: Context, imageUri: Uri, signedUploadUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                    ?: throw Exception("Không thể đọc ảnh từ URI")
                val imageBytes = inputStream.readBytes()
                inputStream.close()

                val url = URL(signedUploadUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "PUT"
                connection.setRequestProperty("Content-Type", "image/jpeg")
                connection.doOutput = true
                connection.connectTimeout = 30000
                connection.readTimeout = 30000

                val outputStream: OutputStream = connection.outputStream
                outputStream.write(imageBytes)
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                connection.disconnect()

                responseCode in 200..299
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
