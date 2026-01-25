package com.example.finalproject.ui.screens.theory

import android.annotation.SuppressLint
import android.net.Uri // Import quan trọng để xử lý Link
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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

    LaunchedEffect(lessonId) {
        viewModel.fetchLessonDetail(lessonId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Chi tiết bài học",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
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
                                text = data.title,
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
                                    text = "Video bài giảng",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                VideoWebView(videoUrl = data.videoUrl)

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // 3. NỘI DUNG (Text/LaTeX/Image)
                        items(data.contents) { content ->
                            when (content.contentType) {
                                "TEXT" -> {
                                    LatexWebView(content = content.contentValue)
                                }
                                "IMAGE" -> {
                                    // Placeholder cho ảnh
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Hình ảnh minh họa", color = Color.DarkGray)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

//                        item {
//                            Spacer(modifier = Modifier.height(24.dp))
//
//                            Button(
//                                onClick = { viewModel.markAsCompleted(data.id) },
//                                enabled = !data.isCompleted, // Nếu xong rồi thì disable nút
//                                colors = ButtonDefaults.buttonColors(
//                                    containerColor = if (data.isCompleted) Color.Gray else PrimaryBlue
//                                ),
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(50.dp),
//                                shape = RoundedCornerShape(12.dp)
//                            ) {
//                                if (data.isCompleted) {
//                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
//                                    Spacer(modifier = Modifier.width(8.dp))
//                                    Text("Đã hoàn thành", fontSize = 16.sp)
//                                } else {
//                                    Text("Đánh dấu hoàn thành", fontSize = 16.sp, fontWeight = FontWeight.Bold)
//                                }
//                            }
//
//                            // Khoảng trống dưới cùng để không bị sát mép
//                            Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

// --- COMPOSABLE VIDEO (SỬ DỤNG URI PARSE & ORIGIN FIX) ---

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoWebView(videoUrl: String) {
    // 1. Dùng Uri parse để lấy ID chuẩn xác từ mọi loại link (kể cả link có list, index...)
    val videoId = extractYoutubeVideoId(videoUrl)

    // 2. Thêm tham số origin để tránh lỗi "Unavailable"
    val embedUrl = "https://www.youtube.com/embed/$videoId?playsinline=1&rel=0&origin=https://www.youtube.com"

    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body, html { margin: 0; padding: 0; background-color: black; height: 100%; overflow: hidden; }
                iframe { width: 100%; height: 100%; border: none; }
            </style>
        </head>
        <body>
            <iframe 
                src="$embedUrl" 
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                allowfullscreen>
            </iframe>
        </body>
        </html>
    """.trimIndent()

    if (videoId.isNotEmpty()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true

                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                }
            },
            update = { webView ->
                // BaseURL có dấu / ở cuối để đảm bảo khớp origin
                webView.loadDataWithBaseURL("https://www.youtube.com/", html, "text/html", "UTF-8", null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color.Black)
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("Link video không hợp lệ", color = Color.White)
        }
    }
}

/**
 * Hàm tách ID Youtube bằng Android Uri (Chính xác nhất)
 * Xử lý được:
 * - youtube.com/watch?v=ID&list=...
 * - youtu.be/ID
 * - youtube.com/shorts/ID
 * - youtube.com/embed/ID
 */
fun extractYoutubeVideoId(url: String): String {
    return try {
        val uri = Uri.parse(url)
        val host = uri.host ?: return ""

        if (host.contains("youtu.be")) {
            uri.lastPathSegment ?: ""
        } else if (host.contains("youtube.com")) {
            val path = uri.path ?: ""
            when {
                path.startsWith("/shorts/") -> uri.lastPathSegment ?: ""
                path.startsWith("/embed/") -> uri.lastPathSegment ?: ""
                else -> uri.getQueryParameter("v") ?: ""
            }
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}

// --- COMPOSABLE LATEX (GIỮ NGUYÊN) ---

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LatexWebView(content: String) {
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            
            <script>
            MathJax = {
              tex: {
                inlineMath: [['$', '$'], ['\\(', '\\)']],
                displayMath: [['$$', '$$'], ['\\[', '\\]']],
                processEscapes: true
              },
              svg: {
                fontCache: 'global'
              }
            };
            </script>

            <script id="MathJax-script" async src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js"></script>
            
            <style>
                html, body {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                }

                body { 
                    font-family: 'Roboto', sans-serif; 
                    font-size: 16px; 
                    color: #333; 
                    line-height: 1.6;
                    
                    overflow-wrap: break-word;
                    word-wrap: break-word;
                    word-break: break-word;
                }
                
                img { 
                    max-width: 100%; 
                    height: auto; 
                    border-radius: 8px; 
                    display: block;
                    margin: 10px auto;
                }
                
                p { margin: 0 0 10px 0; }
                
                mjx-container {
                    display: inline-block !important; 
                    vertical-align: middle;
                    overflow-x: auto;
                    overflow-y: hidden;
                    max-width: 100%;
                }
            </style>
        </head>
        <body>
            $content
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = false
                settings.useWideViewPort = false
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
}