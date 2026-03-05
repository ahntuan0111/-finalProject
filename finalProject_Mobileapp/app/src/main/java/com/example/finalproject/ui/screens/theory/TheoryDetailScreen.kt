package com.example.finalproject.ui.screens.theory

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebView
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.TheoryDetailViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

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

                        // 2. VIDEO (SỬ DỤNG NATIVE YOUTUBE PLAYER LIBRARY)
                        if (!data.videoUrl.isNullOrEmpty()) {
                            item {
                                Text(
                                    text = "Video bài giảng",
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
                                        Text("Link video không hợp lệ", color = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // 3. NỘI DUNG (Text/LaTeX/Image)
                        // LƯU Ý HIỆU NĂNG: Nếu danh sách data.contents có nhiều item "TEXT",
                        // việc đặt LatexWebView (bản chất là 1 WebView) bên trong vòng lặp items()
                        // sẽ tạo ra vô số WebView, gây giật lag cực mạnh khi cuộn (scroll).
                        // CÁCH TỐI ƯU TỐT NHẤT: Bạn nên gộp tất cả content loại TEXT thành 1 chuỗi HTML duy nhất
                        // và chỉ render 1 LatexWebView cho toàn bộ bài học.
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
                                    Text("Đã hoàn thành", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Đánh dấu hoàn thành", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
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

// --- COMPONENT CHƠI VIDEO NATIVE (CỰC KỲ TỐI ƯU) ---
@Composable
fun NativeYouTubePlayer(videoId: String) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Lưu trữ tham chiếu (reference) của player để update khi cần
    var youtubePlayerRef by remember { mutableStateOf<YouTubePlayer?>(null) }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f) // QUAN TRỌNG: Ép tỷ lệ 16:9 chuẩn của YouTube để View không bị co về 0
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black), // Thêm nền đen trong lúc chờ load
        factory = { context ->
            YouTubePlayerView(context).apply {
                // Rất quan trọng để quản lý vòng đời (pause khi ẩn app)
                lifecycleOwner.lifecycle.addObserver(this)

                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youtubePlayerRef = youTubePlayer
                        youTubePlayer.cueVideo(videoId, 0f) // cueVideo: Nạp video nhưng không tự động phát
                    }
                })
            }
        },
        update = { _ ->
            // Mỗi khi videoId từ ViewModel thay đổi, Compose sẽ gọi khối update này
            // Giúp nạp đúng video ngay cả khi API phản hồi chậm
            youtubePlayerRef?.cueVideo(videoId, 0f)
        }
    )
}

/**
 * Hàm tách ID Youtube
 */
fun extractYoutubeVideoId(url: String): String {
    return try {
        val uri = Uri.parse(url)
        val host = uri.host ?: return ""

        when {
            host.contains("youtu.be") -> uri.lastPathSegment ?: ""
            host.contains("youtube.com") -> {
                val path = uri.path ?: ""
                when {
                    path.startsWith("/shorts/") -> uri.lastPathSegment ?: ""
                    path.startsWith("/embed/") -> uri.lastPathSegment ?: ""
                    else -> uri.getQueryParameter("v") ?: ""
                }
            }
            else -> ""
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