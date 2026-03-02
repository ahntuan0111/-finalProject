package com.example.finalproject.ui.screens.quizes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalproject.ui.components.MathWebView
import com.example.finalproject.ui.viewmodel.QuizViewModel
import kotlinx.coroutines.delay // Import thêm nếu cần

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTakingScreen(navController: NavController, viewModel: QuizViewModel) {
    val quiz = viewModel.currentQuiz.value

    if (quiz == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Lỗi dữ liệu bài thi")
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val totalQuestions = quiz.questions.size
    val currentQuestion = quiz.questions[currentIndex]

    val isFirstQuestion = currentIndex == 0
    val isLastQuestion = currentIndex == totalQuestions - 1
    val isAllAnswered = viewModel.isAllAnswered()

    // Lắng nghe trạng thái nộp bài từ ViewModel
    val submitSuccess by viewModel.submitSuccess
    val isSubmitting by viewModel.isSubmitting

    // =========================================================================
    // XỬ LÝ ĐIỀU HƯỚNG SAU KHI NỘP BÀI THÀNH CÔNG
    // =========================================================================
    LaunchedEffect(submitSuccess) {
        if (submitSuccess == true) {
            // Nộp thành công -> Xóa trạng thái và chuyển trang
            viewModel.resetSubmitState()
            navController.navigate("quiz_result") {
                // Xóa màn hình làm bài khỏi ngăn xếp (ngăn user bấm back quay lại)
                popUpTo("quiz_taking") { inclusive = true }
            }
        } else if (submitSuccess == false) {
            // Nếu có lỗi (có thể hiển thị Snackbar/Toast ở đây)
            viewModel.resetSubmitState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quiz.title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF0F2F5))
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(Color.White).padding(16.dp)) {

                if (isLastQuestion && !isAllAnswered) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFAAD14), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bạn chưa trả lời hết các câu hỏi!", color = Color(0xFFFAAD14), fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { if (!isFirstQuestion) currentIndex-- },
                        enabled = !isFirstQuestion && !isSubmitting, // Khóa nút khi đang nộp bài
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Câu trước")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    if (isLastQuestion) {
                        Button(
                            // =========================================================
                            // ĐÃ SỬA CHỖ NÀY: GỌI API THAY VÌ CHUYỂN TRANG NGAY LẬP TỨC
                            // =========================================================
                            onClick = { viewModel.submitQuizResult() },
                            enabled = isAllAnswered && !isSubmitting,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52C41A))
                        ) {
                            if (isSubmitting) {
                                // Hiển thị vòng xoay loading khi đang gọi API
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Nộp bài")
                            }
                        }
                    } else {
                        Button(
                            onClick = { currentIndex++ },
                            enabled = !isSubmitting, // Khóa nút khi đang nộp bài
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Câu tiếp")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Câu ${currentIndex + 1} / $totalQuestions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Đã trả lời: ${viewModel.userAnswers.size}/$totalQuestions",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / totalQuestions },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    currentQuestion.contents.forEach { content ->
                        if (content.contentType == "TEXT") {
                            MathWebView(
                                htmlContent = content.contentValue,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Chọn đáp án:", fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            currentQuestion.choices.forEach { choice ->
                val isSelected = viewModel.userAnswers[currentQuestion.id] == choice.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable(enabled = !isSubmitting) { // Không cho click khi đang nộp bài
                            viewModel.selectAnswer(currentQuestion.id, choice.id)
                        },
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFE6F7FF) else Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                if(!isSubmitting) viewModel.selectAnswer(currentQuestion.id, choice.id)
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (choice.contentType == "TEXT") {
                            MathWebView(
                                htmlContent = choice.contentValue,
                                modifier = Modifier.weight(1f).wrapContentHeight()
                            )
                        }
                    }
                }
            }
        }
    }
}