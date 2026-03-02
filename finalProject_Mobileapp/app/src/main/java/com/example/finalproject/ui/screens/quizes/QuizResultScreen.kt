package com.example.finalproject.ui.screens.quizes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SentimentDissatisfied
import androidx.compose.material.icons.rounded.SentimentSatisfiedAlt
import androidx.compose.material.icons.rounded.SentimentVerySatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalproject.data.remote.model.QuestionResponse
import com.example.finalproject.ui.viewmodel.QuizViewModel
import com.example.finalproject.ui.components.MathWebView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(navController: NavController, viewModel: QuizViewModel) {
    val quiz = viewModel.currentQuiz.value ?: return
    val totalQuestions = quiz.questions.size
    val correctAnswers = viewModel.calculateScore()

    // Tính điểm trên thang 10
    val score = if (totalQuestions > 0) (correctAnswers.toFloat() / totalQuestions) * 10f else 0f
    val formattedScore = String.format("%.1f", score)

    // Đánh giá mức độ để đổi màu & Icon
    val (resultColor, resultIcon, resultText) = when {
        score >= 8.0f -> Triple(Color(0xFF52C41A), Icons.Rounded.SentimentVerySatisfied, "Xuất sắc!")
        score >= 5.0f -> Triple(Color(0xFFFAAD14), Icons.Rounded.SentimentSatisfiedAlt, "Khá tốt!")
        else -> Triple(Color(0xFFFF4D4F), Icons.Rounded.SentimentDissatisfied, "Cần cố gắng hơn!")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kết quả kiểm tra", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF0F2F5))
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // ==========================================
            // HEADER: THỐNG KÊ KẾT QUẢ ĐẸP MẮT
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon trạng thái cảm xúc
                    Icon(
                        imageVector = resultIcon,
                        contentDescription = null,
                        tint = resultColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Lời khen
                    Text(resultText, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = resultColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Điểm số to
                    Text("ĐIỂM CỦA BẠN", fontSize = 14.sp, color = Color.Gray)
                    Text("$formattedScore / 10", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = resultColor)

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Dòng thống kê chi tiết (Số câu & Thời gian)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Cột: Trả lời đúng
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.Checklist, contentDescription = null, tint = Color(0xFF1890FF))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Trả lời đúng", fontSize = 12.sp, color = Color.Gray)
                            Text("$correctAnswers / $totalQuestions", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        // Cột: Thời gian (Giả lập thời gian làm bài, sau này bạn tự thay biến thực tế vào)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.Timer, contentDescription = null, tint = Color(0xFFFAAD14))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Thời gian", fontSize = 12.sp, color = Color.Gray)
                            // Hiện tại gán cứng, nếu có biến time từ ViewModel thì thay vào đây
                            Text("15:30", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ==========================================
            // DANH SÁCH CHI TIẾT TỪNG CÂU
            // ==========================================
            Text(
                "Chi tiết bài làm",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                items(quiz.questions) { question ->
                    ResultQuestionItem(question, viewModel)
                }
                item {
                    Button(
                        onClick = { navController.popBackStack("quiz_list", false) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp).height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hoàn thành & Quay lại", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ResultQuestionItem(question: QuestionResponse, viewModel: QuizViewModel) {
    val selectedChoiceId = viewModel.userAnswers[question.id]
    val correctChoice = question.choices.find { it.correct }

    // KIỂM TRA ĐÚNG/SAI
    val isCorrect = selectedChoiceId == correctChoice?.id

    // Mặc định câu sai sẽ mở sẵn, câu đúng thu gọn
    var isExpanded by remember { mutableStateOf(!isCorrect) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(isCorrect),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Thanh tiêu đề (Câu số + Icon Đúng/Sai)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isCorrect) Color(0xFF52C41A) else Color(0xFFFF4D4F)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Câu ${question.orderNo}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            // Phần nội dung chi tiết (Sẽ ẩn nếu isExpanded = false)
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {

                    // 1. ĐỀ BÀI (Dùng MathWebView)
                    question.contents.forEach { content ->
                        if (content.contentType == "TEXT") {
                            MathWebView(
                                htmlContent = content.contentValue,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. ĐÁP ÁN BẠN CHỌN
                    val userChoice = question.choices.find { it.id == selectedChoiceId }
                    if (userChoice != null) {
                        Text("Bạn đã chọn:", fontSize = 12.sp, color = Color.Gray)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .background(
                                    color = if (isCorrect) Color(0xFFF6FFED) else Color(0xFFFFF1F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            MathWebView(
                                htmlContent = userChoice.contentValue,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            )
                        }
                    } else {
                        Text("Bạn đã bỏ qua câu này", color = Color(0xFFFF4D4F), fontWeight = FontWeight.Bold)
                    }

                    // 3. NẾU SAI -> HIỂN THỊ THÊM ĐÁP ÁN ĐÚNG
                    if (!isCorrect && correctChoice != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Đáp án đúng là:", fontSize = 12.sp, color = Color.Gray)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .background(Color(0xFFF6FFED), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            MathWebView(
                                htmlContent = correctChoice.contentValue,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            )
                        }
                    }

                    // 4. LỜI GIẢI THÍCH (Nếu có)
                    if (!question.explanation.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Lời giải chi tiết:", fontWeight = FontWeight.Bold, color = Color(0xFF1890FF))
                        Spacer(modifier = Modifier.height(8.dp))
                        MathWebView(
                            htmlContent = question.explanation ?: "",
                            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                        )
                    }
                }
            }
        }
    }
}

// Viền thẻ: Xanh lá (đúng) - Đỏ (sai)
@Composable
fun CardDefaults.outlinedCardBorder(isCorrect: Boolean) = androidx.compose.foundation.BorderStroke(
    1.dp, if (isCorrect) Color(0xFFB7EB8F) else Color(0xFFFFA39E)
)