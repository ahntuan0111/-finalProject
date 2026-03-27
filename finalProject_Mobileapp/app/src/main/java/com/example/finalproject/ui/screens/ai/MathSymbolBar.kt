package com.example.finalproject.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===================================================================
// Cấu trúc dữ liệu mô tả một ký hiệu toán học
// ===================================================================
data class MathSymbol(
    val display: String,      // Nhãn hiển thị trên nút bấm
    val insertText: String,   // Chuỗi Unicode được chèn vào TextField (học sinh đọc được)
    val latex: String,        // LaTeX tương đương (dùng cho Preview panel - Solution 2)
    val tooltip: String       // Mô tả đầy đủ
)

// ===================================================================
// Danh sách ký hiệu theo nhóm (Tab)
// ===================================================================
private val SYMBOL_GROUPS = listOf(
    "Cơ bản" to listOf(
        MathSymbol("x²",   "x²",      "x^{2}",         "Số mũ 2"),
        MathSymbol("xⁿ",   "x^n",     "x^{n}",         "Số mũ n"),
        MathSymbol("√",    "√()",     "\\sqrt{}",      "Căn bậc hai"),
        MathSymbol("ⁿ√",   "ⁿ√()",    "\\sqrt[n]{}",   "Căn bậc n"),
        MathSymbol("a/b",  "(a)/(b)", "\\frac{a}{b}",  "Phân số"),
        MathSymbol("|x|",  "|x|",     "|x|",           "Giá trị tuyệt đối"),
        MathSymbol("( )",  "()",      "()",            "Ngoặc tròn"),
    ),
    "Đại số" to listOf(
        MathSymbol("≤",    "≤",   "\\leq",    "Nhỏ hơn hoặc bằng"),
        MathSymbol("≥",    "≥",   "\\geq",    "Lớn hơn hoặc bằng"),
        MathSymbol("≠",    "≠",   "\\neq",    "Khác"),
        MathSymbol("≈",    "≈",   "\\approx", "Xấp xỉ"),
        MathSymbol("±",    "±",   "\\pm",     "Cộng trừ"),
        MathSymbol("∞",    "∞",   "\\infty",  "Vô cùng"),
        MathSymbol("Δ",    "Δ",   "\\Delta",  "Delta"),
        MathSymbol("∑",    "∑",   "\\sum",    "Tổng Sigma"),
        MathSymbol("∏",    "∏",   "\\prod",   "Tích Pi"),
    ),
    "Hình học" to listOf(
        MathSymbol("π",    "π",      "\\pi",        "Pi"),
        MathSymbol("α",    "α",      "\\alpha",     "Alpha (góc)"),
        MathSymbol("β",    "β",      "\\beta",      "Beta (góc)"),
        MathSymbol("θ",    "θ",      "\\theta",     "Theta (góc)"),
        MathSymbol("°",    "°",      "^{\\circ}",   "Độ"),
        MathSymbol("sin",  "sin()",  "\\sin({})",   "Sin"),
        MathSymbol("cos",  "cos()",  "\\cos({})",   "Cos"),
        MathSymbol("tan",  "tan()",  "\\tan({})",   "Tan"),
        MathSymbol("⊥",    "⊥",      "\\perp",      "Vuông góc"),
        MathSymbol("∥",    "∥",      "\\parallel",  "Song song"),
        MathSymbol("∠",    "∠",      "\\angle",     "Góc"),
        MathSymbol("△",    "△",      "\\triangle",  "Tam giác"),
    ),
    "Tập hợp" to listOf(
        MathSymbol("∈",   "∈",   "\\in",        "Thuộc"),
        MathSymbol("∉",   "∉",   "\\notin",     "Không thuộc"),
        MathSymbol("⊂",   "⊂",   "\\subset",    "Tập con"),
        MathSymbol("⊆",   "⊆",   "\\subseteq",  "Tập con hoặc bằng"),
        MathSymbol("∪",   "∪",   "\\cup",       "Hợp"),
        MathSymbol("∩",   "∩",   "\\cap",       "Giao"),
        MathSymbol("∅",   "∅",   "\\emptyset",  "Tập rỗng"),
        MathSymbol("ℕ",   "ℕ",   "\\mathbb{N}", "Số tự nhiên"),
        MathSymbol("ℤ",   "ℤ",   "\\mathbb{Z}", "Số nguyên"),
        MathSymbol("ℚ",   "ℚ",   "\\mathbb{Q}", "Số hữu tỉ"),
        MathSymbol("ℝ",   "ℝ",   "\\mathbb{R}", "Số thực"),
    ),
    "Xác suất" to listOf(
        MathSymbol("P(A)",    "P(A)",    "P(A)",          "Xác suất biến cố A"),
        MathSymbol("P(A|B)",  "P(A|B)",  "P(A\\mid B)",   "Xác suất có điều kiện"),
        MathSymbol("x̄",       "x̄",       "\\bar{x}",      "Trung bình"),
        MathSymbol("σ",       "σ",       "\\sigma",       "Độ lệch chuẩn"),
        MathSymbol("σ²",      "σ²",      "\\sigma^{2}",   "Phương sai"),
        MathSymbol("μ",       "μ",       "\\mu",          "Kỳ vọng"),
        MathSymbol("C(n,k)",  "C(n,k)",  "C_{n}^{k}",    "Tổ hợp"),
        MathSymbol("A(n,k)",  "A(n,k)",  "A_{n}^{k}",    "Chỉnh hợp"),
        MathSymbol("n!",      "n!",      "n!",            "Giai thừa"),
        MathSymbol("∀",       "∀",       "\\forall",      "Với mọi"),
        MathSymbol("∃",       "∃",       "\\exists",      "Tồn tại"),
    ),
)

// ===================================================================
// Composable chính: Math Symbol Bar
// ===================================================================
@Composable
fun MathSymbolBar(
    onSymbolClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxWidth()) {
        // --- Hàng Tab chọn nhóm ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SYMBOL_GROUPS.forEachIndexed { index, (groupName, _) ->
                val isSelected = index == selectedTab
                TextButton(
                    onClick = { selectedTab = index },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = groupName,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        // --- Hàng ký hiệu của tab đang chọn ---
        val symbols = SYMBOL_GROUPS[selectedTab].second
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            symbols.forEach { symbol ->
                // Solution 1: chèn Unicode (insertText) thay vì LaTeX
                SymbolChip(symbol = symbol, onClick = { onSymbolClick(symbol.insertText) })
            }
        }
    }
}

@Composable
private fun SymbolChip(
    symbol: MathSymbol,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 1.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = symbol.display,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
