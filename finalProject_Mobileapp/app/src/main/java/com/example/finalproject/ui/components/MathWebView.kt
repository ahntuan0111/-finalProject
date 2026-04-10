package com.example.finalproject.ui.components

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.viewinterop.AndroidView

/**
 * MathWebView — render công thức LaTeX/MathJax với màu chữ theo theme của app.
 *
 * QUAN TRỌNG: Dùng MaterialTheme.colorScheme thay vì isSystemInDarkTheme()
 * để màu text khớp đúng với theme user đã chọn trong app (không phải theme hệ thống).
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MathWebView(
    htmlContent: String,
    modifier: Modifier = Modifier,
    // Cho phép override từ parent nếu cần, mặc định đọc từ MaterialTheme
    isDark: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f
) {
    val textColorHex = if (isDark) "#F1F5F9" else "#1E293B"
    val bgColorHex   = if (isDark) "#0F172A"  else "#FFFFFF"

    val htmlTemplate = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <script>
                MathJax = {
                    tex: {
                        inlineMath: [['$', '$'], ['\\(', '\\)']],
                        displayMath: [['$$', '${'$'}$'], ['\\[', '\\]']],
                        processEscapes: true
                    },
                    svg: { fontCache: 'global' }
                };
            </script>
            <script type="text/javascript" id="MathJax-script" async
                src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-svg.js">
            </script>
            <style>
                * { box-sizing: border-box; }
                html, body {
                    margin: 0; padding: 4px 8px;
                    background-color: $bgColorHex;
                }
                body {
                    font-family: 'Roboto', 'Noto Sans', sans-serif;
                    font-size: 16px;
                    color: $textColorHex;
                    line-height: 1.7;
                    display: flex;
                    align-items: center;
                    word-break: break-word;
                }
                img { max-width: 100%; height: auto; }
                .MathJax { color: $textColorHex !important; }
            </style>
        </head>
        <body>
            $htmlContent
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                // Đặt màu nền khớp với theme thay vì transparent
                setBackgroundColor(android.graphics.Color.parseColor(bgColorHex))
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                loadDataWithBaseURL("https://localhost", htmlTemplate, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://localhost", htmlTemplate, "text/html", "UTF-8", null)
        }
    )
}