import React, { useState, useEffect } from 'react';
import {
  Card, Table, Typography, Breadcrumb, Tag,
  Tooltip, Space, Button, Modal, Badge, Empty
} from 'antd';
import {
  CheckCircleOutlined, CloseCircleOutlined,
  EyeOutlined, ReloadOutlined, FileImageOutlined,
  RobotOutlined
} from '@ant-design/icons';
import submissionApi from '../../api/submissionApi';

const { Title, Text, Paragraph } = Typography;

/* ── Fallback SVG inline (không cần mạng, không cần via.placeholder.com) ── */
const FALLBACK_SVG = `data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='120' height='80' viewBox='0 0 120 80'><rect width='120' height='80' fill='%23f5f5f5' rx='6'/><text x='50%' y='44%' dominant-baseline='middle' text-anchor='middle' font-size='11' fill='%23bbb' font-family='sans-serif'>Không có ảnh</text><text x='50%' y='66%' dominant-baseline='middle' text-anchor='middle' font-size='9' fill='%23ccc' font-family='sans-serif'>No image</text></svg>`;

/* ── Normalize Firebase URL (same pattern as Android app) ─────────────────── */
const normalizeFirebaseUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('https://') || url.startsWith('http://')) return url;
  if (url.startsWith('firebasestorage.googleapis.com')) return `https://${url}`;
  if (url.startsWith('storage.googleapis.com')) return `https://${url}`;
  return url;
};

/* ── Thumbnail with error handling ────────────────────────────────────────── */
const SubmissionThumbnail = ({ url, onPreview }) => {
  const [errored, setErrored] = useState(false);
  const normalized = normalizeFirebaseUrl(url);

  if (!normalized || errored) {
    return (
      <div style={{
        width: 80, height: 60, borderRadius: 6,
        background: '#fafafa', border: '1px dashed #d9d9d9',
        display: 'flex', flexDirection: 'column',
        alignItems: 'center', justifyContent: 'center', gap: 2
      }}>
        <FileImageOutlined style={{ fontSize: 18, color: '#bbb' }} />
        <Text style={{ fontSize: 9, color: '#ccc' }}>Không có ảnh</Text>
      </div>
    );
  }

  return (
    <div style={{ position: 'relative', display: 'inline-block' }}>
      <img
        src={normalized}
        alt="Submission"
        onError={() => setErrored(true)}
        style={{
          width: 80, height: 60,
          objectFit: 'cover',
          borderRadius: 6,
          border: '1px solid #f0f0f0',
          cursor: 'pointer',
          transition: 'transform 0.15s, box-shadow 0.15s',
          display: 'block'
        }}
        onMouseEnter={e => {
          e.currentTarget.style.transform = 'scale(1.05)';
          e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)';
        }}
        onMouseLeave={e => {
          e.currentTarget.style.transform = 'scale(1)';
          e.currentTarget.style.boxShadow = 'none';
        }}
        onClick={() => onPreview(normalized)}
      />
      <div style={{
        position: 'absolute', bottom: 2, right: 2,
        background: 'rgba(0,0,0,0.45)', borderRadius: 3,
        padding: '1px 3px'
      }}>
        <EyeOutlined style={{ fontSize: 9, color: '#fff' }} />
      </div>
    </div>
  );
};

