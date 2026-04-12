package com.example.finalproject.ui.screens.theory

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.JavascriptInterface
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
import androidx.compose.ui.graphics.luminance
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
import coil.request.CachePolicy
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

    // Đọc isDark từ MaterialTheme — phản ánh đúng lựa chọn của user trong app
    val isDarkTheme = !MaterialTheme.colorScheme.background.luminance().let { it > 0.5f }

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
                        // Padding bottom lớn hơn một chút để tạo khoảng không thở ở cuối trang
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 40.dp)
                    ) {
                        // 1. Tiêu đề
                        item {
                            Text(
                                text = data.title ?: stringResource(id = R.string.theory_detail_no_title),
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
                                    LatexMathView(
                                        latex = content.contentValue ?: "",
                                        isDark = isDarkTheme
                                    )
                                }
                                "IMAGE" -> {
                                    LessonImage(imageUrl = content.contentValue ?: "")
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // NÚT ĐÁNH DẤU HOÀN THÀNH
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
                                    .height(54.dp), // Tăng nhẹ chiều cao nút cho dễ bấm
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
                    Text(stringResource(id = R.string.theory_detail_video_restricted), color = Color.White, fontSize = 12.sp)
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
            Text(stringResource(id = R.string.theory_detail_watch_on_youtube), fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
private fun normalizeImageUrl(imageUrl: String): String {
    if (imageUrl.isBlank()) return ""
    return when {
        // URL đã đầy đủ
        imageUrl.startsWith("https://") -> imageUrl
        imageUrl.startsWith("http://") -> imageUrl
        // Firebase Storage thiếu scheme
        imageUrl.startsWith("firebasestorage.googleapis.com") -> "https://$imageUrl"
        imageUrl.startsWith("storage.googleapis.com") -> "https://$imageUrl"
        // Relative path → backend local
        imageUrl.startsWith("/") -> "http://192.168.0.159:8080$imageUrl"
        else -> imageUrl
    }
}

@Composable
fun LessonImage(imageUrl: String) {
    val fullUrl = remember(imageUrl) { normalizeImageUrl(imageUrl) }
    val context = LocalContext.current

    if (fullUrl.isNotEmpty()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(fullUrl)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .addHeader("Cache-Control", "no-cache")
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
                    CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 2.dp)
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
                        Text(
                            stringResource(id = R.string.theory_detail_image_error),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
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
            Text(stringResource(id = R.string.theory_detail_image_unavailable), color = Color.Gray)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// JAVASCRIPT INTERFACE: Báo chiều cao nội dung sau khi KaTeX render xong
// ─────────────────────────────────────────────────────────────────────────────
class HeightReporter(private val onHeight: (Int) -> Unit) {
    @JavascriptInterface
    fun reportHeight(px: Int) {
        onHeight(px)
    }
}

// --- COMPOSABLE LATEX ---
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LatexMathView(
    latex: String,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    val textColorHex = if (isDark) "#F1F5F9" else "#1E293B"
    val bgColorHex   = if (isDark) "#0F172A" else "#FFFFFF"

    var webHeightDp by remember { mutableStateOf(80.dp) }
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
                html {
                    margin: 0; padding: 0;
                    background-color: $bgColorHex;
                }
                body {
                    margin: 0; padding: 8px 12px;
                    font-size: 16px;
                    color: $textColorHex;
                    background-color: $bgColorHex;
                    font-family: 'Roboto', 'Noto Sans', sans-serif;
                    line-height: 1.75;
                    word-break: break-word;
                }
                h1, h2, h3, h4, h5, p, span, li { color: $textColorHex !important; }
                img { max-width: 100%; height: auto; border-radius: 8px; }
                strong, b { color: $textColorHex; font-weight: 700; }
                .katex { font-size: 1.05em; }
                .katex-display {
                    overflow-x: auto;
                    margin: 10px 0;
                    padding: 4px 0;
                }
                pre, code {
                    background: ${if (isDark) "#1E293B" else "#F1F5F9"};
                    color: ${if (isDark) "#93C5FD" else "#1D4ED8"};
                    border-radius: 6px;
                    padding: 2px 6px;
                    font-size: 14px;
                }
            </style>
            <script>
                document.addEventListener("DOMContentLoaded", function() {
                    if (typeof renderMathInElement !== 'undefined') {
                        renderMathInElement(document.body, {
                            delimiters: [
                                {left: "$dollarSign$dollarSign", right: "$dollarSign$dollarSign", display: true},
                                {left: "$dollarSign",            right: "$dollarSign",            display: false},
                                {left: "\\(", right: "\\)", display: false},
                                {left: "\\[", right: "\\]", display: true}
                            ],
                            throwOnError: false
                        });
                    }
                    setTimeout(function() {
                        var h = document.body.scrollHeight;
                        if (window.HeightBridge) window.HeightBridge.reportHeight(h);
                    }, 150);
                });
            </script>
        </head>
        <body>$latex</body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(webHeightDp),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = false
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                settings.allowFileAccess = true

                setBackgroundColor(android.graphics.Color.parseColor(bgColorHex))
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                // ĐÃ SỬA: Lấy px và chuyển trực tiếp thành dp (không thông qua density).
                addJavascriptInterface(
                    HeightReporter { px ->
                        post { webHeightDp = px.dp.coerceAtLeast(40.dp) }
                    },
                    "HeightBridge"
                )

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        view?.evaluateJavascript("document.body.scrollHeight") { result ->
                            // Bỏ đi các dấu ngoặc kép bọc quanh chuỗi trả về từ JS
                            val px = result?.replace("\"", "")?.trim()?.toIntOrNull() ?: return@evaluateJavascript
                            post { webHeightDp = px.dp.coerceAtLeast(40.dp) }
                        }
                    }
                }
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                "file:///android_asset/",
                htmlContent,
                "text/html",
                "UTF-8",
                null
            )
        }
    )
}