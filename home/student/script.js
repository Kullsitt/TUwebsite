// 1. ฐานข้อมูลจำลอง (เก็บวิชาทั้งหมดไว้ที่นี่ที่เดียว)
const allCourses = [
    // --- หมวด ป.ตรี ---
    { code: "CS101", title: "การเขียนโปรแกรมคอมพิวเตอร์เบื้องต้น", instructor: "ปัณณทัต", capacity: 20, type: "bachelor", color: "bg-blue" },
    { code: "MA102", title: "แคลคูลัส 1", instructor: "ปัณณทัต", capacity: 25, type: "bachelor", color: "bg-green" },
    { code: "EL165", title: "ภาษาอังกฤษเพื่อการสื่อสาร", instructor: "ปัณณทัต", capacity: 20, type: "bachelor", color: "bg-yellow" },
    { code: "HIS21", title: "ประวัติศาสตร์ไทย", instructor: "ปัณณทัต", capacity: 20, type: "bachelor", color: "bg-darkblue" },
    
    // --- หมวด ป.โท ---
    { code: "MBA201", title: "การจัดการธุรกิจขั้นสูง", instructor: "สมชาย", capacity: 15, type: "master", color: "bg-darkblue" }
];

// โหลดวิชาที่ลงทะเบียนแล้วจาก localStorage
let enrolledCourses = JSON.parse(localStorage.getItem('enrolledCourses')) || ['CS101', 'MA102', 'EL165'];

document.addEventListener('DOMContentLoaded', () => {
    const grid = document.querySelector('.course-grid');
    const searchInput = document.querySelector('.search-box input');
    
    // ถ้าไม่มีจุดแสดงผลวิชา ไม่ต้องทำงาน
    if (!grid) return;

    // อ่านค่าว่าเราอยู่หน้าไหน (กำหนดในแท็ก <body> ของแต่ละไฟล์ HTML)
    const pageType = document.body.getAttribute('data-page') || 'all'; 

    // 2. ฟังก์ชันกรองวิชาตามหน้าที่เปิดอยู่
    function getCoursesByPageType() {
        if (pageType === 'my_courses') return allCourses.filter(c => enrolledCourses.includes(c.code));
        if (pageType === 'bachelor') return allCourses.filter(c => c.type === 'bachelor');
        if (pageType === 'master') return allCourses.filter(c => c.type === 'master');
        return allCourses; // หน้า all_courses ส่งกลับทุกวิชา
    }

    // 3. ฟังก์ชันสำหรับวาดการ์ดวิชาลงบนหน้าจอ
    function renderCourses(coursesToRender) {
        grid.innerHTML = ''; // ล้างของเก่าออกก่อน
        
        if (coursesToRender.length === 0) {
            grid.innerHTML = '<p style="grid-column: span 2; color: #777; text-align: center; padding: 20px;">ไม่พบรายวิชา</p>';
            return;
        }

        coursesToRender.forEach(c => {
            const isEnrolled = enrolledCourses.includes(c.code);
            let btnHtml = '';
            let titleOnClick = '';
            if (pageType === 'my_courses') {
                titleOnClick = `onclick="window.location.href='course_detail.html?code=${c.code}'"`;
            } else {
                btnHtml = isEnrolled 
                    ? `<button class="btn-registered" style="background-color: #999; color: white; width: 100%; border: none; padding: 10px; border-radius: 6px; cursor: default; font-family: inherit;">ลงทะเบียนแล้ว</button>` 
                    : `<button class="btn-register" data-code="${c.code}" style="background-color: #6ed062; color: white; width: 100%; border: none; padding: 10px; border-radius: 6px; cursor: pointer; font-family: inherit;">ลงทะเบียนเรียน</button>`;
            }

            const cardHTML = `
                <div class="course-card">
                    <div class="card-banner ${c.color}">
                        <span class="course-code">${c.code}</span>
                        <h3 class="course-title" ${titleOnClick}>${c.title}</h3>
                    </div>
                    <div class="card-details">
                        <p>อาจารย์: ${c.instructor}</p>
                        <p class="capacity">ลงทะเบียน: ${c.capacity} คน</p>
                        ${btnHtml}
                    </div>
                </div>
            `;
            grid.insertAdjacentHTML('beforeend', cardHTML);
        });

        // เพิ่ม event listener สำหรับปุ่มลงทะเบียน
        if (pageType !== 'my_courses') {
            document.querySelectorAll('.btn-register').forEach(btn => {
                btn.addEventListener('click', (e) => {
                    const code = e.target.getAttribute('data-code');
                    if (!enrolledCourses.includes(code)) {
                        enrolledCourses.push(code);
                        localStorage.setItem('enrolledCourses', JSON.stringify(enrolledCourses));
                        // Re-render
                        renderCourses(getCoursesByPageType());
                    }
                });
            });
        }
    }

    // ดึงวิชาตามหน้าปัจจุบันมาแสดงเป็นค่าเริ่มต้น
    let currentList = getCoursesByPageType();
    renderCourses(currentList);

    // 4. ระบบค้นหา (ค้นหาเฉพาะวิชาที่มีในหน้านั้นๆ)
    if(searchInput) {
        searchInput.addEventListener('keyup', (e) => {
            const searchTerm = e.target.value.toLowerCase();
            // กรองหาจากรหัสวิชา หรือ ชื่อวิชา
            const filtered = currentList.filter(c => 
                c.code.toLowerCase().includes(searchTerm) || 
                c.title.toLowerCase().includes(searchTerm)
            );
            renderCourses(filtered);
        });
    }
});

