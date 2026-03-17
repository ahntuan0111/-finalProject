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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.data.remote.model.QuestionResponse
import com.example.finalproject.ui.viewmodel.QuizViewModel
import com.example.finalproject.ui.components.MathWebView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(navController: NavController, viewModel: QuizViewModel) {
    val quiz = viewModel.currentQuiz.value ?: return
    val totalQuestions = quiz.questions.size
    val correctAnswers = viewModel.calculateScore()

    val score = if (totalQuestions > 0) (correctAnswers.toFloat() / totalQuestions) * 10f else 0f
    val formattedScore = String.format("%.1f", score)

    // Xác định ID resource động để dùng trong stringResource
    val (resultColor, resultIcon, resultTextId) = when {
        score >= 8.0f -> Triple(Color(0xFF52C41A), Icons.Rounded.SentimentVerySatisfied, R.string.quiz_result_excellent)
        score >= 5.0f -> Triple(Color(0xFFFAAD14), Icons.Rounded.SentimentSatisfiedAlt, R.string.quiz_result_good)
        else -> Triple(Color(0xFFFF4D4F), Icons.Rounded.SentimentDissatisfied, R.string.quiz_result_needs_improvement)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.quiz_result_top_title), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF0F2F5))
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

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
                    Icon(
                        imageVector = resultIcon,
                        contentDescription = null,
                        tint = resultColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(stringResource(id = resultTextId), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = resultColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(id = R.string.quiz_result_your_score), fontSize = 14.sp, color = Color.Gray)
                    Text("$formattedScore / 10", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = resultColor)

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.Checklist, contentDescription = null, tint = Color(0xFF1890FF))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(stringResource(id = R.string.quiz_result_correct_answers), fontSize = 12.sp, color = Color.Gray)
                            Text("$correctAnswers / $totalQuestions", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.Timer, contentDescription = null, tint = Color(0xFFFAAD14))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(stringResource(id = R.string.quiz_result_time_taken), fontSize = 12.sp, color = Color.Gray)
                            Text("15:30", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Text(
                stringResource(id = R.string.quiz_result_detail_title),
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
                        Text(stringResource(id = R.string.quiz_result_btn_finish), fontSize = 16.sp, fontWeight = FontWeight.Bold)
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

    val isCorrect = selectedChoiceId == correctChoice?.id
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isCorrect) Color(0xFF52C41A) else Color(0xFFFF4D4F)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.quiz_result_question_number, question.orderNo), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {

                    question.contents.forEach { content ->
                        if (content.contentType == "TEXT") {
                            MathWebView(
                                htmlContent = content.contentValue,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val userChoice = question.choices.find { it.id == selectedChoiceId }
                    if (userChoice != null) {
                        Text(stringResource(id = R.string.quiz_result_you_selected), fontSize = 12.sp, color = Color.Gray)
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
                        Text(stringResource(id = R.string.quiz_result_skipped), color = Color(0xFFFF4D4F), fontWeight = FontWeight.Bold)
                    }

                    if (!isCorrect && correctChoice != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(stringResource(id = R.string.quiz_result_correct_is), fontSize = 12.sp, color = Color.Gray)
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

                    if (!question.explanation.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(stringResource(id = R.string.quiz_result_explanation), fontWeight = FontWeight.Bold, color = Color(0xFF1890FF))
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

@Composable
fun CardDefaults.outlinedCardBorder(isCorrect: Boolean) = androidx.compose.foundation.BorderStroke(
    1.dp, if (isCorrect) Color(0xFFB7EB8F) else Color(0xFFFFA39E)
)