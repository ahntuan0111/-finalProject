package com.example.finalproject.ui.screens.theory

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.data.remote.model.ChapterDto
import com.example.finalproject.data.remote.model.LessonDto
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.UserViewModel

// --- MÀU SẮC ĐÃ ĐƯỢC CHỈNH SỬA THEO THEME XANH DƯƠNG ---
// Màu nền nhạt khi mở rộng chương (Xanh dương rất nhạt)
val ExpandBlueBg = Color(0xFFE3F2FD)
// Màu xanh lá dùng cho icon hoàn thành (Giữ nguyên để biểu thị thành công)
val SuccessGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheoryScreen(
    navController: NavController,
    subjectCode: String,
    subjectName: String,
    viewModel: TheoryViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val chapters by viewModel.chapters.collectAsState()
    val expandedIds by viewModel.expandedChapterIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val grade by userViewModel.userGrade.collectAsState()

    LaunchedEffect(subjectCode) {
        viewModel.fetchChapters(subjectCode)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Lý thuyết - $subjectName ${grade?.let { "($it)" } ?: ""}",
                        // SỬA: Đổi màu chữ thành Đen cho hợp với nền Trắng
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        // SỬA: Đổi màu icon Back thành Đen
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    // SỬA: Đổi nền TopBar thành Trắng
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFFAFAFA) // Nền xám nhạt chung của App
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFAFAFA))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryBlue
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(chapters) { chapter ->
                        ChapterItem(
                            chapter = chapter,
                            isExpanded = expandedIds.contains(chapter.id),
                            onToggle = { viewModel.toggleChapter(chapter.id) },
                            onLessonClick = { lesson ->
                                navController.navigate(AppRoutes.createLessonDetailRoute(lesson.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterItem(
    chapter: ChapterDto,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onLessonClick: (LessonDto) -> Unit
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "ArrowRotation"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            // SỬA: Nếu mở rộng thì dùng màu Xanh nhạt, đóng thì dùng Trắng
            containerColor = if (isExpanded) ExpandBlueBg else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            // --- HEADER CỦA CHƯƠNG ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // SỬA: Số thứ tự dùng màu PrimaryBlue
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chapter.orderNo.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = chapter.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = Color.Gray,
                    modifier = Modifier.rotate(rotationState)
                )
            }

            // --- DANH SÁCH BÀI HỌC ---
            if (isExpanded) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    if (chapter.lessons.isNullOrEmpty()) {
                        Text(
                            text = "Chưa có bài học nào.",
                            modifier = Modifier.padding(start = 72.dp, bottom = 16.dp),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    } else {
                        chapter.lessons.forEach { lesson ->
                            LessonItem(lesson = lesson, onClick = { onLessonClick(lesson) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonItem(lesson: LessonDto, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // SỬA: Icon sách dùng màu PrimaryBlue
        Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = lesson.title,
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )

        if (lesson.isCompleted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = SuccessGreen, // Giữ màu xanh lá cho trạng thái hoàn thành
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}