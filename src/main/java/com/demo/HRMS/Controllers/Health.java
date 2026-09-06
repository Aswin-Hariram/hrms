package com.demo.HRMS.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class Health {

    @GetMapping("/check")
    public Map<String,String> health() {
        return Map.of(
                "Status","Active",
                "Message","Hello from HRMS"        );
    }
}