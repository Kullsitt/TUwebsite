package com.courses.tu.central.portal.dto;

import lombok.Data;

@Data
public class TULoginResponse {
    private boolean status;
    private String tu_status;       // "student" หรือ "employee"
    private String displayname_th;
    private String displayname_en;
    private String username;
    private String department;
    private String faculty;
    private String email;
}