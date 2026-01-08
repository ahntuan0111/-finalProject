import React, { useEffect, useRef, useState } from 'react';
import { Button, Tooltip, Tabs, Modal, Input } from 'antd';
// Import thêm icon EnterOutlined cho nút xuống dòng
import { FunctionOutlined, EnterOutlined } from '@ant-design/icons';
import 'mathlive';

// --- DANH SÁCH CÁC KÝ HIỆU TOÁN HỌC ---
const MATH_GROUPS = [
  {
    key: 'basic',
    label: 'Cơ bản',
    items: [
      { label: 'Phân số', latex: '\\frac{\\placeholder}{\\placeholder}', icon: 'numerator/denominator' },
      { label: 'Căn bậc 2', latex: '\\sqrt{\\placeholder}', icon: '√' },
      { label: 'Bình phương', latex: '^2', icon: 'x²' },
      { label: 'Mũ tổng quát', latex: '^{\\placeholder}', icon: 'xⁿ' },
      { label: 'Chỉ số dưới', latex: '_\\placeholder', icon: 'xₙ' },
      { label: 'Ngoặc đơn', latex: '(\\placeholder)', icon: '( )' },
      { label: 'Ngoặc vuông', latex: '[\\placeholder]', icon: '[ ]' },
      { label: 'Trị tuyệt đối', latex: '|\\placeholder|', icon: '|x|' },
    ]
  },
  {
    key: 'geometry',
    label: 'Hình học',
    items: [
      { label: 'Góc', latex: '\\widehat{\\placeholder}', icon: 'x̂' },
      { label: 'Ký hiệu Góc', latex: '\\angle', icon: '∠' },
      { label: 'Độ', latex: '^\\circ', icon: '°' },
      { label: 'Tam giác', latex: '\\triangle', icon: '△' },
      { label: 'Vuông góc', latex: '\\perp', icon: '⊥' },
      { label: 'Song song', latex: '\\parallel', icon: '∥' },
      { label: 'Dấu Pi', latex: '\\pi', icon: 'π' },
      { label: 'Vector', latex: '\\vec{\\placeholder}', icon: '→' },
    ]
  },
  {
    key: 'algebra',
    label: 'Đại số',
    items: [
      { label: 'Delta', latex: '\\Delta', icon: 'Δ' },
      { label: 'Alpha', latex: '\\alpha', icon: 'α' },
      { label: 'Beta', latex: '\\beta', icon: 'β' },
      { label: 'Omega', latex: '\\Omega', icon: 'Ω' },
      { label: 'Tổng (Sigma)', latex: '\\sum', icon: '∑' },
      { label: 'Vô cực', latex: '\\infty', icon: '∞' },
      { label: 'Hệ phương trình', latex: '\\begin{cases} \\placeholder \\\\ \\placeholder \\end{cases}', icon: '{' },
      { label: 'Tích phân', latex: '\\int', icon: '∫' },
      { label: 'Giới hạn', latex: '\\lim_{x \\to \\infty}', icon: 'lim' },
    ]
  },
  {
    key: 'logic',
    label: 'Logic',
    items: [
      { label: 'Thuộc', latex: '\\in', icon: '∈' },
      { label: 'Không thuộc', latex: '\\notin', icon: '∉' },
      { label: 'Giao', latex: '\\cap', icon: '∩' },
      { label: 'Hợp', latex: '\\cup', icon: '∪' },
      { label: 'Suy ra', latex: '\\Rightarrow', icon: '⇒' },
      { label: 'Tương đương', latex: '\\Leftrightarrow', icon: '⇔' },
      { label: 'Với mọi', latex: '\\forall', icon: '∀' },
      { label: 'Tồn tại', latex: '\\exists', icon: '∃' },
    ]
  }
];

