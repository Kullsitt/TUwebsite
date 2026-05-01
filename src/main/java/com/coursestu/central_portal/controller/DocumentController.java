package com.coursestu.central_portal.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final String uploadDir = "uploads/";

    
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // ✅ เปลี่ยนชื่อไฟล์ให้ไม่มี space
            String safeFileName = file.getOriginalFilename().replaceAll("\\s+", "_");
            
            Path path = Paths.get(uploadDir + safeFileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return "Upload success: " + safeFileName;
        } catch (Exception e) {
            return "Upload failed";
        }
    }

    
    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> viewFile(@PathVariable String filename) throws IOException {
        // decode %20 กลับเป็น space
        String decodedFilename = java.net.URLDecoder.decode(filename, "UTF-8");
        Path path = Paths.get(uploadDir + decodedFilename);
        
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                .body(resource);
    }
    
   
    
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws IOException {
        String decodedFilename = java.net.URLDecoder.decode(filename, "UTF-8");
        Path path = Paths.get(uploadDir + decodedFilename);
        
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + decodedFilename + "\"")
                .body(resource);
    }
}