-- ==========================================
-- SCRIPT THÊM DỮ LIỆU KHTN LỚP 6, 7, 8, 9
-- VÀ 2 QUIZ CHO KHTN LỚP 7
-- ==========================================

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

-- --------------------------------------------------------
-- 1. THÊM CHƯƠNG (CHAPTERS) CHO KHTN (Subject ID: 2, 4, 6, 8)
-- KHTN 6 (Subject ID: 2)
INSERT INTO `chapters` (`id`, `subject_id`, `title_vi`, `order_no`, `title_en`) VALUES
(21, 2, 'Chương 1: Mở đầu về khoa học tự nhiên', 1, 'Chapter 1: Introduction to Natural Sciences'),
(22, 2, 'Chương 2: Các phép đo', 2, 'Chapter 2: Measurements'),
(23, 2, 'Chương 3: Các thể của chất', 3, 'Chapter 3: States of Matter');

-- KHTN 7 (Subject ID: 4)
INSERT INTO `chapters` (`id`, `subject_id`, `title_vi`, `order_no`, `title_en`) VALUES
(24, 4, 'Chương 1: Nguyên tử - Nguyên tố hóa học', 1, 'Chapter 1: Atoms - Chemical Elements'),
(25, 4, 'Chương 2: Phân tử - Liên kết hóa học', 2, 'Chapter 2: Molecules - Chemical Bonds'),
(26, 4, 'Chương 3: Tốc độ', 3, 'Chapter 3: Speed');

-- KHTN 8 (Subject ID: 6)
INSERT INTO `chapters` (`id`, `subject_id`, `title_vi`, `order_no`, `title_en`) VALUES
(27, 6, 'Chương 1: Tốc độ phản ứng', 1, 'Chapter 1: Reaction Rate'),
(28, 6, 'Chương 2: Acid - Base - pH - Muối', 2, 'Chapter 2: Acid - Base - pH - Salts'),
(29, 6, 'Chương 3: Khối lượng riêng và áp suất', 3, 'Chapter 3: Density and pressure');

-- KHTN 9 (Subject ID: 8)
INSERT INTO `chapters` (`id`, `subject_id`, `title_vi`, `order_no`, `title_en`) VALUES
(30, 8, 'Chương 1: Năng lượng cơ học', 1, 'Chapter 1: Mechanical Energy'),
(31, 8, 'Chương 2: Ánh sáng', 2, 'Chapter 2: Light'),
(32, 8, 'Chương 3: Điện', 3, 'Chapter 3: Electricity');

-- --------------------------------------------------------
-- 2. THÊM BÀI HỌC (LESSONS) CHO TỪNG CHƯƠNG (3 bài / chương)
-- KHTN 6 Lessons
INSERT INTO `lessons` (`id`, `chapter_id`, `title_vi`, `video_url`, `order_no`, `title_en`) VALUES
(21, 21, 'Bài 1: Giới thiệu về khoa học tự nhiên', 'https://www.youtube.com/watch?v=1', 1, 'Lesson 1: Introduction to Natural Sciences'),
(22, 21, 'Bài 2: Các lĩnh vực chủ yếu của KHTN', 'https://www.youtube.com/watch?v=2', 2, 'Lesson 2: Main fields of Natural Sciences'),
(23, 21, 'Bài 3: Quy định an toàn trong phòng thực hành', 'https://www.youtube.com/watch?v=3', 3, 'Lesson 3: Safety rules in the laboratory'),
(24, 22, 'Bài 4: Đo chiều dài', 'https://www.youtube.com/watch?v=4', 1, 'Lesson 4: Length measurement'),
(25, 22, 'Bài 5: Đo khối lượng', 'https://www.youtube.com/watch?v=5', 2, 'Lesson 5: Mass measurement'),
(26, 22, 'Bài 6: Đo thời gian', 'https://www.youtube.com/watch?v=6', 3, 'Lesson 6: Time measurement'),
(27, 23, 'Bài 7: Sự đa dạng của chất', 'https://www.youtube.com/watch?v=7', 1, 'Lesson 7: Diversity of matter'),
(28, 23, 'Bài 8: Các thể của chất', 'https://www.youtube.com/watch?v=8', 2, 'Lesson 8: States of matter'),
(29, 23, 'Bài 9: Sự chuyển thể của chất', 'https://www.youtube.com/watch?v=9', 3, 'Lesson 9: Change of state');

