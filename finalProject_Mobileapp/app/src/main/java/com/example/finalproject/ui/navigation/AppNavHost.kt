package com.example.finalproject.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel // CẬP NHẬT IMPORT NÀY
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation // IMPORT CHO NESTED NAVIGATION
import androidx.navigation.navArgument
import com.example.finalproject.di.TheoryMode
import com.example.finalproject.ui.screens.auth.AuthSelectionScreen
import com.example.finalproject.ui.screens.auth.ForgotPasswordScreen
import com.example.finalproject.ui.screens.auth.LoginScreen
import com.example.finalproject.ui.screens.auth.OtpVerificationScreen
import com.example.finalproject.ui.screens.auth.RegisterScreen
import com.example.finalproject.ui.screens.auth.ResetPasswordScreen
import com.example.finalproject.ui.screens.auth.WelcomeScreen
import com.example.finalproject.ui.screens.class_selection.ClassSelectionScreen
import com.example.finalproject.ui.screens.dashboard.DashboardScreen
import com.example.finalproject.ui.screens.document.DocumentListScreen
import com.example.finalproject.ui.screens.document.PdfViewerScreen
import com.example.finalproject.ui.screens.reminder.ReminderScreen
import com.example.finalproject.ui.screens.exercise.ExerciseDetailScreen
import com.example.finalproject.ui.screens.home.HomeScreen
import com.example.finalproject.ui.screens.subject_detail.SubjectDetailScreen
import com.example.finalproject.ui.screens.theory.TheoryDetailScreen
import com.example.finalproject.ui.screens.theory.TheoryScreen
import com.example.finalproject.utils.DataStoreManager
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import com.example.finalproject.ui.screens.quizes.QuizResultScreen
import com.example.finalproject.ui.screens.quizes.QuizTakingScreen
import com.example.finalproject.ui.screens.quizes.QuizListScreen
import com.example.finalproject.ui.viewmodel.QuizViewModel
import com.example.finalproject.ui.screens.ai.ChatBoxAIScreen
import com.example.finalproject.ui.screens.profile.UserScreen
import com.example.finalproject.ui.screens.search.SearchScreen

/**
 * Định nghĩa các đường dẫn (Route) trong App
 */
object AppRoutes {
    const val WELCOME = "welcome"
    const val AUTH_SELECTION = "auth_selection"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP_VERIFICATION = "otp_verification/{email}"
    fun createOtpVerificationRoute(email: String) = "otp_verification/$email"
    const val HOME = "home"
    const val DASHBOARD = "dashboard"
    const val AI_CHAT = "ai_chat"
    const val REMINDER = "reminder_global"
    const val USER = "user"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password/{email}"
    const val CLASS_SELECTION = "class_selection"
    const val SEARCH = "search"

    const val SUBJECT_CODE = "subjectCode"
    const val SUBJECT_NAME = "subjectName"
    const val MODE = "mode"
    const val LESSON_ID = "lessonId"
    const val EXERCISE_ID = "exerciseId"
    const val GRADE = "grade"

    const val SUBJECT_DETAIL = "subject_detail/{$SUBJECT_CODE}/{$SUBJECT_NAME}"
    fun createSubjectDetailRoute(code: String, name: String) = "subject_detail/$code/$name"

    const val THEORY_SCREEN = "theory/{$SUBJECT_CODE}/{$SUBJECT_NAME}/{$MODE}"
    fun createTheoryRoute(code: String, name: String, mode: String): String {
        return "theory/$code/$name/$mode"
    }

    const val LESSON_DETAIL = "lesson_detail/{$LESSON_ID}"
    fun createLessonDetailRoute(id: Long) = "lesson_detail/$id"

    // Các màn hình Detail
    const val THEO_DETAIL = "theo_detail/{$SUBJECT_CODE}/{$GRADE}"
    const val EXERCISE_LIST = "exercise_list/{$LESSON_ID}" // MỚI
    fun createExerciseListRoute(id: Long) = "exercise_list/$id"

    const val EXERCISE_DETAIL = "exercise_detail/{$LESSON_ID}/{$EXERCISE_ID}"

    const val DOCUMENT_LIST = "document_list/{$SUBJECT_CODE}/{$GRADE}"
    fun createDocumentListRoute(code: String, grade: Int) = "document_list/$code/$grade"

    const val PDF_VIEWER = "pdf_viewer/{url}/{title}"

    // --- THÊM ROUTES CHO LUỒNG QUIZ ---
    const val QUIZ_FLOW = "quiz_flow/{$SUBJECT_CODE}/{$GRADE}"
    fun createQuizFlowRoute(code: String, grade: Int) = "quiz_flow/$code/$grade"

    const val QUIZ_LIST = "quiz_list/{$SUBJECT_CODE}/{$GRADE}"
    fun createQuizListRoute(code: String, grade: Int) = "quiz_list/$code/$grade"

