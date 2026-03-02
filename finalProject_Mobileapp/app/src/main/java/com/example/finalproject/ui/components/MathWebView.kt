package com.example.finalproject.ui.components

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MathWebView(htmlContent: String, modifier: Modifier = Modifier) {
    // Template HTML nhúng MathJax để render LaTeX
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
                        displayMath: [['$$', '$$'], ['\\[', '\\]']],
                        processEscapes: true
                    },
                    svg: { fontCache: 'global' }
                };
            </script>
            <script type="text/javascript" id="MathJax-script" async
                src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-svg.js">
            </script>
            <style>
                body {
                    font-family: 'sans-serif';
                    font-size: 16px;
                    color: #333333;
                    background-color: transparent; /* Giúp trong suốt nền */
                    margin: 0;
                    padding: 0;
                    display: flex;
                    align-items: center; /* Căn giữa theo chiều dọc nếu cần */
                }
                img { max-width: 100%; height: auto; }
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
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                setBackgroundColor(0x00000000) // Nền trong suốt
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                loadDataWithBaseURL(null, htmlTemplate, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, htmlTemplate, "text/html", "UTF-8", null)
        }
    )
}