-- KHTN 7 Lessons
INSERT INTO `lessons` (`id`, `chapter_id`, `title_vi`, `video_url`, `order_no`, `title_en`) VALUES
(30, 24, 'Bài 1: Phương pháp và kĩ năng học tập KHTN', 'https://www.youtube.com/watch?v=10', 1, 'Lesson 1: Method and skills in learning Natural Science'),
(31, 24, 'Bài 2: Nguyên tử', 'https://www.youtube.com/watch?v=11', 2, 'Lesson 2: Atoms'),
(32, 24, 'Bài 3: Nguyên tố hóa học', 'https://www.youtube.com/watch?v=12', 3, 'Lesson 3: Chemical Elements'),
(33, 25, 'Bài 4: Sơ lược về bảng tuần hoàn', 'https://www.youtube.com/watch?v=13', 1, 'Lesson 4: Overview of the Periodic Table'),
(34, 25, 'Bài 5: Phân tử - Đơn chất - Hợp chất', 'https://www.youtube.com/watch?v=14', 2, 'Lesson 5: Molecules - Simple Substances - Compounds'),
(35, 25, 'Bài 6: Giới thiệu về liên kết hóa học', 'https://www.youtube.com/watch?v=15', 3, 'Lesson 6: Introduction to Chemical Bonds'),
(36, 26, 'Bài 7: Hóa trị và công thức hóa học', 'https://www.youtube.com/watch?v=16', 1, 'Lesson 7: Valency and Chemical Formulas'),
(37, 26, 'Bài 8: Tốc độ chuyển động', 'https://www.youtube.com/watch?v=17', 2, 'Lesson 8: Speed of Motion'),
(38, 26, 'Bài 9: Đồ thị quãng đường - thời gian', 'https://www.youtube.com/watch?v=18', 3, 'Lesson 9: Distance-Time Graph');

-- KHTN 8 Lessons
INSERT INTO `lessons` (`id`, `chapter_id`, `title_vi`, `video_url`, `order_no`, `title_en`) VALUES
(39, 27, 'Bài 1: Biến đổi vật lí và biến đổi hóa học', 'https://youtube.com', 1, 'Lesson 1: Physical and Chemical Changes'),
(40, 27, 'Bài 2: Phản ứng hóa học', 'https://youtube.com', 2, 'Lesson 2: Chemical Reactions'),
(41, 27, 'Bài 3: Tốc độ phản ứng', 'https://youtube.com', 3, 'Lesson 3: Reaction Rate'),
(42, 28, 'Bài 4: Dung dịch và nồng độ', 'https://youtube.com', 1, 'Lesson 4: Solutions and Concentration'),
(43, 28, 'Bài 5: Acid - Base', 'https://youtube.com', 2, 'Lesson 5: Acid - Base'),
(44, 28, 'Bài 6: Thang pH', 'https://youtube.com', 3, 'Lesson 6: pH Scale'),
(45, 29, 'Bài 7: Oxide', 'https://youtube.com', 1, 'Lesson 7: Oxides'),
(46, 29, 'Bài 8: Muối', 'https://youtube.com', 2, 'Lesson 8: Salts'),
(47, 29, 'Bài 9: Khối lượng riêng', 'https://youtube.com', 3, 'Lesson 9: Density');

-- KHTN 9 Lessons
INSERT INTO `lessons` (`id`, `chapter_id`, `title_vi`, `video_url`, `order_no`, `title_en`) VALUES
(48, 30, 'Bài 1: Nhận biết một số dụng cụ, hoá chất', 'https://youtube.com', 1, 'Lesson 1: Identifying some tools, chemicals'),
(49, 30, 'Bài 2: Động năng - Thế năng', 'https://youtube.com', 2, 'Lesson 2: Kinetic - Potential Energy'),
(50, 30, 'Bài 3: Cơ năng', 'https://youtube.com', 3, 'Lesson 3: Mechanical Energy'),
(51, 31, 'Bài 4: Khúc xạ ánh sáng', 'https://youtube.com', 1, 'Lesson 4: Refraction of Light'),
(52, 31, 'Bài 5: Phản xạ toàn phần', 'https://youtube.com', 2, 'Lesson 5: Total Internal Reflection'),
(53, 31, 'Bài 6: Hiện tượng tán sắc ánh sáng', 'https://youtube.com', 3, 'Lesson 6: Light Dispersion'),
(54, 32, 'Bài 7: Định luật Ohm', 'https://youtube.com', 1, 'Lesson 7: Ohm Law'),
(55, 32, 'Bài 8: Điện trở', 'https://youtube.com', 2, 'Lesson 8: Resistors'),
(56, 32, 'Bài 9: Công suất điện', 'https://youtube.com', 3, 'Lesson 9: Electrical Power');

-- --------------------------------------------------------
-- 3. THÊM NỘI DUNG (LESSON CONTENTS) ĐỂ DEMO
-- Copy tự động tạo một dòng content chuẩn cho mỗi bài học
INSERT INTO `lesson_contents` (`lesson_id`, `content_type`, `content_value_vi`, `content_order`, `content_value_en`)
SELECT id, 'TEXT', CONCAT('<p>Nội dung lý thuyết cơ bản cho bài: <strong>', title_vi, '</strong></p>'), 1, CONCAT('<p>Basic theory content for: <strong>', IFNULL(title_en, title_vi), '</strong></p>')
FROM `lessons`
WHERE id >= 21 AND id <= 56;

