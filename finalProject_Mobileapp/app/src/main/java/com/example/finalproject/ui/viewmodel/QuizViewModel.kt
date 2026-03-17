package com.example.finalproject.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.model.QuizResponse
import com.example.finalproject.data.remote.model.QuizSubmitRequest
import com.example.finalproject.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

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

    // =========================================================================
    // THÊM LOGIC TIMER ĐẾM NGƯỢC
    // =========================================================================
    var timeLeft = mutableIntStateOf(0)
        private set

    private var totalTimeAllowed = 0
    private var timerJob: Job? = null

    // Giả sử mặc định bài thi là 15 phút (900 giây).
    // Nếu API của bạn có trường thời gian, hãy truyền vào đây.
    fun startTimer(durationMinutes: Int = 15) {
        timerJob?.cancel() // Hủy job cũ nếu có
        totalTimeAllowed = durationMinutes * 60
        timeLeft.intValue = totalTimeAllowed

        timerJob = viewModelScope.launch {
            while (timeLeft.intValue > 0) {
                delay(1000L) // Đợi 1 giây
                timeLeft.intValue -= 1
            }

            // HẾT GIỜ: Tự động nộp bài nếu chưa nộp
            if (timeLeft.intValue == 0 && !isSubmitting.value && submitSuccess.value != true) {
                Log.d("QUIZ_DEBUG", "Hết giờ! Đang tự động nộp bài...")
                submitQuizResult()
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }
    // =========================================================================

    fun fetchQuizzes(subjectCode: String, grade: Int) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                Log.d("QUIZ_DEBUG", "Đang gọi API lấy Quiz: subjectCode = '$subjectCode', grade = $grade")
                val response = quizRepository.getQuizzesBySubjectAndGrade(subjectCode, grade)

                if (response.success) {
                    val list = response.data ?: emptyList()
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

    fun setQuiz(quiz: QuizResponse) {
        currentQuiz.value = quiz
        userAnswers.clear()

        // Tự động lấy thời gian theo QuizType từ Backend.
        // Nếu vì lý do nào đó DB bị null, ta gán fallback mặc định là 15 phút.
        val timeLimit = quiz.durationMinutes ?: 15

        // Gọi hàm startTimer với số phút động
        startTimer(timeLimit)
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

        // Ngưng đồng hồ khi bắt đầu nộp bài
        stopTimer()

        viewModelScope.launch {
            isSubmitting.value = true
            try {
                val correctAnswers = calculateScore()

                // ĐÃ CẬP NHẬT: Gửi lên Backend thời gian thực tế học sinh đã làm
                val timeTakenSeconds = totalTimeAllowed - timeLeft.intValue

                val request = QuizSubmitRequest(
                    correctAnswers = correctAnswers,
                    totalQuestions = quiz.questions.size,
                    durationSeconds = timeTakenSeconds
                )

                val response = quizRepository.submitQuiz(quiz.id.toInt(), request)
                submitSuccess.value = response.success

            } catch (e: HttpException) {
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

    override fun onCleared() {
        super.onCleared()
        stopTimer() // Chống rò rỉ bộ nhớ khi ViewModel bị hủy
    }

    fun resetSubmitState() {
        submitSuccess.value = null
    }
}