package com.example.finalproject.ui.screens.class_selection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.ui.components.AppNotificationManager
import com.example.finalproject.ui.components.PrimaryButton
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.theme.TextGray
import com.example.finalproject.ui.viewmodel.ClassSelectionState
import com.example.finalproject.ui.viewmodel.ClassSelectionViewModel

@Composable
fun ClassSelectionScreen(
    navController: NavController,
    viewModel: ClassSelectionViewModel = hiltViewModel()
) {
    var selectedGrade by remember { mutableIntStateOf(0) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is ClassSelectionState.Success) {
            navController.navigate("home") {
                popUpTo("class_selection") { inclusive = true }
                popUpTo("login") { inclusive = true }
            }
        } else if (state is ClassSelectionState.Error) {
            AppNotificationManager.error((state as ClassSelectionState.Error).message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(56.dp))

        // ===== Header =====
        Text(
            text = stringResource(id = R.string.class_selection_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )

        Text(
            text = stringResource(id = R.string.class_selection_subtitle),
            fontSize = 15.sp,
            color = TextGray,
            modifier = Modifier.padding(top = 12.dp, bottom = 36.dp)
        )

        // ===== Grid chọn lớp (2x2) =====
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GradeOption(grade = 6, selected = selectedGrade) {
                    selectedGrade = 6
                }
                GradeOption(grade = 7, selected = selectedGrade) {
                    selectedGrade = 7
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GradeOption(grade = 8, selected = selectedGrade) {
                    selectedGrade = 8
                }
                GradeOption(grade = 9, selected = selectedGrade) {
                    selectedGrade = 9
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // ===== Motivational text =====
        Text(
            text = stringResource(id = R.string.class_selection_motivation),
            fontSize = 13.sp,
            color = TextGray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // ===== Button =====
        PrimaryButton(
            text = stringResource(id = R.string.class_selection_btn_start),
            onClick = {
                if (selectedGrade != 0) {
                    viewModel.selectGrade(selectedGrade)
                } else {
                    AppNotificationManager.error("Vui lòng chọn lớp học của bạn")
                }
            },
            isLoading = state is ClassSelectionState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RowScope.GradeOption(
    grade: Int,
    selected: Int,
    onClick: () -> Unit
) {
    val isSelected = grade == selected

    Box(
        modifier = Modifier
            .weight(1f)
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) PrimaryBlue.copy(alpha = 0.9f)
                else Color(0xFFF8FAFC)
            )
            .border(
                width = 2.dp,
                color = if (isSelected) PrimaryBlue else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = when (grade) {
                    6 -> "🎒"
                    7 -> "📗"
                    8 -> "📘"
                    else -> "📙"
                },
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Text hiển thị "Lớp 6" (hoặc "Grade 6" bên English)
            Text(
                text = stringResource(id = R.string.class_selection_grade_format, grade),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else PrimaryBlue
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.class_selection_selected),
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}