-- --------------------------------------------------------
-- 4. TẠO 2 QUIZ DÀNH CHO KHTN LỚP 7
-- Quiz 10: 15 phút, nằm ở Chương 1 (Nguyên tử - ID 24)
-- Quiz 11: 30 phút, nằm ở Chương 2 (Phân tử - ID 25)
INSERT INTO `quizzes` (`id`, `chapter_id`, `quiz_type_id`, `code`, `title_vi`, `title_en`) VALUES
(10, 24, 1, 'KHTN_07_001', 'Bài Kiểm tra 15 phút - Nguyên  Tử', '15 Minute Quiz - Atoms'),
(11, 25, 2, 'KHTN_07_002', 'Bài Kiểm tra 30 phút - Phân Tử', '30 Minute Quiz - Molecules');

-- --------------------------------------------------------
-- 5. THÊM CÂU HỎI (QUESTIONS) & LỜI GIẢI THÍCH 
-- Quiz 10 (2 câu)
INSERT INTO `questions` (`id`, `quiz_id`, `explanation_vi`, `order_no`, `explanation_en`) VALUES
(10, 10, 'Nguyên tử là hạt vô cùng nhỏ và trung hòa về điện.', 1, 'An atom is extremely small and electrically neutral.'),
(11, 10, 'Hạt nhân nguyên tử gồm proton và neutron.', 2, 'Nucleus contains proton and neutron.');

-- Quiz 11 (2 câu)
INSERT INTO `questions` (`id`, `quiz_id`, `explanation_vi`, `order_no`, `explanation_en`) VALUES
(12, 11, 'Phân tử là hạt đại diện cho chất, gồm một số nguyên tử liên kết với nhau.', 1, 'Molecules depend on the combination of atoms.'),
(13, 11, 'Liên kết ion được tạo thành bởi lực hút tĩnh điện giữa các ion mang điện tích trái dấu.', 2, 'Ionic bond is made via electrostatic force.');

-- --------------------------------------------------------
-- 6. THÊM NỘI DUNG CÂU HỎI (QUESTION CONTENTS)
INSERT INTO `question_contents` (`id`, `question_id`, `content_type`, `content_value_vi`, `content_order`, `content_value_en`) VALUES
(10, 10, 'TEXT', 'Nguyên tử là gì?', 1, 'What is an atom?'),
(11, 11, 'TEXT', 'Hạt nhân nguyên tử chứa các loại hạt nào?', 1, 'What particles does the atomic nucleus contain?'),
(12, 12, 'TEXT', 'Định nghĩa phân tử?', 1, 'Definition of molecule?'),
(13, 13, 'TEXT', 'Bản chất của liên kết ion là điện tĩnh, đúng hay sai?', 1, 'Nature of ionic bond is electrostatic, true or false?');

-- --------------------------------------------------------
-- 7. THÊM CÁC SỰ LỰA CHỌN TRẢ LỜI CỦA CÂU HỎI (CHOICES)
-- Câu 10
INSERT INTO `choices` (`id`, `question_id`, `content_type`, `content_value_vi`, `is_correct`, `content_value_en`) VALUES
(40, 10, 'TEXT', 'Là hạt vô cùng nhỏ, trung hòa về điện', 1, 'Extremely small, neutral'),
(41, 10, 'TEXT', 'Mang điện tích dương', 0, 'Positive charge'),
(42, 10, 'TEXT', 'Mang điện tích âm', 0, 'Negative charge'),
(43, 10, 'TEXT', 'Có kích thước lớn', 0, 'Large size'),

-- Câu 11
(44, 11, 'TEXT', 'Proton', 0, 'Proton'),
(45, 11, 'TEXT', 'Neutron', 0, 'Neutron'),
(46, 11, 'TEXT', 'Electron', 0, 'Electron'),
(47, 11, 'TEXT', 'Proton và Neutron', 1, 'Proton and Neutron'),

-- Câu 12
(48, 12, 'TEXT', 'Hạt đại diện cho chất', 1, 'Represents substance'),
(49, 12, 'TEXT', 'Hạt không mang điện', 0, 'No charge'),
(50, 12, 'TEXT', 'Tạo ra từ phản ứng hóa học', 0, 'From chemical reaction'),
(51, 12, 'TEXT', 'Chỉ có ở chất rắn', 0, 'Only in solid'),

-- Câu 13
(52, 13, 'TEXT', 'Lực hút tĩnh điện', 1, 'Electrostatic attraction'),
(53, 13, 'TEXT', 'Dùng chung electron', 0, 'Shared electron'),
(54, 13, 'TEXT', 'Liên kết hidro', 0, 'Hydrogen bond'),
(55, 13, 'TEXT', 'Cho nhận electron', 0, 'Dative bond');

COMMIT;
