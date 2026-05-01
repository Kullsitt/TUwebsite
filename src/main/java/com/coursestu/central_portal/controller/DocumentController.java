package com.coursestu.central_portal.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.io.IOException;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final String uploadDir = "uploads/";

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 🔥 กัน null
            String filename = file.getOriginalFilename();
            if (filename == null || filename.isEmpty()) {
                return "Upload failed: filename is empty";
            }

            // 🔥 ลบ space
            String safeFileName = filename.replaceAll("\\s+", "_");

            Path path = Paths.get(uploadDir).resolve(safeFileName);

            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return "Upload success: http://localhost:8080/uploads/" + safeFileName;

        } catch (IOException e) {
            return "Upload failed: " + e.getMessage();
        }
    }

    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> viewFile(@PathVariable String filename) throws IOException {

        String decodedFilename = java.net.URLDecoder.decode(filename, "UTF-8");
        Path path = Paths.get(uploadDir).resolve(decodedFilename);

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
        Path path = Paths.get(uploadDir).resolve(decodedFilename);

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