package com.example.finalproject.ui.screens.exercise

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Base64
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.finalproject.R
import com.example.finalproject.data.remote.model.ExerciseDto
import com.example.finalproject.data.remote.model.ExerciseSolutionDto
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.ExerciseDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons

// IP máy chủ (Thay đổi tùy môi trường)
const val BASE_IMAGE_URL = "http://192.168.0.144:8080"

fun getFullImageUrl(path: String?): String {
    if (path.isNullOrEmpty()) return ""
    return if (path.startsWith("http")) path else "http://192.168.0.144:8080$path"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    lessonId: Long,
    exerciseId: Long,
    viewModel: ExerciseDetailViewModel = hiltViewModel()
) {
    val state by viewModel.exercises.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isCompletedState by viewModel.isCompleted.collectAsState()
    val validationResult by viewModel.validationResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // ĐÃ SỬA: Thêm biến isSubmitting để khắc phục lỗi Unresolved reference
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    var showCamera by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // Camera permission request
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
        if (isGranted) showCamera = true
    }

    // 15-Minute Timer
    var timeLeft by remember { mutableIntStateOf(15 * 60) } // 900 seconds

    LaunchedEffect(lessonId) {
        viewModel.fetchExercises(lessonId)
    }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    if (showCamera) {
        CameraCaptureScreen(
            onImageCaptured = { uri ->
                showCamera = false
                capturedImageUri = uri
                // Start AI Validation and Firebase Upload workflow
                viewModel.submitExerciseImage(context, exerciseId, lessonId, uri)
            },
            onCancel = { showCamera = false }
        )
        return
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.exercise_detail_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.common_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryBlue
                )
            } else {
                val exercises = state
                if (exercises.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.exercise_detail_empty),
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Timer UI
                        item {
                            val mins = timeLeft / 60
                            val secs = timeLeft % 60
                            val timeStr = String.format("%02d:%02d", mins, secs)

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.ExpandLess, contentDescription = null, tint = Color(0xFFFF9800))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Thời gian làm bài: $timeStr",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100),
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        val isPassedState = validationResult?.isValid == true
                        val finalIsCompleted = isCompletedState

                        // Validation UI
                        if (errorMessage != null) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2F0)),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            "Lỗi kết nối",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFCF1322),
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            errorMessage ?: "",
                                            color = Color(0xFFCF1322),
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { viewModel.resetValidationResult() },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFCF1322))
                                        ) {
                                            Text("Đã hiểu, chụp ảnh lại")
                                        }
                                    }
                                }
                            }
                        } else if (validationResult != null) {
                            item {
                                val isPassed = validationResult?.isValid == true
                                val cardColor = if (isPassed) Color(0xFFF6FFED) else Color(0xFFFFF2F0)
                                val textColor = if (isPassed) Color(0xFF389E0D) else Color(0xFFCF1322)

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = cardColor),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            if (isPassed) "AI Đánh giá: Hợp lệ!" else "AI Đánh giá: Cần làm lại",
                                            fontWeight = FontWeight.Bold,
                                            color = textColor,
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            validationResult?.reason ?: "",
                                            color = textColor,
                                            fontSize = 14.sp
                                        )
                                        if (!isPassed) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = { viewModel.resetValidationResult() },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = textColor)
                                            ) {
                                                Text("Đã hiểu, chụp ảnh lại")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        val currentExercise = exercises.find { it.id == exerciseId }
                        if (currentExercise != null) {
                            item {
                                ExerciseItem(
                                    exercise = currentExercise,
                                    timeLeft = timeLeft,
                                    snackbarHostState = snackbarHostState,
                                    isCompleted = finalIsCompleted
                                )
                            }
                        } else {
                            item {
                                Text("Không tìm thấy bài tập", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    if (hasCameraPermission) {
                                        showCamera = true
                                    } else {
                                        launcher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                enabled = !finalIsCompleted && !isSubmitting,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (finalIsCompleted) Color(0xFF52C41A) else PrimaryBlue,
                                    disabledContainerColor = if (finalIsCompleted) Color(0xFF52C41A).copy(alpha = 0.8f) else Color.Gray
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Đang dùng AI chấm bài...", color = Color.White, fontWeight = FontWeight.Bold)
                                } else if (finalIsCompleted) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        stringResource(id = R.string.exercise_detail_btn_completed),
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Chụp bài tự luận (Validation AI)",
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(
    exercise: ExerciseDto,
    timeLeft: Int,
    snackbarHostState: SnackbarHostState,
    isCompleted: Boolean
) {
    var isExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- Header ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(PrimaryBlue, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.exercise_item_question_num, exercise.orderNo),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Nội dung câu hỏi ---
            ContentRenderer(type = exercise.questionType, value = exercise.questionValue)

            Spacer(modifier = Modifier.height(16.dp))

            // --- Gợi ý (Hints) - Luôn hiển thị, nhấn vào mới mở nội dung ---
            val isHint1Unlocked = timeLeft <= 13 * 60 // đã qua 2 phút
            val isHint2Unlocked = timeLeft <= 10 * 60 // đã qua 5 phút
            val isHint3Unlocked = timeLeft <= 7 * 60  // đã qua 8 phút

            if (!exercise.hint1.isNullOrEmpty()) {
                HintBlock(
                    title = "Gợi ý 1",
                    content = exercise.hint1,
                    isUnlocked = isHint1Unlocked,
                    unlockMinutes = 2,
                    onLockedClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("⏰ Gợi ý 1 sẽ mở sau khi làm bài được 2 phút")
                        }
                    }
                )
            }
            if (!exercise.hint2.isNullOrEmpty()) {
                HintBlock(
                    title = "Gợi ý 2",
                    content = exercise.hint2,
                    isUnlocked = isHint2Unlocked,
                    unlockMinutes = 5,
                    onLockedClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("⏰ Gợi ý 2 sẽ mở sau khi làm bài được 5 phút")
                        }
                    }
                )
            }
            if (!exercise.hint3.isNullOrEmpty()) {
                HintBlock(
                    title = "Gợi ý 3",
                    content = exercise.hint3,
                    isUnlocked = isHint3Unlocked,
                    unlockMinutes = 8,
                    onLockedClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("⏰ Gợi ý 3 sẽ mở sau khi làm bài được 8 phút")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            if (isCompleted) {
                // --- Nút Xem Lời Giải ---
                Button(
                    onClick = { isExpanded = !isExpanded },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isExpanded) Color(0xFFEEEEEE) else Color(0xFFE3F2FD),
                        contentColor = if (isExpanded) Color.Gray else PrimaryBlue
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    val btnText = if (isExpanded) {
                        stringResource(id = R.string.exercise_item_btn_hide_solution)
                    } else {
                        stringResource(id = R.string.exercise_item_btn_show_solution)
                    }
                    Text(btnText)
                }

                // --- Danh sách Lời giải ---
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.exercise_item_solution_guide),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (exercise.solutions.isNullOrEmpty()) {
                            Text(
                                text = stringResource(id = R.string.exercise_item_solution_empty),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            exercise.solutions.sortedBy { it.solutionOrder }.forEach { solution ->
                                SolutionItem(solution)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HintBlock(
    title: String,
    content: String,
    isUnlocked: Boolean,
    unlockMinutes: Int,
    onLockedClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xFFFFFBE6) else Color(0xFFF5F5F5)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = {
            if (isUnlocked) {
                isExpanded = !isExpanded
            } else {
                onLockedClick()
            }
        }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isUnlocked) Icons.Default.Lightbulb else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) Color(0xFFFAAD14) else Color(0xFFBFBFBF),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) Color(0xFFD48806) else Color(0xFF8C8C8C),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                if (!isUnlocked) {
                    Text(
                        text = "Mở khoá sau ${unlockMinutes} phút",
                        fontSize = 11.sp,
                        color = Color(0xFFBFBFBF)
                    )
                } else {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFFD48806),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Nội dung chỉ hiện khi đã mở khoá VÀ đã nhấn expand
            AnimatedVisibility(visible = isUnlocked && isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(6.dp))
                    LatexMathView(latex = content, modifier = Modifier)
                }
            }
        }
    }
}

