// 1. ข้อมูล Mock นักศึกษา (ปรับแก้/เพิ่มลด ตรงนี้ได้เลย)
const studentsMockData = {
    "3": [
        { id: "6409610001", name: "นายสมชาย สายลม", fileUrl: "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf", score: "" },
        { id: "6409610002", name: "นางสาวสมหญิง รักเรียน", fileUrl: "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf", score: "" }
    ],
};

// 2. ฟังก์ชันวาดตาราง
function renderStudentTable() {
    const assignmentId = window.location.pathname.split('/').pop();
    const students = studentsMockData[assignmentId] || [];
    const tableBody = document.getElementById('student-table-body');
    
    if (!tableBody) return;

    if (students.length > 0) {
        tableBody.innerHTML = students.map(s => `
            <tr>
                <td>${s.id}</td>
                <td>${s.name}</td>
                <td class="text-center"><span class="status-badge">รอตรวจ</span></td>
                <td class="text-center">
                    <input type="number" class="score-input" value="${s.score}" 
                           placeholder="..." onchange="updateScore('${s.id}', this.value)">
                </td>
				<td class="text-right">
				                <button onclick="viewStudentWork('${s.fileUrl}')" class="btn-orange">
				                    ตรวจสอบ
				                </button>
				            </td>
            </tr>
        `).join('');
    } else {
        tableBody.innerHTML = `<tr><td colspan="5" style="text-align: center; padding: 20px; color: #888;">ยังไม่มีนักศึกษาส่งงานในขณะนี้</td></tr>`;
    }
}

// 3. ฟังก์ชันเปิดดูงาน (แก้ไขให้รองรับทั้ง Link นอก และไฟล์ในเครื่อง)
function viewStudentWork(fileName) {
    if (fileName && fileName !== 'undefined' && fileName !== 'null') {
        
        let fileURL = "";

        // เช็คว่าถ้า fileName ขึ้นต้นด้วย http หรือ https (เป็น Link จากข้างนอก)
        if (fileName.startsWith('http://') || fileName.startsWith('https://')) {
            fileURL = fileName; // ใช้ URL นั้นตรงๆ เลย
        } else {
            // ถ้าเป็นแค่ชื่อไฟล์ (เช่น work1.pdf) ให้ชี้ไปที่ Path ในโปรเจกต์
            // ปรับ Path ตามที่คุณเก็บไฟล์จริง (เช่น /uploads/ หรือ /)
            fileURL = "/" + fileName; 
        }
        
        console.log("กำลังเปิดไฟล์จาก:", fileURL);
        window.open(fileURL, '_blank');
    } else {
        alert("ไม่พบไฟล์งานของนักศึกษาคนนี้");
    }
}

// 4. ฟังก์ชันบันทึกคะแนน (Fetch ไปหา Controller ที่เราทำไว้)
async function updateScore(studentId, newScore) {
    const assignmentId = window.location.pathname.split('/').pop();
    
    try {
        const response = await fetch('/api/evaluation/save', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                assignmentId: assignmentId,
                studentId: studentId,
                score: newScore
            })
        });

        const result = await response.json();
        if (result.success) {
            console.log("บันทึกสำเร็จ:", result.message);
        } else {
            alert("บันทึกไม่สำเร็จ: " + result.message);
        }
    } catch (error) {
        console.error("Error saving score:", error);
    }
}

// สั่งให้ทำงานเมื่อโหลดหน้าเสร็จ
document.addEventListener('DOMContentLoaded', renderStudentTable);