package com.example.finalproject.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.model.QuizResponse
import com.example.finalproject.data.remote.model.QuizSubmitRequest
import com.example.finalproject.data.repository.QuizRepositoryImpl
import com.example.finalproject.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import retrofit2.HttpException

@HiltViewModel // <--- BẮT BUỘC PHẢI CÓ
class QuizViewModel @Inject constructor( // <--- THÊM @Inject constructor
    private val quizRepository: QuizRepository // <--- Đổi thành Interface QuizRepository
) : ViewModel() {

    // --- CÁC ĐOẠN CODE BÊN TRONG GIỮ NGUYÊN HOÀN TOÀN ---
    private val _quizList = MutableStateFlow<List<QuizResponse>>(emptyList())
    val quizList: StateFlow<List<QuizResponse>> = _quizList

    var isLoading = mutableStateOf(false)
        private set

    var currentQuiz = mutableStateOf<QuizResponse?>(null)
        private set
    val userAnswers = mutableStateMapOf<Long, Long>()

    var isSubmitting = mutableStateOf(false)
        private set

    var submitSuccess = mutableStateOf<Boolean?>(null)
        private set

    fun fetchQuizzes(subjectCode: String, grade: Int) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // In ra Logcat để xem Mobile đang gửi tham số gì
                Log.d("QUIZ_DEBUG", "Đang gọi API lấy Quiz: subjectCode = '$subjectCode', grade = $grade")

                val response = quizRepository.getQuizzesBySubjectAndGrade(subjectCode, grade)

                if (response.success) {
                    val list = response.data ?: emptyList()
                    // In ra số lượng bài Quiz lấy được
                    Log.d("QUIZ_DEBUG", "API trả về thành công: ${list.size} bài Quiz")
                    _quizList.value = list
                } else {
                    Log.e("QUIZ_DEBUG", "Lỗi từ Backend: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("QUIZ_DEBUG", "Lỗi Exception khi gọi API", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    // Các hàm cũ giữ nguyên
    fun setQuiz(quiz: QuizResponse) {
        currentQuiz.value = quiz
        userAnswers.clear()
    }

    fun selectAnswer(questionId: Long, choiceId: Long) {
        userAnswers[questionId] = choiceId
    }

    fun calculateScore(): Int {
        var count = 0
        currentQuiz.value?.questions?.forEach { q ->
            if (userAnswers[q.id] == q.choices.find { it.correct }?.id) count++
        }
        return count
    }

    fun isAllAnswered(): Boolean = userAnswers.size == (currentQuiz.value?.questions?.size ?: 0)

    fun submitQuizResult() {
        val quiz = currentQuiz.value ?: return
        viewModelScope.launch {
            isSubmitting.value = true
            try {
                val correctAnswers = calculateScore()
                val request = QuizSubmitRequest(
                    correctAnswers = correctAnswers,
                    totalQuestions = quiz.questions.size,
                    durationSeconds = 0
                )

                // quiz.id có thể là Long, nếu API yêu cầu Int thì nhớ ép kiểu .toInt()
                val response = quizRepository.submitQuiz(quiz.id.toInt(), request)
                submitSuccess.value = response.success

            } catch (e: HttpException) {
                // BẮT LỖI HTTP (400, 401, 403, 500...) VÀ IN RA LOG
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("QUIZ_DEBUG", "Lỗi HTTP từ Server: $errorBody")
                submitSuccess.value = false
            } catch (e: Exception) {
                Log.e("QUIZ_DEBUG", "Lỗi Exception Android: ${e.message}")
                submitSuccess.value = false
            } finally {
                isSubmitting.value = false
            }
        }
    }

    // Hàm reset trạng thái
    fun resetSubmitState() {
        submitSuccess.value = null
    }
}