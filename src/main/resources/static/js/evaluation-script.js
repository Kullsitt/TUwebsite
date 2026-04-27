/**
 * จัดการการแสดงผลหน้าประเมิน (Evaluation Interaction)
 */

document.addEventListener('DOMContentLoaded', () => {
    // เริ่มทำงาน Lucide Icons
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
});

/**
 * เปิดฟอร์มให้คะแนน (Evaluation Form)
 * @param {number} id - รหัสการส่งงาน
 * @param {string} name - ชื่อนักศึกษา
 * @param {string} studentId - รหัสนักศึกษา
 */
function openEvaluation(id, name, studentId) {
    const area = document.getElementById('evaluationArea');
    const inputId = document.getElementById('submissionId');
    const targetName = document.getElementById('targetName');
    
    // ตั้งค่าข้อมูล
    inputId.value = id;
    targetName.textContent = `${name} (${studentId})`;

    // แสดงพื้นที่การประเมิน
    area.classList.remove('hidden');
    
    // เลื่อนหน้าจอไปที่ฟอร์มแบบนุ่มนวล
    area.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

/**
 * ปิดฟอร์มประเมิน
 */
function closeEvaluation() {
    const area = document.getElementById('evaluationArea');
    area.classList.add('hidden');
}