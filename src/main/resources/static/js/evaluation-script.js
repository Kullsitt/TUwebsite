function viewStudentWork(fileUrl) {
    if (!fileUrl || fileUrl === 'null' || fileUrl === '') {
        alert("ไม่พบไฟล์งานที่นักศึกษาส่ง");
        return;
    }
    const targetUrl = (fileUrl.startsWith('http')) ? fileUrl : "/uploads/" + fileUrl;
    window.open(targetUrl, '_blank');
}

async function saveScore(studentId) {
    // ✅ ดึง assignmentId จาก URL เช่น /evaluation/3 -> 3
    const urlParts = window.location.pathname.split('/');
    const assignmentId = urlParts[urlParts.length - 1];

    const input = document.getElementById('score-' + studentId);
    const score = input.value;

    if (score === '' || score === null) {
        alert('กรุณากรอกคะแนนก่อนบันทึก');
        return;
    }
    if (score < 0 || score > 100) {
        alert('คะแนนต้องอยู่ระหว่าง 0 - 100');
        return;
    }

    try {
        const response = await fetch('/api/evaluation/save', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                assignmentId: parseInt(assignmentId),
                studentId: studentId,
                score: parseFloat(score)
            })
        });

        const result = await response.json();

        if (result.success) {
            // ✅ อัปเดตสถานะทันทีโดยไม่ต้อง reload
            const statusEl = document.getElementById('status-' + studentId);
            statusEl.textContent = 'ตรวจแล้ว';
            statusEl.className = 'status-badge status-success';
            alert('บันทึกคะแนนสำเร็จ');
        } else {
            alert(result.message);
        }
    } catch (err) {
        console.error(err);
        alert('ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    console.log("Evaluation System Ready");
});