@Composable
fun SolutionItem(solution: ExerciseSolutionDto) {
    Column {
        if (solution.solutionOrder > 0) {
            Text(
                text = stringResource(id = R.string.exercise_item_solution_step, solution.solutionOrder),
                fontSize = 13.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        ContentRenderer(type = solution.solutionType, value = solution.solutionValue)
    }
}

@Composable
fun ContentRenderer(type: String?, value: String?) {
    val safeType = type ?: "TEXT"
    val safeValue = value ?: ""

    if (safeType == "IMAGE") {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(getFullImageUrl(safeValue))
                .crossfade(true)
                .build(),
            contentDescription = stringResource(id = R.string.exercise_item_content_desc_image),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.FillWidth
        )
    } else {
        LatexMathView(latex = safeValue, modifier = Modifier)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LatexMathView(
    latex: String,
    modifier: Modifier = Modifier,
    // Nhận isDark từ parent thông qua MaterialTheme — khớp đúng theme user chọn
    isDark: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f
) {
    val textColorHex = if (isDark) "#F1F5F9" else "#1E293B"
    val bgColorHex   = if (isDark) "#0F172A" else "#FFFFFF"
    val dollarSign = "${'$'}"

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="file:///android_asset/katex/katex.min.css">
            <script src="file:///android_asset/katex/katex.min.js"></script>
            <script src="file:///android_asset/katex/auto-render.min.js"></script>
            <style>
                * { box-sizing: border-box; }
                html, body {
                    margin: 0; padding: 4px 8px;
                    background-color: $bgColorHex;
                }
                body { 
                    font-size: 16px; 
                    color: $textColorHex; 
                    font-family: 'Roboto', sans-serif; 
                    overflow-wrap: break-word; 
                    line-height: 1.7;
                }
                h1, h2, h3, p, span, li { color: $textColorHex !important; }
                .katex { font-size: 1.1em; }
                .katex-display { overflow-x: auto; }
            </style>
            <script>
                document.addEventListener("DOMContentLoaded", function() {
                    if (typeof renderMathInElement !== 'undefined') {
                        renderMathInElement(document.body, {
                            delimiters: [
                                {left: "$dollarSign$dollarSign", right: "$dollarSign$dollarSign", display: true},
                                {left: "$dollarSign", right: "$dollarSign", display: false},
                                {left: "\\(", right: "\\)", display: false},
                                {left: "\\[", right: "\\]", display: true}
                            ],
                            throwOnError : false
                        });
                    }
                });
            </script>
        </head>
        <body>
            $latex
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 40.dp),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = false
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                // Sửa lỗi: dùng màu nền khớp theme thay vì TRANSPARENT
                setBackgroundColor(android.graphics.Color.parseColor(bgColorHex))
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                    }
                }
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "UTF-8", null)
        }
    )
}