const MathEditor = ({ value = '', onChange, placeholder }) => {
  const textAreaRef = useRef(null);
  const mfRef = useRef(null);
  
  // State quản lý Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [cursorPos, setCursorPos] = useState(null);

  // --- 1. XỬ LÝ NHẬP LIỆU VÀ CON TRỎ ---
  const handleTextChange = (e) => {
    if (onChange) onChange(e.target.value);
  };

  // Luôn cập nhật vị trí con trỏ khi người dùng click hoặc gõ phím
  const saveCursorPosition = (e) => {
    setCursorPos(e.target.selectionStart);
  };

  // --- 2. HÀM CHÈN TEXT VÀO VỊ TRÍ CON TRỎ (Dùng chung cho cả <br/> và LaTeX) ---
  const insertTextAtCursor = (textToInsert) => {
    const currentText = value || '';
    // Nếu chưa click vào đâu (cursorPos là null), chèn xuống cuối
    const position = cursorPos !== null ? cursorPos : currentText.length;
    
    // Cắt chuỗi và chèn
    const newText = 
      currentText.substring(0, position) + 
      textToInsert + 
      currentText.substring(position);

    // Cập nhật giá trị ra ngoài Form
    if (onChange) onChange(newText);
    
    // Tính toán vị trí con trỏ mới (sau đoạn text vừa chèn)
    const newPos = position + textToInsert.length;
    setCursorPos(newPos);

    // Focus lại vào ô nhập liệu và đặt con trỏ đúng chỗ
    setTimeout(() => {
        if(textAreaRef.current) {
            textAreaRef.current.focus();
            // setSelectionRange giúp người dùng gõ tiếp ngay sau chỗ vừa chèn
            textAreaRef.current.resizableTextArea.textArea.setSelectionRange(newPos, newPos);
        }
    }, 50);
  };

  // --- 3. XỬ LÝ NÚT XUỐNG DÒNG ---
  const handleInsertBreak = () => {
    // Chèn thẻ <br/>. Mobile App (WebView) sẽ hiểu đây là xuống dòng.
    insertTextAtCursor('<br/>');
  };

  // --- 4. XỬ LÝ MODAL TOÁN HỌC ---
  const openMathModal = () => {
    // Nếu chưa có vị trí con trỏ, đặt mặc định ở cuối để tránh lỗi
    if (cursorPos === null && value) {
        setCursorPos(value.length);
    }
    setIsModalOpen(true);
    
    // Reset và Focus vào MathField sau khi modal mở
    setTimeout(() => {
      if (mfRef.current) {
        mfRef.current.value = ''; 
        mfRef.current.focus();
      }
    }, 100);
  };

  const handleInsertLatexToText = () => {
    // Lấy giá trị trực tiếp từ MathField
    const latexValue = mfRef.current ? mfRef.current.getValue() : '';

    if (!latexValue) {
      setIsModalOpen(false);
      return;
    }

    // Format công thức: Thêm khoảng trắng và dấu $ để Mobile nhận diện
    const formattedLatex = ` $${latexValue}$ `; 
    
    // Dùng hàm chèn chung
    insertTextAtCursor(formattedLatex);
    
    setIsModalOpen(false);
  };

  // Insert từ Toolbar ký hiệu vào MathField trong Modal
  const insertToMathField = (latex) => {
    if (mfRef.current) {
      mfRef.current.executeCommand(['insert', latex]);
      mfRef.current.focus();
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
            {/* NÚT XUỐNG DÒNG (MỚI) */}
            <Tooltip title="Chèn ngắt dòng (Xuống dòng)">
                <Button 
                    icon={<EnterOutlined />} 
                    onClick={handleInsertBreak}
                    size="small"
                >
                    Xuống dòng
                </Button>
            </Tooltip>

            {/* NÚT CHÈN TOÁN */}
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
        // Các sự kiện quan trọng để theo dõi vị trí con trỏ
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

      {/* --- MODAL SOẠN THẢO CÔNG THỨC --- */}
      <Modal
        title="Soạn thảo Công thức Toán"
        open={isModalOpen}
        onOk={handleInsertLatexToText}
        onCancel={() => setIsModalOpen(false)}
        okText="Chèn vào bài học"
        cancelText="Hủy"
        width={700}
        destroyOnClose // Reset modal khi đóng
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          
          {/* MathLive Input */}
          <div style={{ border: '1px solid #1677ff', borderRadius: 8, padding: 4 }}>
            <math-field 
                ref={mfRef} 
                style={{ 
                    width: '100%', 
                    fontSize: '24px', 
                    padding: '10px', 
                    outline: 'none', 
                    border: 'none', 
                    minHeight: '50px' 
                }}
            >
            </math-field>
          </div>

          {/* Toolbar ký hiệu trong Modal */}
          <div style={{ background: '#fafafa', border: '1px solid #f0f0f0', borderRadius: 8 }}>
            <Tabs 
                defaultActiveKey="basic" 
                size="small"
                tabBarStyle={{ marginBottom: 0, paddingLeft: 10 }}
                items={MATH_GROUPS.map(group => ({
                    key: group.key,
                    label: group.label,
                    children: (
                        <div style={{ 
                            padding: '12px', 
                            display: 'flex', 
                            flexWrap: 'wrap', 
                            gap: 8, 
                            maxHeight: '200px', 
                            overflowY: 'auto' 
                        }}>
                            {group.items.map((tool, idx) => (
                                <Tooltip title={tool.label} key={idx}>
                                    <Button 
                                        size="middle" 
                                        onClick={() => insertToMathField(tool.latex)}
                                        style={{ 
                                            fontWeight: 'bold', 
                                            fontFamily: 'Cambria, serif', 
                                            minWidth: 40, 
                                            fontSize: '16px' 
                                        }}
                                    >
                                        {tool.icon === 'numerator/denominator' ? (
                                            <div style={{display:'flex', flexDirection:'column', lineHeight:0.8, fontSize:10}}>
                                                <span>a</span><span style={{borderTop:'1px solid'}}>b</span>
                                            </div>
                                        ) : tool.icon}
                                    </Button>
                                </Tooltip>
                            ))}
                        </div>
                    )
                }))}
            />
          </div>
          
          <div style={{ fontSize: 12, color: '#888', fontStyle: 'italic' }}>
             * Mẹo: Bạn có thể gõ trực tiếp (vd: gõ "sqrt" rồi Tab) hoặc dùng các nút ở trên.
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default MathEditor;