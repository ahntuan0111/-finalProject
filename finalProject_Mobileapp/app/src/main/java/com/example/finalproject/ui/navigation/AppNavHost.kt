package com.example.finalproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finalproject.ui.screens.auth.AuthSelectionScreen
import com.example.finalproject.ui.screens.auth.LoginScreen
import com.example.finalproject.ui.screens.auth.WelcomeScreen
// import com.example.finalproject.ui.screens.auth.RegisterScreen // (Sẽ mở comment khi tạo xong file này)
// import com.example.finalproject.ui.screens.home.HomeScreen // (Sẽ mở comment khi tạo xong file này)
import androidx.compose.material3.Text // Dùng tạm cho màn hình chưa có
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.finalproject.ui.screens.auth.ForgotPasswordScreen
import com.example.finalproject.ui.screens.auth.RegisterScreen
import com.example.finalproject.ui.screens.auth.ResetPasswordScreen
import com.example.finalproject.ui.screens.class_selection.ClassSelectionScreen
import com.example.finalproject.ui.screens.home.HomeScreen

/**
 * Định nghĩa các đường dẫn (Route) trong App
 * Nên tách ra file Constants riêng nếu App lớn, tạm thời để đây cho gọn.
 */
object AppRoutes {
    const val WELCOME = "welcome"
    const val AUTH_SELECTION = "auth_selection"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"

    const val DASHBOARD = "dashboard"

    const val AI_CHAT = "ai_chat"

    const val USER = "user"

    const val FORGOT_PASSWORD = "forgot_password"

    const val RESET_PASSWORD = "reset_password/{email}"

    const val CLASS_SELECTION = "class_selection"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AppRoutes.WELCOME // Mặc định vào màn hình Welcome
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 1. Màn hình Chào mừng (Slide giới thiệu)
        composable(AppRoutes.WELCOME) {
            WelcomeScreen(navController = navController)
        }

        // 2. Màn hình Chọn Đăng nhập / Đăng ký
        composable(AppRoutes.AUTH_SELECTION) {
            AuthSelectionScreen(navController = navController)
        }

        // 3. Màn hình Đăng nhập
        composable(AppRoutes.LOGIN) {
            // LoginScreen tự gọi ViewModel bằng hiltViewModel() bên trong nó
            LoginScreen(navController = navController)
        }

        // 4. Màn hình Đăng ký (Placeholder - Chờ bạn tạo file RegisterScreen)
        composable(AppRoutes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // 5. Màn hình Trang chủ (Sau khi login thành công)
        composable(AppRoutes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(AppRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController = navController)
        }

        // 2. Màn hình Reset (nhận email)
        composable(
            route = AppRoutes.RESET_PASSWORD,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(navController = navController, email = email)
        }

        // --- THÊM ĐOẠN NÀY ĐỂ SỬA LỖI ---
        composable(AppRoutes.CLASS_SELECTION) {
            ClassSelectionScreen(navController = navController)
        }

        composable(AppRoutes.HOME) {
            HomeScreen(navController = navController)
        }
        composable(AppRoutes.DASHBOARD) {
            Text("Màn hình Thống kê (Dashboard)")
        }
        composable(AppRoutes.AI_CHAT) {
            Text("Màn hình AI Chat")
        }
        composable(AppRoutes.USER) {
            Text("Màn hình Cá nhân (User)")
        }
    }
}