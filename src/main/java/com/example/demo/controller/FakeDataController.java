package com.example.demo.controller;

import net.datafaker.Faker;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.example.demo.service.DataGenService;

import io.micrometer.core.ipc.http.HttpSender.Response;

import java.util.List;



@RestController
@RequestMapping("/api/v1/generator/")
@CrossOrigin(origins = "*")
public class FakeDataController {

    @Autowired
    private DataGenService dataGenService;

// private final DataGenService dataGenService;

//     public FakeDataController(DataGenService dataGenService) {
//         this.dataGenService = dataGenService;
//     }

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

    @PostMapping("dgenformat")
    public ResponseEntity<Object> generateDataAvro(@RequestBody String template, @RequestParam int count, @RequestParam String schemaPath) {
        if( template == null || template.isEmpty() || count <= 0 || schemaPath == null || schemaPath.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(dataGenService.generateSchemabasedData(template, count, schemaPath));
    }
     
    
}