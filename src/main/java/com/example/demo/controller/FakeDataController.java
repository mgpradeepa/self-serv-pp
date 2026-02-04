package com.example.demo.controller;

import net.datafaker.Faker;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.demo.service.DataGenService;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;



@RestController
@RequestMapping("/api/v1/generator/")
@CrossOrigin(origins = "*")
public class FakeDataController {

private final DataGenService dataGenService;

    public FakeDataController(DataGenService dataGenService) {
        this.dataGenService = dataGenService;
    }

    // @PostMapping("/datagen")
    // public ResponseEntity<List<String>> generateData(@RequestBody String template, @RequestParam int count) {
    //     if( template == null || template.isEmpty() || count <= 0) {
    //         return ResponseEntity.badRequest().build();
    //     }
    //     return ResponseEntity.ok(dataGenService.generateRecords(template, count));
    // }

    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is up and running");
    }

    @PostMapping("datagenTemplate")
    public ResponseEntity<Object> generateData(@RequestBody String template, @RequestParam int count) {
        if( template == null || template.isEmpty() || count <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(dataGenService.generateRecords(template, count));
    }
     
    
}