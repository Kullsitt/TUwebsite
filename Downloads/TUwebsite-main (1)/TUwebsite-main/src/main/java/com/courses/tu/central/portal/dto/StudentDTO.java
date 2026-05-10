package com.courses.tu.central.portal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // ป้องกัน Error ถ้า API ส่งค่าที่เราไม่ได้ประกาศไว้มาให้
public class StudentDTO {
    private String displayname_th;
    private String displayname_en;
    private String email;
    private String faculty;
    private String department;
}