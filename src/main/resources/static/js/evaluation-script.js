// 1. Mock Data
const mockSubmissions = [
    {
        studentId: "6409610001",
        studentName: "นักศึกษา ทดสอบ",
        fileUrl: "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
    },
    {
        studentId: "6409610002",
        studentName: "สมชาย ใจดี",
        fileUrl: "https://www.tu.ac.th/uploads/file/sample.pdf"
    }
];

// 2. ฟังก์ชันวาดตาราง
function renderTable() {
    const tableBody = document.getElementById('student-table-body');
    if (!tableBody) return;

    tableBody.innerHTML = mockSubmissions.map(sub => `
        <tr>
            <td>${sub.studentId}</td>
            <td>${sub.studentName}</td>
            <td class="text-center"><span class="status-badge">รอตรวจ</span></td>
            <td class="text-right">
                <button onclick="viewStudentWork('${sub.fileUrl}')" class="btn-orange">
                    ตรวจสอบ
                </button>
            </td>
        </tr>
    `).join('');
}

// 3. ฟังก์ชันเปิดไฟล์ (หัวใจสำคัญของปุ่มสีส้ม)
function viewStudentWork(fileUrl) {
    if (!fileUrl || fileUrl === 'null' || fileUrl === '') {
        alert("ไม่พบไฟล์งาน");
        return;
    }
    window.open(fileUrl, '_blank');
}

// โหลดตารางเมื่อเปิดหน้าเว็บ
document.addEventListener('DOMContentLoaded', renderTable);