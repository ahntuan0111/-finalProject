package com.example.finalproject

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
// XÓA: import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity // THÊM IMPORT NÀY
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.ui.MainScreen
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.ui.theme.FinalProjectTheme
import com.example.finalproject.utils.DataStoreManager
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() { // SỬA: Kế thừa từ AppCompatActivity

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    // Gọi ApiService để push token lên BE
    @Inject
    lateinit var apiService: ApiService

    // Bộ công cụ xin quyền thông báo (Bắt buộc từ Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("FCM_PERMISSION", "Người dùng đã cấp quyền thông báo")
        } else {
            Toast.makeText(this, "Bạn có thể bỏ lỡ các nhắc nhở học tập quan trọng!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedLang = runBlocking { dataStoreManager.getLanguageSync() }
        val appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(savedLang)
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)

        // =================================================================
    // PHẦN 1: CẤU HÌNH THÔNG BÁO FIREBASE & GỬI TOKEN LÊN BACKEND
        // =================================================================

        // Xin quyền hiển thị thông báo (Chỉ gọi lệnh này trên Android 13 trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Lấy FCM Token và gửi lên BE
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Lấy token thất bại", task.exception)
                return@addOnCompleteListener
            }
            // Lấy token thành công
            val token = task.result
            Log.d("FCM_TOKEN", "MÃ THIẾT BỊ LÀ: $token")

            // Gửi Token lên Backend bằng Coroutine
            lifecycleScope.launch(Dispatchers.IO) {
                // Chỉ gửi nếu người dùng đã đăng nhập (có JWT)
                val jwtToken = dataStoreManager.getTokenSync()
                if (!jwtToken.isNullOrEmpty()) {
                    try {
                        val requestBody = mapOf("fcmToken" to token)
                        val response = apiService.updateFcmToken(requestBody)
                        if (response.isSuccessful) {
                            Log.d("FCM_API", "Đã lưu FCM Token lên Backend thành công!")
                        } else {
                            Log.e("FCM_API", "Lỗi BE trả về: ${response.errorBody()?.string()}")
                        }
                    } catch (e: Exception) {
                        Log.e("FCM_API", "Call API thất bại: ${e.message}")
                    }
                } else {
                    Log.d("FCM_API", "User chưa đăng nhập, không gửi token lên BE")
                }
            }
        }

        // =================================================================
        // PHẦN 2: XỬ LÝ ĐIỀU HƯỚNG MÀN HÌNH ĐẦU TIÊN
        // =================================================================

        // Đọc token đồng bộ ngay khi mở App
        val initialToken = runBlocking { dataStoreManager.getTokenSync() }

        // Quyết định màn hình đầu tiên (Có token -> HOME, Không token -> WELCOME)
        val startDestination = if (initialToken.isNullOrEmpty()) {
            AppRoutes.WELCOME
        } else {
            AppRoutes.HOME
        }

        // Render giao diện Jetpack Compose
        setContent {
            FinalProjectTheme {
                MainScreen(
                    dataStoreManager = dataStoreManager,
                    startDestination = startDestination
                )
            }
        }
    }
}