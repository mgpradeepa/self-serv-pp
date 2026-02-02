package com.example.demo.controller;

import net.datafaker.Faker;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/generator")
public class FakeDataController {
private final DataGenService dataGenService;

    public FakeDataController(DataGenService dataGenService) {
        this.dataGenService = dataGenService;
    }

    @PostMapping("/datagen")
    public List<Object> generateData(@RequestBody Object template, @RequestParam int count) {
        return dataGenService.generateRecords(template, count);
    }
     
    
}