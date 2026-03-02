package com.example.finalproject.ui.screens.quizes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalproject.ui.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(
    navController: NavController,
    viewModel: QuizViewModel,
    subjectCode: String,
    grade: Int
) {
    // Quan sát State từ ViewModel
    val quizList by viewModel.quizList.collectAsState()
    val isLoading by viewModel.isLoading

    // Gọi API tự động khi màn hình mở lên
    LaunchedEffect(subjectCode, grade) {
        viewModel.fetchQuizzes(subjectCode, grade)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bài Kiểm Tra - Khối $grade") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF0F2F5))
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (quizList.isEmpty()) {
                Text(
                    text = "Chưa có bài kiểm tra nào cho môn học này.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(quizList) { quiz ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clickable {
                                    viewModel.setQuiz(quiz)
                                    navController.navigate("quiz_taking")
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = quiz.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF1890FF),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Chương: ${quiz.chapterTitle}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Loại: ${quiz.quizTypeName}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Text(text = "${quiz.questions.size} câu hỏi", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}