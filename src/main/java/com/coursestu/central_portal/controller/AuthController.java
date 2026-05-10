package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.dto.TULoginResponse;
import com.coursestu.central_portal.service.TUAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private TUAuthService tuAuthService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 1. Mock teacher สำหรับทดสอบ
        if ("teacher".equals(username) && "1234".equals(password)) {
            TULoginResponse mockTeacher = new TULoginResponse();
            mockTeacher.setStatus(true);
            mockTeacher.setTuStatus("teacher"); 
            mockTeacher.setDisplaynameTh("อาจารย์ทดสอบ"); 
            mockTeacher.setDisplaynameEn("Test Teacher"); 
            mockTeacher.setUsername("t12345");
            mockTeacher.setEmail("teacher@tu.ac.th"); // ลบตัว 'ฟ' ออกให้แล้วครับ
            mockTeacher.setFaculty("คณะวิทยาศาสตร์และเทคโนโลยี");
            session.setAttribute("user", mockTeacher);
            session.setAttribute("role", "teacher");
            return "redirect:/dashboard/teacher";
        }

        // 2. Mock student สำหรับทดสอบ (พิมพ์รหัสนักศึกษา 10 หลัก รหัสผ่านอะไรก็ได้)
        if (username != null && username.matches("\\d{10}")) {
            TULoginResponse mockStudent = new TULoginResponse();
            mockStudent.setStatus(true);
            mockStudent.setTuStatus("student");
            mockStudent.setDisplaynameTh("นักศึกษา ทดสอบ");
            mockStudent.setUsername(username);
            session.setAttribute("user", mockStudent);
            session.setAttribute("role", "student");
            return "redirect:/home/student";
        }

        // 3. ส่วนเชื่อม API จริง
        try {
            TULoginResponse tuResponse = tuAuthService.authenticate(username, password);

            if (tuResponse != null && tuResponse.isStatus()) {
                session.setAttribute("user", tuResponse);
                
                session.setAttribute("role", tuResponse.getTuStatus());

                if (tuResponse.getUsername() != null && tuResponse.getUsername().matches("\\d{10}")) {
                    return "redirect:/home/student";
                } else {
                    return "redirect:/dashboard/teacher";
                }
            }

        } catch (Exception e) {
            System.out.println("=== Login Error ===");
            System.out.println(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Username หรือ Password ไม่ถูกต้อง");
        }

        return "redirect:/login?error";
    }

    public TUAuthService getTuAuthService() {
		return tuAuthService;
	}

	public void setTuAuthService(TUAuthService tuAuthService) {
		this.tuAuthService = tuAuthService;
	}

	@GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}