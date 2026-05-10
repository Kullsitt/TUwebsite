package com.coursestu.central_portal.controller;

import com.coursestu.central_portal.service.TUService;
import com.coursestu.central_portal.dto.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tu")
@CrossOrigin(origins = "*") 
public class TUController {

    @Autowired
    private TUService tuService;

    @GetMapping("/student")
    public StudentDTO getStudent(@RequestParam String id) {
        return tuService.getStudentInfo(id);
    }
}