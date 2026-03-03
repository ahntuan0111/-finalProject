package com.example.finalproject

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.finalproject.ui.MainScreen
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.ui.theme.FinalProjectTheme
import com.example.finalproject.utils.DataStoreManager
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

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

        // =================================================================
        // PHẦN 1: CẤU HÌNH THÔNG BÁO FIREBASE (FCM)
        // =================================================================

        // Xin quyền hiển thị thông báo (Chỉ gọi lệnh này trên Android 13 trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Lấy FCM Token (Mã định danh thiết bị) để in ra Logcat
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Lấy token thất bại", task.exception)
                return@addOnCompleteListener
            }
            // Lấy token thành công
            val token = task.result
            Log.d("FCM_TOKEN", "MÃ THIẾT BỊ LÀ: $token")

            // (Tùy chọn) Gợi ý: Về sau, nếu người dùng đã đăng nhập,
            // bạn có thể gọi API gửi token này lên Spring Boot tại đây.
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
                    startDestination = startDestination // Truyền biến này vào MainScreen
                )
            }
        }
    }
}