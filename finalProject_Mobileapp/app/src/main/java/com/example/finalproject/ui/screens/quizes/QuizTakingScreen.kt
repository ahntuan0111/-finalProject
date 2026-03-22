package com.example.finalproject.ui.screens.quizes

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.ui.components.MathWebView
import com.example.finalproject.ui.viewmodel.QuizViewModel
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTakingScreen(navController: NavController, viewModel: QuizViewModel) {
    val quiz = viewModel.currentQuiz.value

    if (quiz == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(id = R.string.quiz_taking_error_data))
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val totalQuestions = quiz.questions.size
    val currentQuestion = quiz.questions[currentIndex]

    val isFirstQuestion = currentIndex == 0
    val isLastQuestion = currentIndex == totalQuestions - 1
    val isAllAnswered = viewModel.isAllAnswered()

    val submitSuccess by viewModel.submitSuccess
    val isSubmitting by viewModel.isSubmitting

    val timeLeft by viewModel.timeLeft
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    val timerColor = if (timeLeft <= 60) Color.Red else Color.Black

    var showTimeUpDialog by remember { mutableStateOf(false) }
    LaunchedEffect(timeLeft) {
        if (timeLeft == 0 && !isSubmitting) {
            showTimeUpDialog = true
        }
    }

    LaunchedEffect(submitSuccess) {
        if (submitSuccess == true) {
            viewModel.resetSubmitState()
            navController.navigate("quiz_result") {
                popUpTo("quiz_taking") { inclusive = true }
            }
        } else if (submitSuccess == false) {
            viewModel.resetSubmitState()
        }
    }

    if (showTimeUpDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(id = R.string.quiz_taking_time_up_title), fontWeight = FontWeight.Bold, color = Color.Red) },
            text = { Text(stringResource(id = R.string.quiz_taking_time_up_msg)) },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quiz.title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    Surface(
                        color = if (timeLeft <= 60) Color(0xFFFFE5E5) else Color(0xFFE6F7FF),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("⏳", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timeString,
                                color = timerColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(16.dp)) {

                if (isLastQuestion && !isAllAnswered && timeLeft > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFAAD14), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(id = R.string.quiz_taking_warning_unanswered), color = Color(0xFFFAAD14), fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { if (!isFirstQuestion) currentIndex-- },
                        enabled = !isFirstQuestion && !isSubmitting && timeLeft > 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(id = R.string.quiz_taking_btn_prev))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    if (isLastQuestion) {
                        Button(
                            onClick = { viewModel.submitQuizResult() },
                            enabled = isAllAnswered && !isSubmitting && timeLeft > 0,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52C41A))
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(stringResource(id = R.string.quiz_taking_btn_submit))
                            }
                        }
                    } else {
                        Button(
                            onClick = { currentIndex++ },
                            enabled = !isSubmitting && timeLeft > 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.quiz_taking_btn_next))
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
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
                    text = stringResource(id = R.string.quiz_taking_question_count, currentIndex + 1, totalQuestions),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(id = R.string.quiz_taking_answered_count, viewModel.userAnswers.size, totalQuestions),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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

            Text(stringResource(id = R.string.quiz_taking_select_answer), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            currentQuestion.choices.forEach { choice ->
                val isSelected = viewModel.userAnswers[currentQuestion.id] == choice.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable(enabled = !isSubmitting && timeLeft > 0) {
                            viewModel.selectAnswer(currentQuestion.id, choice.id)
                        },
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                if(!isSubmitting && timeLeft > 0) viewModel.selectAnswer(currentQuestion.id, choice.id)
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