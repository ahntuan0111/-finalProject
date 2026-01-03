import React, { useEffect, useRef, useState } from 'react';
import { Button, Tooltip, Tabs, Radio } from 'antd';
import { 
  FieldStringOutlined, 
  FunctionOutlined, 
  EnterOutlined 
} from '@ant-design/icons';
import 'mathlive';

// --- DANH SÁCH CÁC KÝ HIỆU (GIỮ NGUYÊN) ---
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

const MathEditor = ({ value, onChange, placeholder }) => {
  const mfRef = useRef(null);
  const [currentMode, setCurrentMode] = useState('text'); // State để quản lý UI nút bấm

  useEffect(() => {
    import('mathlive').then(() => {
      if (mfRef.current) {
        const mf = mfRef.current;

        // 1. Cấu hình cơ bản
        mf.defaultMode = 'text'; 
        mf.smartMode = false;
        mf.mathVirtualKeyboardPolicy = 'manual';

        // 2. Load giá trị ban đầu
        if (value && mf.value !== value) {
            mf.value = value;
        }

        // 3. XỬ LÝ SỰ KIỆN ENTER (QUAN TRỌNG)
        mf.addEventListener('keydown', (evt) => {
            if (evt.key === 'Enter') {
                // Ngăn chặn hành vi mặc định (Submit form)
                evt.preventDefault();
                evt.stopPropagation();
                
                // Chèn lệnh xuống dòng của LaTeX
                mf.executeCommand(['insert', '\\newline']);
            }
        });

        // 4. Lắng nghe sự thay đổi mode để cập nhật UI nút bấm
        mf.addEventListener('mode-change', (evt) => {
            setCurrentMode(evt.detail.mode); // 'text' hoặc 'math'
        });
      }
    });
  }, []);

  const handleInput = (evt) => {
    if (onChange) {
      onChange(evt.target.value); 
    }
  };

  useEffect(() => {
    if (mfRef.current && value !== mfRef.current.value) {
        mfRef.current.value = value || '';
    }
  }, [value]);

  // --- CÁC HÀM THAO TÁC ---

  const insertLatex = (latex) => {
    if (mfRef.current) {
      mfRef.current.focus();
      mfRef.current.executeCommand(['insert', latex]);
    }
  };

  const switchMode = (mode) => {
    if (mfRef.current) {
        mfRef.current.executeCommand(['switch-mode', mode]);
        mfRef.current.focus();
        setCurrentMode(mode);
    }
  };

  return (
    <div style={{ 
        border: '1px solid #d9d9d9', 
        borderRadius: 8, 
        overflow: 'hidden', 
        backgroundColor: '#fff',
        display: 'flex',
        flexDirection: 'column'
    }}>
      {/* TOOLBAR: CHUYỂN ĐỔI MODE */}
      <div style={{ 
          padding: '8px 12px', 
          background: '#f0f2f5', 
          borderBottom: '1px solid #e8e8e8',
          display: 'flex',
          alignItems: 'center',
          gap: 12
      }}>
        <span style={{ fontWeight: 600, fontSize: 13 }}>Chế độ nhập:</span>
        <Radio.Group 
            value={currentMode} 
            onChange={(e) => switchMode(e.target.value)} 
            buttonStyle="solid"
            size="small"
        >
            <Radio.Button value="text">
                <FieldStringOutlined /> Văn bản (Text)
            </Radio.Button>
            <Radio.Button value="math">
                <FunctionOutlined /> Công thức (Math)
            </Radio.Button>
        </Radio.Group>
        
        <div style={{ flex: 1 }}></div>
        <Tooltip title="Nhấn Enter để xuống dòng">
             <EnterOutlined style={{ color: '#999' }} />
        </Tooltip>
      </div>

      {/* TOOLBAR: CÁC KÝ HIỆU TOÁN HỌC */}
      <div style={{ background: '#fafafa', borderBottom: '1px solid #f0f0f0' }}>
        <Tabs 
            defaultActiveKey="basic" 
            size="small"
            tabBarStyle={{ marginBottom: 0, paddingLeft: 10 }}
            items={MATH_GROUPS.map(group => ({
                key: group.key,
                label: group.label,
                children: (
                    <div style={{ 
                        padding: '8px', 
                        display: 'flex', 
                        flexWrap: 'wrap', 
                        gap: 6 
                    }}>
                        {group.items.map((tool, idx) => (
                            <Tooltip title={tool.label} key={idx}>
                                <Button 
                                    size="small" 
                                    onClick={() => insertLatex(tool.latex)}
                                    style={{ 
                                        fontWeight: 'bold', 
                                        fontFamily: 'Cambria, serif',
                                        minWidth: 36,
                                        fontSize: '14px'
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

      {/* VÙNG NHẬP LIỆU */}
      <math-field 
        ref={mfRef} 
        onInput={handleInput}
        placeholder={placeholder}
        style={{
            width: '100%',
            fontSize: '18px',
            padding: '15px',
            outline: 'none',
            border: 'none',
            display: 'block',
            minHeight: '120px',    
            lineHeight: '1.6',     
            whiteSpace: 'pre-wrap', // Hỗ trợ hiển thị nhiều dòng
            wordBreak: 'break-word',
        }}
      >
        {value}
      </math-field>

      {/* FOOTER */}
      <div style={{ 
          padding: '4px 10px', 
          background: '#fafafa', 
          borderTop: '1px solid #f0f0f0',
          fontSize: '11px',
          color: '#888',
          textAlign: 'right'
      }}>
          Sử dụng nút chuyển chế độ hoặc phím tắt (Alt+T: Text, Alt+M: Math)
      </div>
    </div>
  );
};

export default MathEditor;