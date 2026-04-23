-- ==========================================

-- 1. สร้างข้อมูลวิชาจำลอง (Course)

-- ==========================================

INSERT IGNORE INTO course (course_id, course_name, teacher_name, capacity)

VALUES ('CS232', 'Backend Development with Spring Boot', 'อาจารย์สมชาย ใจดี', 40);



INSERT IGNORE INTO course (course_id, course_name, teacher_name, capacity)

VALUES ('CS216', 'Data Structures and Algorithms', 'อาจารย์สมหญิง รักเรียน', 30);



INSERT IGNORE INTO course (course_id, course_name, teacher_name, capacity)

VALUES ('CS101', 'Introduction to Programming', 'อาจารย์สมหมาย ยกยอ', 35);



-- ==========================================

-- 2. สร้างข้อมูลเนื้อหาจำลอง (Assignment) ให้ CS232 และ CS216

-- ==========================================

-- วิชา CS232

INSERT IGNORE INTO assignment (id, title, description, type, course_id)

VALUES (1, 'ประกาศสำคัญก่อนเริ่มเรียน', 'ให้นักศึกษาทุกคนเข้ากลุ่ม Line ของวิชา CS232 ด้วยครับ', 'announcement', 'CS232');



INSERT IGNORE INTO assignment (id, title, description, type, course_id)

VALUES (2, 'สไลด์บทที่ 1: Spring Boot Architecture', 'โหลดเอกสารก่อนเข้าเรียนนะครับ', 'material', 'CS232');



INSERT IGNORE INTO assignment (id, title, description, type, course_id)

VALUES (3, 'การบ้านครั้งที่ 1: สร้าง API ง่ายๆ', 'ส่งผ่านระบบก่อนวันอาทิตย์นี้เวลา 23:59 น.', 'homework', 'CS232');



-- วิชา CS216

INSERT IGNORE INTO assignment (id, title, description, type, course_id)

VALUES (4, 'Quiz 1: Binary Search Trees', 'ควิซเก็บคะแนน 5% ทำในระบบได้เลย', 'quizzes', 'CS216');



-- ==========================================

-- 3. สร้างข้อมูลการลงทะเบียนจำลอง (Enrollment)

-- สมมติให้นักศึกษารหัส "std001" (หรือรหัสที่คุณใช้ทดสอบ) ลงเรียนไว้ 2 วิชา

-- ==========================================

INSERT IGNORE INTO enrollments (id, student_id, course_id)

VALUES (1, 'std001', 'CS232');



INSERT IGNORE INTO enrollments (id, student_id, course_id)

VALUES (2, 'std001', 'CS216');