import React, { useEffect, useRef, useState } from 'react';
import { Button, Tooltip, Tabs, Modal, Input } from 'antd';
// ĐÃ SỬA LỖI: Thay PlusMinusOutlined bằng CalculatorOutlined
import { FunctionOutlined, EnterOutlined, CalculatorOutlined, BorderOutlined, BuildOutlined, AppstoreOutlined } from '@ant-design/icons';
import 'mathlive';

// --- DANH SÁCH CÁC KÝ HIỆU TOÁN HỌC (CHUẨN THCS) ---
const MATH_GROUPS = [
  {
    key: 'algebra',
    // ĐÃ SỬA: Dùng CalculatorOutlined
    label: <span><CalculatorOutlined /> Phép toán & Đại số</span>,
    items: [
      { label: 'Nhân', latex: '\\times', icon: '×' },
      { label: 'Chia', latex: '\\div', icon: '÷' },
      { label: 'Cộng trừ', latex: '\\pm', icon: '±' },
      { label: 'Xấp xỉ', latex: '\\approx', icon: '≈' },
      { label: 'Khác', latex: '\\neq', icon: '≠' },
      { label: 'Lớn hơn/bằng', latex: '\\ge', icon: '≥' },
      { label: 'Nhỏ hơn/bằng', latex: '\\le', icon: '≤' },
      { label: 'Phần trăm', latex: '\\%', icon: '%' },
      { label: 'Tỷ lệ', latex: ':', icon: ':' },
      { label: 'Căn bậc 2', latex: '\\sqrt{\\placeholder}', icon: '√x' },
      { label: 'Căn bậc 3', latex: '\\sqrt[3]{\\placeholder}', icon: '∛x' },
      { label: 'Trị tuyệt đối', latex: '|\\placeholder|', icon: '|x|' },
      { label: 'Bình phương', latex: '^2', icon: 'x²' },
      { label: 'Lập phương', latex: '^3', icon: 'x³' },
      { label: 'Lũy thừa', latex: '^{\\placeholder}', icon: 'xⁿ' },
      { label: 'Chỉ số dưới', latex: '_\\placeholder', icon: 'xₙ' },
    ]
  },
  {
    key: 'structures',
    label: <span><BuildOutlined /> Phân số & Biểu thức</span>,
    items: [
      { label: 'Phân số', latex: '\\frac{\\placeholder}{\\placeholder}', icon: '◻/◻' },
      { label: 'Hỗn số', latex: '\\placeholder\\frac{\\placeholder}{\\placeholder}', icon: '◻ ◻/◻' },
      { label: 'Ngoặc đơn', latex: '(\\placeholder)', icon: '(◻)' },
      { label: 'Ngoặc vuông', latex: '[\\placeholder]', icon: '[◻]' },
      { label: 'Ngoặc nhọn', latex: '\\{\\placeholder\\}', icon: '{◻}' },
      { label: 'Hệ phương trình (2 dòng)', latex: '\\begin{cases} \\placeholder \\\\ \\placeholder \\end{cases}', icon: '{...' },
    ]
  },
  {
    key: 'geometry',
    label: <span><BorderOutlined /> Hình học</span>,
    items: [
      { label: 'Góc', latex: '\\widehat{\\placeholder}', icon: 'Â' },
      { label: 'Ký hiệu Góc', latex: '\\angle', icon: '∠' },
      { label: 'Độ', latex: '^\\circ', icon: '°' },
      { label: 'Tam giác', latex: '\\triangle', icon: '△' },
      { label: 'Vuông góc', latex: '\\perp', icon: '⊥' },
      { label: 'Song song', latex: '\\parallel', icon: '∥' },
      { label: 'Đồng dạng', latex: '\\backsim', icon: '∽' },
      { label: 'Bằng nhau', latex: '=', icon: '=' },
      { label: 'Dấu Pi', latex: '\\pi', icon: 'π' },
      { label: 'Cung tròn', latex: '\\stackrel{\\frown}{\\placeholder}', icon: '⌒' },
      { label: 'Vector', latex: '\\vec{\\placeholder}', icon: '→' },
    ]
  },
  {
    key: 'logic',
    label: <span><AppstoreOutlined /> Tập hợp & Logic</span>,
    items: [
      { label: 'Tập N (Tự nhiên)', latex: '\\mathbb{N}', icon: 'ℕ' },
      { label: 'Tập Z (Nguyên)', latex: '\\mathbb{Z}', icon: 'ℤ' },
      { label: 'Tập Q (Hữu tỉ)', latex: '\\mathbb{Q}', icon: 'ℚ' },
      { label: 'Tập R (Thực)', latex: '\\mathbb{R}', icon: 'ℝ' },
      { label: 'Tập rỗng', latex: '\\emptyset', icon: '∅' },
      { label: 'Thuộc', latex: '\\in', icon: '∈' },
      { label: 'Không thuộc', latex: '\\notin', icon: '∉' },
      { label: 'Tập con', latex: '\\subset', icon: '⊂' },
      { label: 'Chứa tập con', latex: '\\supset', icon: '⊃' },
      { label: 'Giao', latex: '\\cap', icon: '∩' },
      { label: 'Hợp', latex: '\\cup', icon: '∪' },
      { label: 'Suy ra', latex: '\\Rightarrow', icon: '⇒' },
      { label: 'Tương đương', latex: '\\Leftrightarrow', icon: '⇔' },
    ]
  }
];

