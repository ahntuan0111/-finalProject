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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.data.remote.model.ChapterDto
import com.example.finalproject.data.remote.model.LessonDto
import com.example.finalproject.di.TheoryMode
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.TheoryViewModel
import com.example.finalproject.ui.viewmodel.UserViewModel

// --- MÀU SẮC ---
@Composable
fun getExpandBlueBg() = MaterialTheme.colorScheme.primaryContainer
val SuccessGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheoryScreen(
    navController: NavController,
    subjectCode: String,
    subjectName: String,
    mode: String = TheoryMode.THEORY,
    viewModel: TheoryViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val chapters by viewModel.chapters.collectAsState()
    val expandedIds by viewModel.expandedChapterIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val grade by userViewModel.currentGrade.collectAsState()

    // Chuyển "Giải bài tập" hoặc "Lý thuyết" thành đa ngôn ngữ
    val screenTitlePrefix = if (mode == TheoryMode.EXERCISE) {
        stringResource(id = R.string.theory_screen_title_exercise)
    } else {
        stringResource(id = R.string.theory_screen_title_theory)
    }

    LaunchedEffect(subjectCode) {
        viewModel.fetchChapters(subjectCode)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        // Tiêu đề động đa ngôn ngữ
                        text = stringResource(
                            id = R.string.theory_screen_top_bar_title,
                            screenTitlePrefix, subjectName, grade
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.common_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
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
                                if (mode == TheoryMode.EXERCISE) {
                                    navController.navigate(AppRoutes.createExerciseListRoute(lesson.id))
                                } else {
                                    navController.navigate(AppRoutes.createLessonDetailRoute(lesson.id))
                                }
                            },
                            mode = mode
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
    onLessonClick: (LessonDto) -> Unit,
    mode: String
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "ArrowRotation"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) getExpandBlueBg() else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    text = chapter.title ?: "Chương (Chưa có tên)", // BỔ SUNG ?:
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(id = R.string.theory_screen_desc_expand),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(rotationState)
                )
            }

            if (isExpanded) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    if (chapter.lessons.isNullOrEmpty()) {
                        Text(
                            text = stringResource(id = R.string.theory_screen_no_content),
                            modifier = Modifier.padding(start = 72.dp, bottom = 16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    } else {
                        chapter.lessons.forEach { lesson ->
                            LessonItem(
                                lesson = lesson,
                                onClick = { onLessonClick(lesson) },
                                mode = mode
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonItem(lesson: LessonDto, onClick: () -> Unit, mode: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = if (mode == TheoryMode.EXERCISE) Icons.Default.Assignment else Icons.Default.MenuBook

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = lesson.title ?: "Bài học (Chưa có tên)", // BỔ SUNG ?:
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )

        if (lesson.isCompleted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(id = R.string.theory_screen_desc_completed),
                tint = SuccessGreen,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(id = R.string.theory_screen_desc_go),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}