    const val QUIZ_TAKING = "quiz_taking"
    const val QUIZ_RESULT = "quiz_result"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
    startDestination: String
) {
    // 1. Lắng nghe trạng thái của Token từ DataStoreManager
    val token by dataStoreManager.getToken().collectAsState(initial = "LOADING")

    // 2. Phản ứng khi Token thay đổi
    LaunchedEffect(token) {
        if (token == null) {
            navController.navigate(AppRoutes.WELCOME) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AppRoutes.WELCOME) { WelcomeScreen(navController = navController) }
        composable(AppRoutes.AUTH_SELECTION) { AuthSelectionScreen(navController = navController) }
        composable(AppRoutes.LOGIN) { LoginScreen(navController = navController) }
        composable(AppRoutes.REGISTER) { RegisterScreen(navController = navController) }
        composable(
            route = AppRoutes.OTP_VERIFICATION,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtpVerificationScreen(navController = navController, email = email)
        }
        composable(AppRoutes.HOME) { HomeScreen(navController = navController) }
        composable(AppRoutes.FORGOT_PASSWORD) { ForgotPasswordScreen(navController = navController) }

        composable(
            route = AppRoutes.RESET_PASSWORD,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(navController = navController, email = email)
        }

        composable(AppRoutes.CLASS_SELECTION) { ClassSelectionScreen(navController = navController) }
        composable(AppRoutes.DASHBOARD) {
            DashboardScreen(navController = navController)
        }
        composable(AppRoutes.SEARCH) {
            SearchScreen(navController = navController)
        }
        composable(AppRoutes.AI_CHAT) { ChatBoxAIScreen() }
        composable(AppRoutes.REMINDER) { ReminderScreen(navController = navController) }
        composable(AppRoutes.USER) {
            UserScreen(navController = navController)
        }

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

        // Bổ sung ExerciseListScreen
        composable(
            route = AppRoutes.EXERCISE_LIST,
            arguments = listOf(
                navArgument(AppRoutes.LESSON_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonIdString = backStackEntry.arguments?.getString(AppRoutes.LESSON_ID)
            val lessonId = lessonIdString?.toLongOrNull() ?: 0L

            com.example.finalproject.ui.screens.exercise.ExerciseListScreen(
                navController = navController,
                lessonId = lessonId
            )
        }

        composable(
            route = AppRoutes.EXERCISE_DETAIL,
            arguments = listOf(
                navArgument(AppRoutes.LESSON_ID) { type = NavType.StringType },
                navArgument(AppRoutes.EXERCISE_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonIdString = backStackEntry.arguments?.getString(AppRoutes.LESSON_ID)
            val lessonId = lessonIdString?.toLongOrNull() ?: 0L
            
            val exerciseIdString = backStackEntry.arguments?.getString(AppRoutes.EXERCISE_ID)
            val exerciseId = exerciseIdString?.toLongOrNull() ?: 0L

            ExerciseDetailScreen(
                navController = navController,
                lessonId = lessonId,
                exerciseId = exerciseId
            )
        }

        composable(
            route = AppRoutes.DOCUMENT_LIST,
            arguments = listOf(
                navArgument(AppRoutes.SUBJECT_CODE) { type = NavType.StringType },
                navArgument(AppRoutes.GRADE) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val subjectCode = backStackEntry.arguments?.getString(AppRoutes.SUBJECT_CODE) ?: ""
            val grade = backStackEntry.arguments?.getInt(AppRoutes.GRADE) ?: 6

            DocumentListScreen(
                navController = navController,
                subjectCode = subjectCode,
                grade = grade
            )
        }

        composable(
            route = AppRoutes.PDF_VIEWER,
            arguments = listOf(
                navArgument("url") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Giải mã Base64 URL Safe để tránh việc Navigation tự decode mất %2F của Firebase
            val b64url = backStackEntry.arguments?.getString("url") ?: ""
            val b64title = backStackEntry.arguments?.getString("title") ?: ""
            
            var url = ""
            var title = ""
            try {
                url = String(android.util.Base64.decode(b64url, android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP), StandardCharsets.UTF_8)
                title = String(android.util.Base64.decode(b64title, android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP), StandardCharsets.UTF_8)
            } catch (e: Exception) {
                // Ignore fallback to empty
            }

            PdfViewerScreen(
                navController = navController,
                fileUrl = url,
                title = title
            )
        }

        // =========================================================
        // LUỒNG LÀM QUIZ MỚI (NESTED NAVIGATION)
        // =========================================================
        navigation(
            route = AppRoutes.QUIZ_FLOW,
            startDestination = AppRoutes.QUIZ_LIST
        ) {
            composable(
                route = AppRoutes.QUIZ_LIST,
                arguments = listOf(
                    navArgument(AppRoutes.SUBJECT_CODE) { type = NavType.StringType },
                    // SỬA Ở ĐÂY: Đổi IntType thành StringType để tránh lỗi parse của Navigation
                    navArgument(AppRoutes.GRADE) { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val subjectCode = backStackEntry.arguments?.getString(AppRoutes.SUBJECT_CODE) ?: ""

                // SỬA Ở ĐÂY: Lấy String rồi tự .toIntOrNull(), nếu null thì cho mặc định là 6
                val gradeString = backStackEntry.arguments?.getString(AppRoutes.GRADE)
                val grade = gradeString?.toIntOrNull() ?: 6

                val parentEntry = remember(backStackEntry) {
                    // Dùng cứng chuỗi createQuizFlowRoute để đảm bảo match chính xác
                    navController.getBackStackEntry(AppRoutes.createQuizFlowRoute(subjectCode, grade))
                }
                val sharedQuizViewModel: QuizViewModel = hiltViewModel(parentEntry)

                QuizListScreen(
                    navController = navController,
                    viewModel = sharedQuizViewModel,
                    subjectCode = subjectCode,
                    grade = grade
                )
            }

            composable(route = AppRoutes.QUIZ_TAKING) { backStackEntry ->
                val parentId = navController.previousBackStackEntry?.destination?.parent?.id
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(parentId!!)
                }
                val sharedQuizViewModel: QuizViewModel = hiltViewModel(parentEntry)

                QuizTakingScreen(
                    navController = navController,
                    viewModel = sharedQuizViewModel
                )
            }

            composable(route = AppRoutes.QUIZ_RESULT) { backStackEntry ->
                val parentId = navController.previousBackStackEntry?.destination?.parent?.id
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(parentId!!)
                }
                val sharedQuizViewModel: QuizViewModel = hiltViewModel(parentEntry)

                QuizResultScreen(
                    navController = navController,
                    viewModel = sharedQuizViewModel
                )
            }
        }
    }
}