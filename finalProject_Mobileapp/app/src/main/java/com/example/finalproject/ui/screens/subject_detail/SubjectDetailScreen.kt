package com.example.finalproject.ui.screens.subject_detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.di.TheoryMode
import com.example.finalproject.ui.components.AppNotificationManager
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.SubjectDetailViewModel

// --- MÀU SẮC ĐỊNH NGHĨA DỰA THEO MOCKUP ---
// Màu thẻ thực hành (chuyển sang dynamic)
@Composable
fun getGreenTint() = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
val GreenIcon = Color(0xFF4CAF50)
@Composable
fun getOrangeTint() = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
val OrangeIcon = Color(0xFFFF9800)
@Composable
fun getPurpleTint() = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
val PurpleIcon = Color(0xFF9C27B0)

@Composable
fun SubjectDetailScreen(
    navController: NavController,
    subjectCode: String,
    subjectName: String,
    viewModel: SubjectDetailViewModel = hiltViewModel()
) {
    val currentGrade by viewModel.currentGrade.collectAsState()
    val overview by viewModel.overview.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(currentGrade, subjectCode) {
        viewModel.fetchOverview(subjectCode, currentGrade)
    }

    val progressValue = overview?.progressPercent?.div(100f)?.toFloat() ?: 0f
    val nextLessonId = overview?.nextLessonId ?: -1L

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CustomTopBar(
                title = stringResource(id = R.string.subject_detail_top_bar_title, currentGrade, subjectName),
                onBack = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // 1. HERO BANNER
            HeroBanner(subjectName = subjectName, grade = currentGrade)

            Spacer(modifier = Modifier.height(28.dp))

            // 2. HEADER LỘ TRÌNH & XEM TẤT CẢ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.subject_detail_learning_path),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.subject_detail_view_all),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable {
                        navController.navigate(
                            AppRoutes.createTheoryRoute(
                                code = subjectCode,
                                name = subjectName,
                                mode = TheoryMode.THEORY
                            )
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val totalChapters = overview?.totalChapters ?: 0
            val totalLessons = overview?.totalLessons ?: 0

            // 3. THẺ LÝ THUYẾT
            PrimaryTheoryCard(
                progress = progressValue,
                totalChapters = totalChapters,
                totalLessons = totalLessons,
                onContinueClick = {
                    if (nextLessonId != -1L) {
                        navController.navigate(AppRoutes.createLessonDetailRoute(nextLessonId))
                    } else {
                        AppNotificationManager.info(context.getString(R.string.subject_detail_no_lessons_unlocked))
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. GRID 2 CỘT: GIẢI BÀI TẬP & QUIZ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BounceCard(
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                    bgColor = getGreenTint(),
                    icon = Icons.Rounded.CheckCircle,
                    iconColor = GreenIcon,
                    title = stringResource(id = R.string.subject_detail_exercise_title),
                    subtitle = stringResource(id = R.string.subject_detail_exercise_subtitle),
                    onClick = {
                        navController.navigate(
                            AppRoutes.createTheoryRoute(subjectCode, subjectName, TheoryMode.EXERCISE)
                        )
                    }
                )

                BounceCard(
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                    bgColor = getOrangeTint(),
                    icon = Icons.Rounded.Bolt,
                    iconColor = OrangeIcon,
                    title = stringResource(id = R.string.subject_detail_quiz_title),
                    subtitle = stringResource(id = R.string.subject_detail_quiz_subtitle),
                    onClick = {
                        navController.navigate(AppRoutes.createQuizFlowRoute(subjectCode, currentGrade))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. THẺ BỘ ĐỀ THI
            BounceCard(
                modifier = Modifier.fillMaxWidth().height(90.dp),
                bgColor = getPurpleTint(),
                icon = Icons.Rounded.Assignment,
                iconColor = PurpleIcon,
                title = stringResource(id = R.string.subject_detail_exam_title),
                subtitle = stringResource(id = R.string.subject_detail_exam_subtitle),
                isHorizontal = true,
                onClick = {
                    navController.navigate(AppRoutes.createDocumentListRoute(subjectCode, currentGrade))
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ======================= CÁC COMPONENT GIAO DIỆN =======================

@Composable
fun CustomTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, Color(0xFFF0F0F0), CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
        }

        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, Color(0xFFF0F0F0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.DarkMode, contentDescription = "Theme", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun HeroBanner(subjectName: String, grade: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1E3C72), Color(0xFF2A5298))))
            .padding(24.dp)
    ) {
        Text("∑", color = Color.White.copy(alpha = 0.1f), fontSize = 60.sp, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-40).dp, y = (-10).dp))
        Text("π", color = Color.White.copy(alpha = 0.1f), fontSize = 70.sp, modifier = Modifier.align(Alignment.CenterEnd).offset(x = 10.dp, y = (-20).dp))
        Text("√", color = Color.White.copy(alpha = 0.05f), fontSize = 80.sp, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-80).dp, y = 10.dp))

        Column {
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(stringResource(id = R.string.subject_detail_new_curriculum), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.subject_detail_hero_title, subjectName, grade),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.subject_detail_hero_desc),
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(Color.Black).border(2.dp, Color(0xFF2A5298), CircleShape))
                Box(modifier = Modifier.size(28.dp).offset(x = (-10).dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface).border(2.dp, Color(0xFF2A5298), CircleShape))
                Box(
                    modifier = Modifier.size(28.dp).offset(x = (-20).dp).clip(CircleShape).background(PrimaryBlue).border(2.dp, Color(0xFF2A5298), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+12", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Text(stringResource(id = R.string.subject_detail_hero_learning_with_you), color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, modifier = Modifier.offset(x = (-10).dp))
            }
        }
    }
}

@Composable
fun PrimaryTheoryCard(progress: Float, totalChapters: Int, totalLessons: Int, onContinueClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8F0FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = PrimaryBlue)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(stringResource(id = R.string.subject_detail_theory_title), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(if (totalChapters > 0 || totalLessons > 0) "$totalChapters chương • $totalLessons bài học" else stringResource(id = R.string.subject_detail_theory_subtitle), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(50))
                        .clickable { onContinueClick() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(stringResource(id = R.string.subject_detail_btn_continue), color = PrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.subject_detail_progress_label), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${(progress * 100).toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = PrimaryBlue,
                trackColor = Color(0xFFF0F0F0)
            )
        }
    }
}

@Composable
fun BounceCard(
    modifier: Modifier = Modifier,
    bgColor: Color,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    isHorizontal: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scale")

    Card(
        modifier = modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, bgColor.copy(alpha = 0.5f))
    ) {
        if (isHorizontal) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
            }
        }
    }
}