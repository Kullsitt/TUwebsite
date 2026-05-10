package com.coursestu.central_portal.dto;

public class TULoginResponse {
    private boolean status;
    private String tuStatus;
    private String displaynameTh;
    private String displaynameEn;
    private String username;
    private String department;
    private String faculty;
    private String email;

    // Getter & Setter (สร้างมาเพื่อแก้ปัญหาโดยเฉพาะ)
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public String getTuStatus() { return tuStatus; }
    public void setTuStatus(String tuStatus) { this.tuStatus = tuStatus; }

    public String getDisplaynameTh() { return displaynameTh; }
    public void setDisplaynameTh(String displaynameTh) { this.displaynameTh = displaynameTh; }

    public String getDisplaynameEn() { return displaynameEn; }
    public void setDisplaynameEn(String displaynameEn) { this.displaynameEn = displaynameEn; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }
}