package com.example.finalproject.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.finalproject.ui.screens.auth.AuthSelectionScreen
import com.example.finalproject.ui.screens.auth.ForgotPasswordScreen
import com.example.finalproject.ui.screens.auth.LoginScreen
import com.example.finalproject.ui.screens.auth.RegisterScreen
import com.example.finalproject.ui.screens.auth.ResetPasswordScreen
import com.example.finalproject.ui.screens.auth.WelcomeScreen
import com.example.finalproject.ui.screens.class_selection.ClassSelectionScreen
import com.example.finalproject.ui.screens.home.HomeScreen
import com.example.finalproject.ui.screens.subject_detail.SubjectDetailScreen
import com.example.finalproject.ui.screens.theory.TheoryScreen

/**
 * Định nghĩa các đường dẫn (Route) trong App
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

    // --- SỬA: Dùng subjectCode (String) thay vì Id (Int) ---
    const val SUBJECT_DETAIL = "subject_detail/{subjectCode}/{subjectName}"
    fun createSubjectDetailRoute(code: String, name: String) = "subject_detail/$code/$name"

    // --- SỬA: Dùng subjectCode (String) thay vì Id (Int) ---
    const val THEORY = "theory/{subjectCode}/{subjectName}"
    fun createTheoryRoute(code: String, name: String) = "theory/$code/$name"

    const val LESSON_DETAIL = "lesson_detail/{lessonId}"
    fun createLessonDetailRoute(id: Long) = "lesson_detail/$id"
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
        // 1. Màn hình Chào mừng
        composable(AppRoutes.WELCOME) {
            WelcomeScreen(navController = navController)
        }

        // 2. Màn hình Chọn Đăng nhập / Đăng ký
        composable(AppRoutes.AUTH_SELECTION) {
            AuthSelectionScreen(navController = navController)
        }

        // 3. Màn hình Đăng nhập
        composable(AppRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }

        // 4. Màn hình Đăng ký
        composable(AppRoutes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // 5. Màn hình Trang chủ
        composable(AppRoutes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(AppRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController = navController)
        }

        // Màn hình Reset mật khẩu
        composable(
            route = AppRoutes.RESET_PASSWORD,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(navController = navController, email = email)
        }

        composable(AppRoutes.CLASS_SELECTION) {
            ClassSelectionScreen(navController = navController)
        }

        // Các màn hình Placeholder cho BottomNavigation
        composable(AppRoutes.DASHBOARD) {
            Text("Màn hình Thống kê (Dashboard)")
        }
        composable(AppRoutes.AI_CHAT) {
            Text("Màn hình AI Chat")
        }
        composable(AppRoutes.USER) {
            Text("Màn hình Cá nhân (User)")
        }

        // --- MÀN HÌNH CHI TIẾT MÔN HỌC ---
        composable(AppRoutes.SUBJECT_DETAIL) { backStackEntry ->
            // SỬA: Lấy String (Code) thay vì Int (Id)
            val subjectCode = backStackEntry.arguments?.getString("subjectCode") ?: "toan"
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: "Môn học"

            SubjectDetailScreen(
                navController = navController,
                subjectCode = subjectCode,   // Truyền Code ("toan", "khtn")
                subjectName = subjectName
                // KHÔNG TRUYỀN GRADE NỮA (Screen tự lấy từ UserViewModel)
            )
        }

        // --- MÀN HÌNH LÝ THUYẾT ---
        composable(AppRoutes.THEORY) { backStackEntry ->
            // SỬA: Lấy String (Code) thay vì Int (Id)
            val subjectCode = backStackEntry.arguments?.getString("subjectCode") ?: "toan"
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: "Môn học"

            TheoryScreen(
                navController = navController,
                subjectCode = subjectCode,   // Truyền Code ("toan", "khtn")
                subjectName = subjectName
                // KHÔNG TRUYỀN GRADE NỮA (Screen tự lấy từ UserViewModel)
            )
        }

        composable(AppRoutes.LESSON_DETAIL) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId")?.toLongOrNull() ?: 0L

            // Gọi màn hình TheoryDetailScreen
            com.example.finalproject.ui.screens.theory.TheoryDetailScreen(
                navController = navController,
                lessonId = lessonId
            )
        }
    }
}