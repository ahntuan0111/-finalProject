package com.example.finalproject.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.finalproject.di.TheoryMode
import com.example.finalproject.ui.screens.auth.AuthSelectionScreen
import com.example.finalproject.ui.screens.auth.ForgotPasswordScreen
import com.example.finalproject.ui.screens.auth.LoginScreen
import com.example.finalproject.ui.screens.auth.RegisterScreen
import com.example.finalproject.ui.screens.auth.ResetPasswordScreen
import com.example.finalproject.ui.screens.auth.WelcomeScreen
import com.example.finalproject.ui.screens.class_selection.ClassSelectionScreen
import com.example.finalproject.ui.screens.exercise.ExerciseDetailScreen // <-- IMPORT MỚI
import com.example.finalproject.ui.screens.home.HomeScreen
import com.example.finalproject.ui.screens.subject_detail.SubjectDetailScreen
import com.example.finalproject.ui.screens.theory.TheoryDetailScreen
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

    // Định nghĩa tên tham số
    const val SUBJECT_CODE = "subjectCode"
    const val SUBJECT_NAME = "subjectName"
    const val MODE = "mode"
    const val LESSON_ID = "lessonId"

    // --- Route Chi tiết môn học ---
    const val SUBJECT_DETAIL = "subject_detail/{$SUBJECT_CODE}/{$SUBJECT_NAME}"
    fun createSubjectDetailRoute(code: String, name: String) = "subject_detail/$code/$name"

    // --- Route Danh sách bài học (TheoryScreen) ---
    const val THEORY_SCREEN = "theory/{$SUBJECT_CODE}/{$SUBJECT_NAME}/{$MODE}"
    fun createTheoryRoute(code: String, name: String, mode: String): String {
        return "theory/$code/$name/$mode"
    }

    // --- Route Chi tiết bài học (Lý thuyết - Video/Text) ---
    const val LESSON_DETAIL = "lesson_detail/{$LESSON_ID}"
    fun createLessonDetailRoute(id: Long) = "lesson_detail/$id"

    // --- Route Chi tiết bài tập (Làm bài tập) ---  <-- BỔ SUNG MỚI
    const val EXERCISE_DETAIL = "exercise_detail/{$LESSON_ID}"
    fun createExerciseDetailRoute(id: Long) = "exercise_detail/$id"

    const val PDF_VIEWER = "pdf_viewer/{url}/{title}"
    const val DOCUMENT_LIST = "document_list/{subjectCode}/{grade}"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AppRoutes.WELCOME
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

        // Các màn hình Placeholder
        composable(AppRoutes.DASHBOARD) { Text("Màn hình Thống kê") }
        composable(AppRoutes.AI_CHAT) { Text("Màn hình AI Chat") }
        composable(AppRoutes.USER) { Text("Màn hình Cá nhân") }

        // --- MÀN HÌNH CHI TIẾT MÔN HỌC ---
        composable(
            route = AppRoutes.SUBJECT_DETAIL,
            arguments = listOf(
                navArgument(AppRoutes.SUBJECT_CODE) { type = NavType.StringType },
                navArgument(AppRoutes.SUBJECT_NAME) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subjectCode = backStackEntry.arguments?.getString(AppRoutes.SUBJECT_CODE) ?: ""
            val subjectName = backStackEntry.arguments?.getString(AppRoutes.SUBJECT_NAME) ?: ""

            SubjectDetailScreen(
                navController = navController,
                subjectCode = subjectCode,
                subjectName = subjectName
            )
        }

        // --- MÀN HÌNH LÝ THUYẾT / BÀI TẬP (THEORY SCREEN) ---
        composable(
            route = AppRoutes.THEORY_SCREEN,
            arguments = listOf(
                navArgument(AppRoutes.SUBJECT_CODE) { type = NavType.StringType },
                navArgument(AppRoutes.SUBJECT_NAME) { type = NavType.StringType },
                navArgument(AppRoutes.MODE) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subjectCode = backStackEntry.arguments?.getString(AppRoutes.SUBJECT_CODE) ?: ""
            val subjectName = backStackEntry.arguments?.getString(AppRoutes.SUBJECT_NAME) ?: ""
            val mode = backStackEntry.arguments?.getString(AppRoutes.MODE) ?: TheoryMode.THEORY

            TheoryScreen(
                navController = navController,
                subjectCode = subjectCode,
                subjectName = subjectName,
                mode = mode
            )
        }

        // --- MÀN HÌNH CHI TIẾT BÀI HỌC (XEM VIDEO/TEXT) ---
        composable(
            route = AppRoutes.LESSON_DETAIL,
            arguments = listOf(
                navArgument(AppRoutes.LESSON_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonIdString = backStackEntry.arguments?.getString(AppRoutes.LESSON_ID)
            val lessonId = lessonIdString?.toLongOrNull() ?: 0L

            TheoryDetailScreen(
                navController = navController,
                lessonId = lessonId
            )
        }

        // --- MÀN HÌNH GIẢI BÀI TẬP (BỔ SUNG) ---
        composable(
            route = AppRoutes.EXERCISE_DETAIL,
            arguments = listOf(
                navArgument(AppRoutes.LESSON_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonIdString = backStackEntry.arguments?.getString(AppRoutes.LESSON_ID)
            val lessonId = lessonIdString?.toLongOrNull() ?: 0L

            // Gọi màn hình ExerciseDetailScreen
            ExerciseDetailScreen(
                navController = navController,
                lessonId = lessonId
            )
        }

    }
}