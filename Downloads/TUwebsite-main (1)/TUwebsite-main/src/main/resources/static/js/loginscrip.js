// กำหนด email และ password ที่ถูกต้อง (สมมติ)
const validUsername = "6709650474";
const validPassword = "123456";

function login(event) {
    event.preventDefault(); // กันรีเฟรชหน้า

    const email = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    // ตรวจสอบ username และ password
    if (email === validUsername && password === validPassword) {
        window.location.href = "home.html"; // ไปหน้าอื่น
    } else {
        alert("Username หรือ password ไม่ถูกต้อง");
    }
}