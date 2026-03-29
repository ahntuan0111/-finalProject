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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.finalproject.R
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.TheoryDetailViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.regex.Pattern

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

                        // 2. VIDEO YOUTUBE (SỬ DỤNG THƯ VIỆN BÊN THỨ 3)
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

// --- COMPONENT CHƠI VIDEO YOUTUBE (DÙNG THƯ VIỆN PIERFRANCESCOSOFFRITTI) ---
@Composable
fun NativeYouTubePlayer(videoId: String) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasError by remember { mutableStateOf(false) }

    Column {
        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black)
        ) {
            if (hasError) {
                // Nếu video thật sự bị chặn bản quyền (không cho nhúng), hiển thị thông báo
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Video bị giới hạn phát trên ứng dụng ngoài.", color = Color.White, fontSize = 12.sp)
                }
            } else {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        YouTubePlayerView(ctx).apply {
                            // Liên kết vòng đời để giải phóng bộ nhớ khi tắt màn hình
                            lifecycleOwner.lifecycle.addObserver(this)
                            enableAutomaticInitialization = false

                            // Cấu hình thư viện bản 13.0.0 (bắt buộc truyền ctx vào Builder)
                            val options = IFramePlayerOptions.Builder(ctx)
                                .controls(1)
                                .rel(0)
                                .build()

                            initialize(object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    // cueVideo giúp tải video sẵn nhưng không tự động phát (tránh chói tai)
                                    youTubePlayer.cueVideo(videoId, 0f)
                                }

                                override fun onError(
                                    youTubePlayer: YouTubePlayer,
                                    error: PlayerConstants.PlayerError
                                ) {
                                    // Bắt lỗi nếu video bị khóa vùng/bản quyền
                                    hasError = true
                                }
                            }, options)
                        }
                    }
                )
            }
        }

        // NÚT FALLBACK (DÀNH CHO NHỮNG VIDEO BỊ CHỦ KÊNH ĐÁNH BẢN QUYỀN/CẤM NHÚNG)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F).copy(alpha = 0.5f))
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Xem trên ứng dụng YouTube (Nếu video lỗi)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Hàm tách ID Youtube (Bắt chuẩn xác 11 ký tự, loại bỏ mọi rác &list=...)
 */
fun extractYoutubeVideoId(url: String): String {
    if (url.isBlank()) return ""
    return try {
        val pattern = "(?<=v=|v\\/|vi=|vi\\/|youtu.be\\/|embed\\/)[a-zA-Z0-9_-]{11}"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(url)
        if (matcher.find()) {
            return matcher.group() ?: ""
        }

        val uri = Uri.parse(url)
        val vParam = uri.getQueryParameter("v")
        if (vParam != null && vParam.length >= 11) {
            return vParam.substring(0, 11)
        }

        val host = uri.host ?: ""
        if (host.contains("youtu.be")) {
            val lastPath = uri.lastPathSegment ?: ""
            if (lastPath.length >= 11) return lastPath.substring(0, 11)
        }

        ""
    } catch (e: Exception) {
        ""
    }
}

// --- COMPONENT HIỂN THỊ ẢNH ---
@Composable
fun LessonImage(imageUrl: String) {
    val fullUrl = when {
        imageUrl.isBlank() -> ""
        imageUrl.startsWith("http://") || imageUrl.startsWith("https://") -> imageUrl
        imageUrl.startsWith("firebasestorage.googleapis.com") -> "https://$imageUrl"
        imageUrl.startsWith("/") -> "http://10.0.2.2:8080$imageUrl"
        else -> imageUrl
    }

    if (fullUrl.isNotEmpty()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fullUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Lesson Image",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.FillWidth,

            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            },

            error = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Không thể tải ảnh", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Hình ảnh không khả dụng", color = Color.Gray)
        }
    }
}

// --- COMPOSABLE LATEX ---
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