// สำหรับหน้ารายละเอียดวิชา
const assignments = [
    { id: 1, title: 'Assignment 1: Introduction to Programming', dueDate: '2026-05-01', description: 'เขียนโปรแกรมง่ายๆ โดยใช้ภาษา C หรือ Python' },
    { id: 2, title: 'Assignment 2: Project Proposal', dueDate: '2026-05-15', description: 'เสนอแนวคิดโปรเจคสำหรับเทอมนี้' }
];

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('course-detail')) {
        // เพิ่ม modal
        const modalHTML = `
            <div id="assignment-modal" class="modal">
                <div class="modal-content">
                    <span class="close">&times;</span>
                    <h3 id="modal-title">ส่งงาน</h3>
                    <form id="submit-form">
                        <label for="comment">ข้อความ:</label><br>
                        <textarea id="comment" name="comment" rows="4" style="width:100%; margin:10px 0;"></textarea><br>
                        <label for="file">แนบไฟล์:</label><br>
                        <input type="file" id="file" name="file" style="margin:10px 0;"><br>
                        <button type="submit" style="background:#6ed062; color:white; border:none; padding:10px; border-radius:6px;">ส่งงาน</button>
                    </form>
                </div>
            </div>
        `;
        document.body.insertAdjacentHTML('beforeend', modalHTML);

        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get('code');
        const course = allCourses.find(c => c.code === code);
        if (course) {
            let detailHTML = `
                <div class="course-card" style="max-width: 600px; margin: 0 auto;">
                    <div class="card-banner ${course.color}">
                        <span class="course-code">${course.code}</span>
                        <h3 class="course-title">${course.title}</h3>
                    </div>
                    <div class="card-details">
                        <p><strong>อาจารย์:</strong> ${course.instructor}</p>
                        <p><strong>จำนวนที่เปิดสอน:</strong> ${course.capacity} คน</p>
                        <p><strong>ประเภท:</strong> ${course.type === 'bachelor' ? 'ปริญญาตรี' : 'ปริญญาโท'}</p>
                        <p><strong>รายละเอียดเพิ่มเติม:</strong> รายละเอียดของวิชา ${course.title} จะถูกเพิ่มเติมในภายหลัง</p>
                    </div>
                </div>
            `;

            // เพิ่มส่วนการบ้าน
            let assignmentsHTML = '<h2 style="margin-top:30px;">การบ้าน</h2><div class="assignment-list">';
            assignments.forEach(a => {
                assignmentsHTML += `<div class="assignment-item" data-id="${a.id}">
                    <div class="assignment-title">${a.title}</div>
                    <div class="assignment-due">กำหนดส่ง: ${a.dueDate}</div>
                </div>`;
            });
            assignmentsHTML += '</div>';
            detailHTML += assignmentsHTML;

            document.getElementById('course-detail').innerHTML = detailHTML;

            // เพิ่ม event listeners สำหรับการบ้าน
            document.querySelectorAll('.assignment-item').forEach(item => {
                item.addEventListener('click', () => {
                    const id = item.getAttribute('data-id');
                    const assignment = assignments.find(a => a.id == id);
                    document.getElementById('modal-title').textContent = `ส่งงาน: ${assignment.title}`;
                    document.getElementById('assignment-modal').style.display = 'block';
                });
            });
        } else {
            document.getElementById('course-detail').innerHTML = '<p>ไม่พบรายวิชา</p>';
        }

        // Event สำหรับปิด modal
        document.querySelector('.close').addEventListener('click', () => {
            document.getElementById('assignment-modal').style.display = 'none';
        });

        // ปิด modal เมื่อคลิกข้างนอก
        window.addEventListener('click', (e) => {
            if (e.target == document.getElementById('assignment-modal')) {
                document.getElementById('assignment-modal').style.display = 'none';
            }
        });

        // Event สำหรับส่งฟอร์ม
        document.getElementById('submit-form').addEventListener('submit', (e) => {
            e.preventDefault();
            alert('ส่งงานเรียบร้อยแล้ว!');
            document.getElementById('assignment-modal').style.display = 'none';
            // รีเซ็ตฟอร์ม
            document.getElementById('comment').value = '';
            document.getElementById('file').value = '';
        });
    }
});