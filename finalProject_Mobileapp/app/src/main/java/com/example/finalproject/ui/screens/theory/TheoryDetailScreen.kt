package com.example.finalproject.ui.screens.theory

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.TheoryDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheoryDetailScreen(
    navController: NavController,
    lessonId: Long,
    viewModel: TheoryDetailViewModel = hiltViewModel()
) {
    val lesson by viewModel.lessonDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isCompletedState by viewModel.isCompleted.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.fetchLessonDetail(lessonId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.theory_detail_title),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
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
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryBlue
                )
            } else {
                lesson?.let { data ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        // 1. Tiêu đề
                        item {
                            Text(
                                text = data.title ?: "Chưa cập nhật tiêu đề",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // 2. VIDEO
                        if (!data.videoUrl.isNullOrEmpty()) {
                            item {
                                Text(
                                    text = stringResource(id = R.string.theory_detail_video_title),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                val videoId = extractYoutubeVideoId(data.videoUrl)
                                if (videoId.isNotEmpty()) {
                                    NativeYouTubePlayer(videoId = videoId)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .background(Color.Black),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(stringResource(id = R.string.theory_detail_invalid_video), color = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // 3. NỘI DUNG (Text/LaTeX/Image)
                        items(data.contents) { content ->
                            when (content.contentType) {
                                "TEXT" -> {
                                    LatexMathView(latex = content.contentValue ?: "")
                                }
                                "IMAGE" -> {
                                    LessonImage(imageUrl = content.contentValue ?: "")
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))

                            val finalIsCompleted = isCompletedState

                            Button(
                                onClick = { viewModel.markAsCompleted(data.id) },
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
                                    Text(stringResource(id = R.string.theory_detail_btn_completed), fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                } else {
                                    Text(stringResource(id = R.string.theory_detail_btn_mark_complete), fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
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

// --- COMPONENT CHƠI VIDEO QUA WEBVIEW IFRAME (Hoạt động cả trên Emulator & máy thật) ---
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NativeYouTubePlayer(videoId: String) {
    val context = LocalContext.current
    val iframeHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { margin:0; padding:0; background:#000; }
                .video-container { position:relative; padding-bottom:56.25%; height:0; overflow:hidden; }
                .video-container iframe { position:absolute; top:0; left:0; width:100%; height:100%; border:0; }
            </style>
        </head>
        <body>
            <div class="video-container">
                <iframe src="https://www.youtube.com/embed/$videoId?playsinline=1&rel=0"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    allowfullscreen>
                </iframe>
            </div>
        </body>
        </html>
    """.trimIndent()

    Box(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .clip(RoundedCornerShape(8.dp))
        .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    setBackgroundColor(android.graphics.Color.BLACK)
                    webViewClient = WebViewClient()
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://www.youtube.com", iframeHtml, "text/html", "UTF-8", null)
            }
        )
    }
}


/**
 * Hàm tách ID Youtube
 */
fun extractYoutubeVideoId(url: String): String {
    if (url.isBlank()) return ""
    return try {
        val uri = Uri.parse(url)
        val host = uri.host ?: ""

        when {
            host.contains("youtu.be") -> uri.lastPathSegment ?: ""
            host.contains("youtube.com") -> {
                val vParam = uri.getQueryParameter("v")
                if (vParam != null) return vParam
                val pathSegments = uri.pathSegments
                if (pathSegments.contains("shorts") || pathSegments.contains("embed") || pathSegments.contains("live")) {
                    uri.lastPathSegment ?: ""
                } else ""
            }
            else -> ""
        }
    } catch (e: Exception) {
        ""
    }
}

// --- COMPONENT HIỂN THỊ ẢNH ---
@Composable
fun LessonImage(imageUrl: String) {
    // Ảnh được lưu trên Firebase Storage với URL đầy đủ (https://...)
    // Nếu là path tương đối, thêm base URL (chỉ dùng cho ảnh lưu trên server backend nội bộ)
    val fullUrl = when {
        imageUrl.startsWith("https://") || imageUrl.startsWith("http://") -> imageUrl
        imageUrl.isNotBlank() -> "http://10.0.2.2:8080$imageUrl"
        else -> ""
    }

    if (fullUrl.isNotEmpty()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fullUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Lesson Image",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF1F5F9)),
            contentScale = ContentScale.FillWidth
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Không thể tải ảnh", color = Color.Gray)
        }
    }
}

// --- COMPOSABLE LATEX - SỬ DỤNG KATEX TỪ LOCAL ASSETS ---
// KaTeX files phải được tải về và để trong: app/src/main/assets/katex/
// - katex.min.js
// - katex.min.css
// - auto-render.min.js
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LatexMathView(latex: String, modifier: Modifier = Modifier) {
    val textColorHex = if (androidx.compose.foundation.isSystemInDarkTheme()) "#FAFAFA" else "#1E293B"
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
                body {
                    font-size: 16px;
                    color: $textColorHex;
                    font-family: sans-serif;
                    margin: 8px; padding: 0;
                    background-color: transparent;
                    line-height: 1.7;
                }
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
            .defaultMinSize(minHeight = 10.dp),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = false
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                settings.allowFileAccess = true
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "UTF-8", null)
        }
    )
}