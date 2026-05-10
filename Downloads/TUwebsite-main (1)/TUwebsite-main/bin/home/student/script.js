// 1. ฐานข้อมูลจำลอง (เก็บวิชาทั้งหมดไว้ที่นี่ที่เดียว)
const allCourses = [
    // --- หมวด ป.ตรี และ ลงทะเบียนแล้ว (isMine: true) ---
    { code: "CS101", title: "การเขียนโปรแกรมคอมพิวเตอร์เบื้องต้น", instructor: "ปัณณทัต", capacity: 20, type: "bachelor", isMine: true, color: "bg-blue" },
    { code: "MA102", title: "แคลคูลัส 1", instructor: "ปัณณทัต", capacity: 25, type: "bachelor", isMine: true, color: "bg-green" },
    { code: "EL165", title: "ภาษาอังกฤษเพื่อการสื่อสาร", instructor: "ปัณณทัต", capacity: 20, type: "bachelor", isMine: true, color: "bg-yellow" },
    
    // --- หมวด ป.ตรี แต่ยังไม่ได้ลงทะเบียน ---
    { code: "HIS21", title: "ประวัติศาสตร์ไทย", instructor: "ปัณณทัต", capacity: 20, type: "bachelor", isMine: false, color: "bg-darkblue" },
    
    // --- หมวด ป.โท ---
    { code: "MBA201", title: "การจัดการธุรกิจขั้นสูง", instructor: "สมชาย", capacity: 15, type: "master", isMine: false, color: "bg-darkblue" }
];

document.addEventListener('DOMContentLoaded', () => {
    const grid = document.querySelector('.course-grid');
    const searchInput = document.querySelector('.search-box input');
    
    // ถ้าไม่มีจุดแสดงผลวิชา ไม่ต้องทำงาน
    if (!grid) return;

    // อ่านค่าว่าเราอยู่หน้าไหน (กำหนดในแท็ก <body> ของแต่ละไฟล์ HTML)
    const pageType = document.body.getAttribute('data-page') || 'all'; 

    // 2. ฟังก์ชันกรองวิชาตามหน้าที่เปิดอยู่
    function getCoursesByPageType() {
        if (pageType === 'my_courses') return allCourses.filter(c => c.isMine === true);
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
            // เช็คว่าถ้าเป็นวิชาที่ลงแล้ว (isMine = true) ให้ปุ่มเป็นสีเทา
            const btnHtml = c.isMine 
                ? `<button class="btn-registered" style="background-color: #999; color: white; width: 100%; border: none; padding: 10px; border-radius: 6px; cursor: default; font-family: inherit;">ลงทะเบียนแล้ว</button>` 
                : `<button class="btn-register" style="background-color: #6ed062; color: white; width: 100%; border: none; padding: 10px; border-radius: 6px; cursor: pointer; font-family: inherit;">ลงทะเบียนเรียน</button>`;

            const cardHTML = `
                <div class="course-card">
                    <div class="card-banner ${c.color}">
                        <span class="course-code">${c.code}</span>
                        <h3 class="course-title">${c.title}</h3>
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