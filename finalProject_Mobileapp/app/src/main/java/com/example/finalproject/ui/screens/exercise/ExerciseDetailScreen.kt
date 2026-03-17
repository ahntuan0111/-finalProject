package com.example.finalproject.ui.screens.exercise

import android.annotation.SuppressLint
import android.util.Base64
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.finalproject.R
import com.example.finalproject.data.remote.model.ExerciseDto
import com.example.finalproject.data.remote.model.ExerciseSolutionDto
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.ExerciseDetailViewModel

// IP máy chủ (Thay đổi tùy môi trường)
const val BASE_IMAGE_URL = "http://10.0.2.2:8080"

fun getFullImageUrl(path: String?): String {
    if (path.isNullOrEmpty()) return ""
    return if (path.startsWith("http")) path else "$BASE_IMAGE_URL$path"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    lessonId: Long,
    viewModel: ExerciseDetailViewModel = hiltViewModel()
) {
    val state by viewModel.exercises.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isCompletedState by viewModel.isCompleted.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.fetchExercises(lessonId)
    }

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
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.common_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFAFAFA)
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
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(exercises) { exercise ->
                            ExerciseItem(exercise = exercise)
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))

                            val finalIsCompleted = isCompletedState

                            Button(
                                onClick = { viewModel.markAsCompleted(lessonId) },
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
                                    Text(
                                        stringResource(id = R.string.exercise_detail_btn_complete),
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
fun ExerciseItem(exercise: ExerciseDto) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (exercise.solutions.isNullOrEmpty()) {
                        Text(
                            text = stringResource(id = R.string.exercise_item_solution_empty),
                            fontSize = 13.sp,
                            color = Color.Gray
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
fun LatexMathView(latex: String, modifier: Modifier = Modifier) {
    val dollarSign = "$"

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
            
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.css">
            
            <script defer src="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.js"></script>
            <script defer src="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/contrib/auto-render.min.js"></script>
            
            <style>
                body { 
                    font-size: 16px; 
                    color: #000000; 
                    font-family: 'Roboto', sans-serif; 
                    margin: 0; 
                    padding: 0;
                    background-color: transparent;
                    overflow-wrap: break-word; 
                }
                img { max-width: 100%; height: auto; display: block; margin: 4px 0; }
                .katex { font-size: 1.1em; }
            </style>
            
            <script>
                document.addEventListener("DOMContentLoaded", function() {
                    renderMathInElement(document.body, {
                        delimiters: [
                            {left: "$dollarSign$dollarSign", right: "$dollarSign$dollarSign", display: true},
                            {left: "$dollarSign", right: "$dollarSign", display: false},
                            {left: "\\(", right: "\\)", display: false},
                            {left: "\\[", right: "\\]", display: true}
                        ],
                        throwOnError : false
                    });
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

                setBackgroundColor(android.graphics.Color.TRANSPARENT)
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
            val encodedHtml = Base64.encodeToString(htmlContent.toByteArray(), Base64.NO_PADDING)
            webView.loadData(encodedHtml, "text/html", "base64")
        }
    )
}