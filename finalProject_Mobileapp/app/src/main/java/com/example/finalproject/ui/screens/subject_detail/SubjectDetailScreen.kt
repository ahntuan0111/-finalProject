package com.example.finalproject.ui.screens.subject_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.di.TheoryMode
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.theme.TextGray
import com.example.finalproject.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    navController: NavController,
    subjectCode: String,
    subjectName: String,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val grade by userViewModel.userGrade.collectAsState()
    val displayGrade = grade ?: 6

    val options = listOf(
        StudyOption("Lý thuyết", R.drawable.theory, Color(0xFFE3F2FD), 0.25f),
        StudyOption("Giải bài tập", R.drawable.homework, Color(0xFFE8F5E9)),
        StudyOption("Quiz", R.drawable.quiz, Color(0xFFFFF3E0)),
        StudyOption("Bộ đề thi", R.drawable.exam, Color(0xFFF3E5F5))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Khối $displayGrade - $subjectName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text(
                text = "$subjectName cho Khối $displayGrade",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Chọn nội dung bạn muốn học bên dưới:",
                fontSize = 16.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(options) { option ->
                    StudyOptionCard(option = option) {
                        when (option.title) {
                            "Lý thuyết" -> {
                                navController.navigate(
                                    AppRoutes.createTheoryRoute(
                                        code = subjectCode,
                                        name = subjectName,
                                        mode = TheoryMode.THEORY
                                    )
                                )
                            }
                            "Giải bài tập" -> {
                                navController.navigate(
                                    AppRoutes.createTheoryRoute(
                                        code = subjectCode,
                                        name = subjectName,
                                        mode = TheoryMode.EXERCISE
                                    )
                                )
                            }
                            "Quiz" -> { /* TODO */ }
                            "Bộ đề thi" -> {
                                // ĐIỀU HƯỚNG SANG MÀN HÌNH DANH SÁCH TÀI LIỆU
                                navController.navigate(
                                    AppRoutes.createDocumentListRoute(
                                        code = subjectCode,
                                        grade = displayGrade
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class StudyOption(
    val title: String,
    val imageRes: Int,
    val colorBg: Color,
    val progress: Float? = null
)

@Composable
fun StudyOptionCard(option: StudyOption, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x1A000000)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(option.colorBg),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = option.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = option.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                textAlign = TextAlign.Center
            )
            if (option.progress != null) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { option.progress },
                    modifier = Modifier.fillMaxWidth(0.8f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = PrimaryBlue,
                    trackColor = Color(0xFFEEEEEE),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${(option.progress * 100).toInt()}%", fontSize = 12.sp, color = TextGray)
            }
        }
    }
}