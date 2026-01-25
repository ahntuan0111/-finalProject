package com.example.finalproject.ui.screens.exercise

import android.annotation.SuppressLint
import android.util.Base64 // <-- QUAN TRỌNG: Thêm import này
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.finalproject.data.remote.model.ExerciseDto
import com.example.finalproject.data.remote.model.ExerciseSolutionDto
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.ExerciseDetailViewModel
import com.example.finalproject.utils.Resource

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

    LaunchedEffect(lessonId) {
        viewModel.fetchExercises(lessonId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Luyện tập", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                        text = "Chưa có bài tập nào.",
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
            // --- Header: Câu hỏi số X ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(PrimaryBlue, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Câu ${exercise.orderNo}",
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
                Text(if (isExpanded) "Ẩn lời giải" else "Xem lời giải chi tiết")
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
                        text = "Hướng dẫn giải:",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (exercise.solutions.isNullOrEmpty()) {
                        Text("Đang cập nhật lời giải...", fontSize = 13.sp, color = Color.Gray)
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
                text = "Bước ${solution.solutionOrder}:",
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
            contentDescription = "Content Image",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.FillWidth
        )
    } else {
        // Sử dụng LatexMathView để hiển thị công thức
        LatexMathView(latex = safeValue)
    }
}

// --- Component Render LaTeX sử dụng WebView và MathJax (SỬ DỤNG BASE64 ĐỂ FIX LỖI) ---
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LatexMathView(latex: String, modifier: Modifier = Modifier) {
    // Sửa lỗi quan trọng: Dùng ${'$'} để Kotlin không hiểu nhầm là biến
    val dollarSign = "$"

    // Sử dụng KaTeX (nhẹ hơn MathJax) để render nhanh trong LazyColumn
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
                    overflow-wrap: break-word; /* Tránh tràn chữ */
                }
                /* Căn chỉnh ảnh */
                img { max-width: 100%; height: auto; display: block; margin: 4px 0; }
                
                /* Tinh chỉnh công thức toán */
                .katex { font-size: 1.1em; }
            </style>
            
            <script>
                document.addEventListener("DOMContentLoaded", function() {
                    renderMathInElement(document.body, {
                        delimiters: [
                            // Cấu hình nhận diện dấu $ (Quan trọng)
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
            // WebView trong LazyColumn cần chiều cao cố định hoặc wrap_content cẩn thận
            // Tuy nhiên, cách tốt nhất là set chiều cao tối thiểu để tránh bị collapsed về 0
            .defaultMinSize(minHeight = 40.dp),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = false // Quan trọng để text tự xuống dòng
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING

                // Tắt background và thanh cuộn
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                // Không cho người dùng click vào link trong WebView (tránh nhảy ra trình duyệt)
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                    }
                }
            }
        },
        update = { webView ->
            // Base64 encode để giữ nguyên các ký tự đặc biệt
            val encodedHtml = Base64.encodeToString(htmlContent.toByteArray(), Base64.NO_PADDING)
            webView.loadData(encodedHtml, "text/html", "base64")
        }
    )
}