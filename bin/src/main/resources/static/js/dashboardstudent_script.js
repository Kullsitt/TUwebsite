// dashboardstudent_script.js

const urlParams = new URLSearchParams(window.location.search);
const currentCourseId = urlParams.get('id') || 'CS232'; 

const courseNamesMap = {
    'CS271': 'CS265/CS271 ARTIFICIAL INTELLIGENCE FUNDAMENTALS',
    'CS232': 'CS232 SOFTWARE DEVELOPMENT'
};

function updateCourseHeaderTitles() {
    const headerTitleElement = document.getElementById('header-title');
    const breadcrumbTitleElement = document.getElementById('breadcrumb-title');
    
    // ต้องมี Backtick ครอบ
    const fullNameText = courseNamesMap[currentCourseId] || `วิชา ${currentCourseId}`;
    
    if (headerTitleElement) headerTitleElement.innerText = fullNameText;
    if (breadcrumbTitleElement) breadcrumbTitleElement.innerText = `วิชา ${fullNameText}`;
}

async function loadDashboardData() {
    try {
        updateCourseHeaderTitles();

        // ต้องมี Backtick ครอบ URL
        const response = await fetch(`/api/dashboard/${currentCourseId}`);
        if (!response.ok) throw new Error('ไม่สามารถโหลดข้อมูลจาก Server ได้');
        
        const data = await response.json();
        // ต้องมี Backtick ครอบข้อความ
        console.log(`ข้อมูลวิชา ${currentCourseId}:`, data);

        // --- กรองข้อมูลตามหมวดหมู่ ---
        const announcements = data.filter(item => item.itemType === 'ANNOUNCEMENT');
        const materials = data.filter(item => item.itemType === 'MATERIAL');
        const homeworks = data.filter(item => item.itemType === 'HOMEWORK');
        const quizzes = data.filter(item => item.itemType === 'QUIZ');
        
        // --- ส่งไปแสดงผลตามจุดต่างๆ ---
        renderList(announcements, 'announcement-list', 'fa-regular fa-message');
        renderList(materials, 'material-list', 'fa-solid fa-file-arrow-down');
        renderList(homeworks, 'homework-list', 'fa-regular fa-square-check');
        renderList(quizzes, 'quiz-list', 'fa-solid fa-link');

    } catch (error) {
        console.error("Fetch Data Error:", error);
        const container = document.getElementById('announcement-list');
        if (container) {
            // ต้องมี Backtick ครอบ HTML
            container.innerHTML = `<div class="list-item" style="color:red;">เกิดข้อผิดพลาดในการโหลดข้อมูล</div>`;
        }
    }
}

function renderList(items, containerId, iconClass) {
    const container = document.getElementById(containerId);
    if (!container) {
        // ต้องมี Backtick ครอบ
        console.warn(`ไม่พบ Container ID: ${containerId} ในหน้า HTML`);
        return;
    }

    container.innerHTML = ''; 

    if (items.length === 0) {
        // ต้องมี Backtick ครอบ HTML
        container.innerHTML = `<div class="list-item" style="color:#999;">ไม่มีข้อมูลในหมวดหมู่นี้</div>`;
        return;
    }

    items.forEach(item => {
        const itemDiv = document.createElement('div');
        itemDiv.className = 'list-item';
        
        // โครงสร้าง HTML ย่อย ต้องใช้ Backtick ครอบตั้งแต่ต้นจนจบ
        itemDiv.innerHTML = `
            <i class="${iconClass} icon-grey"></i>
            <div class="item-text">
                <div class="text-bold">${item.title}</div>
                <div class="text-normal">${item.detail || ''}</div> 
            </div>
        `;
        container.appendChild(itemDiv);
    });
}

document.addEventListener('DOMContentLoaded', loadDashboardData);