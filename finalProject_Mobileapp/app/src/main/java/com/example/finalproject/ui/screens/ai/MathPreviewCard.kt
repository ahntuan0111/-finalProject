package com.example.finalproject.ui.screens.ai

import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin

// Regex phát hiện mã LaTeX trong chuỗi nhập
// Ví dụ: \frac, \sqrt, \pi, $$...$$ hoặc $...$
private val LATEX_PATTERN = Regex("""\\[a-zA-Z]+|\$\$?.+?\$?\$""")

/**
 * Solution 2: Real-time LaTeX Preview Panel
 *
 * Hiển thị một Card nhỏ phía trên ô nhập liệu, render nội dung đang gõ thành công thức
 * Toán học đẹp mắt bất cứ khi nào phát hiện ký tự LaTeX (VD: \frac, \sqrt...).
 *
 * Học sinh sẽ thấy ngay kết quả trực quan thay vì nhìn vào mã lạ.
 */
@Composable
fun MathPreviewCard(
    inputText: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Chỉ render preview khi có nội dung LaTeX thực sự
    val hasLatex = remember(inputText) { LATEX_PATTERN.containsMatchIn(inputText) }
    val isVisible = hasLatex && inputText.isNotBlank()

    // Khởi tạo Markwon một lần duy nhất
    val markwon = remember {
        Markwon.builder(context)
            .usePlugin(MarkwonInlineParserPlugin.create())
            .usePlugin(JLatexMathPlugin.create(40f) { builder ->
                builder.inlinesEnabled(true)
            })
            .build()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(expandFrom = Alignment.Bottom),
        exit = shrinkVertically(shrinkTowards = Alignment.Bottom),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Nhãn nhỏ phía trên
                Text(
                    text = "👁️ Xem trước công thức",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Preview render Markwon
                AndroidView(
                    factory = { ctx ->
                        TextView(ctx).apply {
                            textSize = 15f
                            setTextColor(
                                android.graphics.Color.parseColor("#1C1B1F")
                            )
                        }
                    },
                    update = { textView ->
                        // Đưa nội dung vào dạng block LaTeX để đảm bảo render thành công
                        val previewText = inputText
                            .replace("\\$", "$")
                            .replace("\\(", "$$").replace("\\)", "$$")
                            .replace("\\[", "$$").replace("\\]", "$$")
                            .replace(Regex("(?<!\\$)\\$(?!\\$)")) { "$$" }
                        markwon.setMarkdown(textView, previewText)
                    }
                )
            }
        }
    }
}