const MathEditor = ({ value = '', onChange, placeholder }) => {
  const textAreaRef = useRef(null);
  const mfRef = useRef(null);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [cursorPos, setCursorPos] = useState(null);

  // --- 1. XỬ LÝ NHẬP LIỆU VÀ CON TRỎ ---
  const handleTextChange = (e) => {
    if (onChange) onChange(e.target.value);
  };

  const saveCursorPosition = (e) => {
    setCursorPos(e.target.selectionStart);
  };

  // --- 2. HÀM CHÈN TEXT VÀO VỊ TRÍ CON TRỎ ---
  const insertTextAtCursor = (textToInsert) => {
    const currentText = value || '';
    const position = cursorPos !== null ? cursorPos : currentText.length;
    
    const newText = 
      currentText.substring(0, position) + 
      textToInsert + 
      currentText.substring(position);

    if (onChange) onChange(newText);
    
    const newPos = position + textToInsert.length;
    setCursorPos(newPos);

    setTimeout(() => {
        if(textAreaRef.current) {
            textAreaRef.current.focus();
            textAreaRef.current.resizableTextArea.textArea.setSelectionRange(newPos, newPos);
        }
    }, 50);
  };

  // --- 3. XỬ LÝ NÚT XUỐNG DÒNG ---
  const handleInsertBreak = () => {
    insertTextAtCursor('<br/>');
  };

  // --- 4. XỬ LÝ MODAL TOÁN HỌC ---
  const openMathModal = () => {
    if (cursorPos === null && value) {
        setCursorPos(value.length);
    }
    setIsModalOpen(true);
    
    setTimeout(() => {
      if (mfRef.current) {
        mfRef.current.value = ''; 
        mfRef.current.focus();
      }
    }, 100);
  };

  const handleInsertLatexToText = () => {
    const latexValue = mfRef.current ? mfRef.current.getValue() : '';

    if (!latexValue) {
      setIsModalOpen(false);
      return;
    }

    const formattedLatex = ` $${latexValue}$ `; 
    insertTextAtCursor(formattedLatex);
    setIsModalOpen(false);
  };

  // NÂNG CẤP: Chèn và tự động nhảy vào Placeholder
  const insertToMathField = (latex) => {
    if (mfRef.current) {
      mfRef.current.executeCommand(['insert', latex]);
      mfRef.current.focus();
      
      // Nếu latex có chứa placeholder, tự động nhảy con trỏ vào ô đầu tiên
      if (latex.includes('\\placeholder')) {
         mfRef.current.executeCommand(['moveToNextPlaceholder']);
      }
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
      {/* --- TOOLBAR --- */}
      <div style={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          background: '#f5f5f5',
          padding: '8px',
          borderRadius: '6px 6px 0 0',
          border: '1px solid #d9d9d9',
          borderBottom: 'none'
      }}>
        <span style={{ fontSize: 12, color: '#666' }}>
          Nhập văn bản. Sử dụng các nút chức năng bên phải.
        </span>
        
        <div style={{ display: 'flex', gap: 8 }}>
            <Tooltip title="Chèn ngắt dòng (Xuống dòng)">
                <Button 
                    icon={<EnterOutlined />} 
                    onClick={handleInsertBreak}
                    size="small"
                >
                    Xuống dòng
                </Button>
            </Tooltip>

            <Tooltip title="Mở bàn phím công thức Toán">
                <Button 
                  type="primary" 
                  icon={<FunctionOutlined />} 
                  onClick={openMathModal}
                  size="small"
                >
                  Chèn Toán (LaTeX)
                </Button>
            </Tooltip>
        </div>
      </div>

      {/* --- VÙNG NHẬP LIỆU CHÍNH (TEXT AREA) --- */}
      <Input.TextArea
        ref={textAreaRef}
        value={value}
        onChange={handleTextChange}
        onSelect={saveCursorPosition} 
        onClick={saveCursorPosition}
        onKeyUp={saveCursorPosition}
        onFocus={saveCursorPosition}
        placeholder={placeholder || "Ví dụ: Diện tích hình tròn được tính bằng công thức..."}
        autoSize={{ minRows: 4, maxRows: 10 }}
        style={{ 
            borderRadius: '0 0 6px 6px',
            fontSize: '16px',
            lineHeight: '1.6'
        }}
      />

      {/* --- MODAL SOẠN THẢO CÔNG THỨC NÂNG CẤP --- */}
      <Modal
        title={
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <FunctionOutlined style={{ color: '#1677ff' }} />
                <span>Bàn phím Toán học</span>
            </div>
        }
        open={isModalOpen}
        onOk={handleInsertLatexToText}
        onCancel={() => setIsModalOpen(false)}
        okText="Chèn vào bài học"
        cancelText="Hủy"
        width={750}
        destroyOnClose 
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: 20, marginTop: 12 }}>
          
          {/* NÂNG CẤP: Khu vực MathLive Input */}
          <div>
              <div style={{ fontSize: 12, fontWeight: 'bold', color: '#888', marginBottom: 6, textTransform: 'uppercase' }}>
                  KẾT QUẢ HIỂN THỊ:
              </div>
              <div style={{ border: '2px solid #1677ff', borderRadius: 8, padding: '16px', background: '#fafafa', minHeight: '90px' }}>
                <math-field 
                    ref={mfRef} 
                    style={{ 
                        width: '100%', 
                        fontSize: '32px', // Tăng kích thước chữ cho dễ nhìn
                        outline: 'none', 
                        border: 'none', 
                        background: 'transparent'
                    }}
                >
                </math-field>
              </div>
          </div>

          {/* NÂNG CẤP: Toolbar ký hiệu (Sử dụng CSS Grid) */}
          <div style={{ background: '#fff', border: '1px solid #e8e8e8', borderRadius: 8, overflow: 'hidden' }}>
            <Tabs 
                defaultActiveKey="algebra" 
                size="middle"
                tabBarStyle={{ marginBottom: 0, paddingLeft: 16, background: '#fafafa', borderBottom: '1px solid #e8e8e8' }}
                items={MATH_GROUPS.map(group => ({
                    key: group.key,
                    label: group.label,
                    children: (
                        <div style={{ 
                            padding: '16px', 
                            display: 'grid', 
                            // Lưới tự động dãn đều
                            gridTemplateColumns: 'repeat(auto-fill, minmax(60px, 1fr))', 
                            gap: '10px', 
                            maxHeight: '250px', 
                            overflowY: 'auto' 
                        }}>
                            {group.items.map((tool, idx) => (
                                <Tooltip title={tool.label} key={idx} placement="bottom">
                                    <Button 
                                        size="large" 
                                        onClick={() => insertToMathField(tool.latex)}
                                        style={{ 
                                            fontWeight: 'bold', 
                                            fontFamily: '"Cambria Math", "Times New Roman", serif', // Font toán học chuẩn
                                            fontSize: '18px',
                                            height: '45px', // Cố định hình vuông
                                            width: '100%',
                                            display: 'flex',
                                            justifyContent: 'center',
                                            alignItems: 'center',
                                            background: '#fff',
                                            borderColor: '#d9d9d9',
                                            boxShadow: '0 1px 2px rgba(0,0,0,0.05)'
                                        }}
                                    >
                                        {tool.icon}
                                    </Button>
                                </Tooltip>
                            ))}
                        </div>
                    )
                }))}
            />
          </div>
          
          <div style={{ fontSize: 12, color: '#888', fontStyle: 'italic', textAlign: 'center' }}>
             * Mẹo: Click vào biểu tượng để chèn. Nếu có ô trống, hệ thống sẽ tự động focus để bạn gõ số.
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default MathEditor;