/* ── Main Page ────────────────────────────────────────────────────────────── */
const SubmissionPage = () => {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [selectedRow, setSelectedRow] = useState(null);
  const [detailOpen, setDetailOpen] = useState(false);

  useEffect(() => {
    fetchSubmissions();
  }, []);

  const fetchSubmissions = async () => {
    setLoading(true);
    try {
      const res = await submissionApi.getAll();
      if (res.success) setSubmissions(res.data || []);
    } catch (err) {
      console.error('Lỗi tải danh sách bài nộp:', err);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: '#',
      dataIndex: 'id',
      width: 60,
      align: 'center',
      render: (_, __, idx) => (
        <Text type="secondary" style={{ fontSize: 12 }}>{idx + 1}</Text>
      )
    },
    {
      title: 'Học sinh',
      dataIndex: 'userId',
      width: 100,
      align: 'center',
      render: (text) => (
        <Tag color="blue" style={{ borderRadius: 12, fontWeight: 600 }}>
          #{text}
        </Tag>
      )
    },
    {
      title: 'Bài tập',
      dataIndex: 'exerciseId',
      width: 100,
      align: 'center',
      render: (text) => (
        <Tag color="purple" style={{ borderRadius: 12, fontWeight: 600 }}>
          #{text}
        </Tag>
      )
    },
    {
      title: 'Ảnh bài nộp',
      dataIndex: 'imageUrl',
      width: 130,
      align: 'center',
      render: (url) => (
        <SubmissionThumbnail
          url={url}
          onPreview={(u) => setPreviewUrl(u)}
        />
      )
    },
    {
      title: 'Kết quả AI',
      dataIndex: 'isValid',
      width: 140,
      align: 'center',
      filters: [
        { text: 'Hợp lệ', value: true },
        { text: 'Không hợp lệ', value: false },
      ],
      onFilter: (value, record) => record.isValid === value,
      render: (isValid) =>
        isValid == null ? (
          <Tag icon={<RobotOutlined />} color="default">Chờ chấm</Tag>
        ) : isValid ? (
          <Tag icon={<CheckCircleOutlined />} color="success" style={{ borderRadius: 12 }}>
            Hợp lệ
          </Tag>
        ) : (
          <Tag icon={<CloseCircleOutlined />} color="error" style={{ borderRadius: 12 }}>
            Không hợp lệ
          </Tag>
        )
    },
    {
      title: 'Nhận xét AI',
      dataIndex: 'aiReason',
      ellipsis: true,
      render: (reason) =>
        reason ? (
          <Tooltip title={reason} overlayStyle={{ maxWidth: 360 }}>
            <Text type="secondary" style={{ fontSize: 12, cursor: 'help' }}>
              {reason.length > 60 ? reason.slice(0, 60) + '…' : reason}
            </Text>
          </Tooltip>
        ) : (
          <Text type="secondary" style={{ fontSize: 11, color: '#ccc' }}>—</Text>
        )
    },
    {
      title: 'Điểm số',
      dataIndex: 'score',
      width: 90,
      align: 'center',
      render: (score) =>
        score != null ? (
          <Text strong style={{ color: score >= 5 ? '#52c41a' : '#ff4d4f', fontSize: 15 }}>
            {score}
          </Text>
        ) : <Text type="secondary">—</Text>
    },
    {
      title: 'Thời gian',
      dataIndex: 'submittedAt',
      width: 160,
      align: 'right',
      sorter: (a, b) => new Date(a.submittedAt) - new Date(b.submittedAt),
      defaultSortOrder: 'descend',
      render: (d) => d
        ? <Text style={{ fontSize: 12 }}>{new Date(d).toLocaleString('vi-VN')}</Text>
        : '—'
    },
    {
      title: '',
      width: 50,
      align: 'center',
      render: (_, record) => (
        <Button
          type="text"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => { setSelectedRow(record); setDetailOpen(true); }}
          style={{ color: '#1a56db' }}
        />
      )
    }
  ];

  /* stats */
  const total = submissions.length;
  const valid = submissions.filter(s => s.isValid === true).length;
  const invalid = submissions.filter(s => s.isValid === false).length;
  const pending = submissions.filter(s => s.isValid == null).length;

  return (
    <div style={{ padding: '24px', background: '#f5f7fa', minHeight: '100vh' }}>

      {/* ── Header ── */}
      <div style={{ marginBottom: 24 }}>
        <Breadcrumb
          items={[{ title: 'Dashboard' }, { title: 'Bài nộp viết tay' }]}
          style={{ fontSize: 13 }}
        />
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: 8 }}>
          <Title level={3} style={{ margin: 0 }}>
            Quản lý Bài Nộp Viết Tay
          </Title>
          <Button
            icon={<ReloadOutlined />}
            onClick={fetchSubmissions}
            loading={loading}
          >
            Làm mới
          </Button>
        </div>
      </div>

      {/* ── Stats Row ── */}
      <div style={{ display: 'flex', gap: 16, marginBottom: 24 }}>
        {[
          { label: 'Tổng bài nộp', value: total, color: '#1a56db', bg: '#eff6ff' },
          { label: 'Hợp lệ', value: valid, color: '#52c41a', bg: '#f6ffed' },
          { label: 'Không hợp lệ', value: invalid, color: '#ff4d4f', bg: '#fff2f0' },
          { label: 'Chờ chấm', value: pending, color: '#faad14', bg: '#fffbe6' },
        ].map(({ label, value, color, bg }) => (
          <Card
            key={label}
            variant="borderless"
            style={{ flex: 1, borderRadius: 12, background: bg, border: `1px solid ${color}22` }}
            styles={{ body: { padding: '16px 20px' } }}
          >
            <Text style={{ fontSize: 12, color: '#888' }}>{label}</Text>
            <div style={{ fontSize: 26, fontWeight: 800, color, marginTop: 2 }}>{value}</div>
          </Card>
        ))}
      </div>

      {/* ── Table ── */}
      <Card
        variant="borderless"
        style={{ borderRadius: 12, boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}
        styles={{ body: { padding: '24px' } }}
      >
        <Table
          columns={columns}
          dataSource={submissions}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 15, showTotal: (t) => `${t} bài nộp` }}
          locale={{ emptyText: <Empty description="Chưa có bài nộp nào" /> }}
          rowClassName={() => 'custom-table-row'}
          size="small"
        />
      </Card>

      {/* ── Image Preview Modal ── */}
      <Modal
        open={!!previewUrl}
        onCancel={() => setPreviewUrl(null)}
        footer={null}
        centered
        width="auto"
        styles={{ body: { padding: 0 } }}
      >
        <img
          src={previewUrl}
          alt="Preview"
          style={{ maxWidth: '90vw', maxHeight: '85vh', display: 'block', borderRadius: 8 }}
          onError={(e) => { e.currentTarget.src = FALLBACK_SVG; }}
        />
      </Modal>

      {/* ── Detail Modal ── */}
      <Modal
        open={detailOpen}
        onCancel={() => setDetailOpen(false)}
        title={
          <Space>
            <RobotOutlined style={{ color: '#1a56db' }} />
            <span>Chi tiết bài nộp #{selectedRow?.id}</span>
          </Space>
        }
        footer={null}
        centered
        width={560}
      >
        {selectedRow && (
          <div style={{ display: 'flex', gap: 20 }}>
            {/* Image */}
            <div style={{ flexShrink: 0 }}>
              <img
                src={normalizeFirebaseUrl(selectedRow.imageUrl)}
                alt="Submission"
                style={{ width: 160, height: 120, objectFit: 'cover', borderRadius: 8, border: '1px solid #f0f0f0' }}
                onError={(e) => { e.currentTarget.src = FALLBACK_SVG; }}
              />
              <Button
                size="small"
                type="link"
                style={{ display: 'block', textAlign: 'center', marginTop: 4 }}
                onClick={() => setPreviewUrl(normalizeFirebaseUrl(selectedRow.imageUrl))}
              >
                Xem toàn cảnh
              </Button>
            </div>

            {/* Info */}
            <div style={{ flex: 1 }}>
              <div style={{ marginBottom: 12 }}>
                <Text type="secondary" style={{ fontSize: 11 }}>Học sinh</Text>
                <div><Text strong>User #{selectedRow.userId}</Text></div>
              </div>
              <div style={{ marginBottom: 12 }}>
                <Text type="secondary" style={{ fontSize: 11 }}>Bài tập</Text>
                <div><Text strong>Exercise #{selectedRow.exerciseId}</Text></div>
              </div>
              <div style={{ marginBottom: 12 }}>
                <Text type="secondary" style={{ fontSize: 11 }}>Kết quả AI</Text>
                <div>
                  {selectedRow.isValid
                    ? <Tag icon={<CheckCircleOutlined />} color="success">Hợp lệ</Tag>
                    : <Tag icon={<CloseCircleOutlined />} color="error">Không hợp lệ</Tag>
                  }
                  {selectedRow.score != null && (
                    <Text strong style={{ marginLeft: 8, color: selectedRow.score >= 5 ? '#52c41a' : '#ff4d4f' }}>
                      {selectedRow.score} điểm
                    </Text>
                  )}
                </div>
              </div>
              <div>
                <Text type="secondary" style={{ fontSize: 11 }}>Nhận xét AI</Text>
                <Paragraph
                  style={{ fontSize: 13, marginTop: 4, marginBottom: 0, color: '#555', background: '#fafafa', padding: 8, borderRadius: 6 }}
                >
                  {selectedRow.aiReason || 'Chưa có nhận xét'}
                </Paragraph>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default SubmissionPage;
