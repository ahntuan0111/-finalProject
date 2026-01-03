export const MATH_SYMBOLS = [
  { label: 'Căn bậc 2', latex: '\\sqrt{x}', icon: '√x' },
  { label: 'Phân số', latex: '\\frac{a}{b}', icon: 'a/b' },
  { label: 'Mũ', latex: 'x^{2}', icon: 'x²' },
  { label: 'Chỉ số dưới', latex: 'x_{2}', icon: 'x₂' },
  { label: 'Dấu Pi', latex: '\\pi', icon: 'π' },
  { label: 'Độ', latex: '^\\circ', icon: '°' },
  { label: 'Góc', latex: '\\angle', icon: '∠' },
  { label: 'Tam giác', latex: '\\triangle', icon: '△' },
  { label: 'Tổng', latex: '\\sum', icon: '∑' },
  { label: 'Tích phân', latex: '\\int', icon: '∫' },
  { label: 'Vô cực', latex: '\\infty', icon: '∞' },
  { label: 'Tương đương', latex: '\\Leftrightarrow', icon: '⇔' },
  { label: 'Suy ra', latex: '\\Rightarrow', icon: '⇒' },
  { label: 'Thuộc', latex: '\\in', icon: '∈' },
  { label: 'Khác', latex: '\\neq', icon: '≠' },
  { label: 'Lớn hơn bằng', latex: '\\geq', icon: '≥' },
  { label: 'Nhỏ hơn bằng', latex: '\\leq', icon: '≤' },
];

// Hàm bọc công thức vào trong dấu $$ để hiển thị Block Math
export const wrapMathBlock = (text) => `$$ ${text} $$`;