package com.coursestu.central_portal.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.io.IOException;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    // 👉 เก็บใน uploads/ ระดับเดียวกับ src
    private final String uploadDir = "uploads/";

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 🔥 กันชื่อไฟล์ null
            String filename = file.getOriginalFilename();
            if (filename == null || filename.isEmpty()) {
                return "Upload failed: filename is empty";
            }

            // 🔥 สร้าง path
            Path path = Paths.get(uploadDir).resolve(filename);

            // 🔥 สร้าง folder ถ้ายังไม่มี
            Files.createDirectories(path.getParent());

            // 🔥 copy ไฟล์
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // 🔥 ส่ง URL กลับ
            return "Upload success: http://localhost:8080/uploads/" + filename;

        } catch (IOException e) {
            return "Upload failed: " + e.getMessage();
        }
    }
}