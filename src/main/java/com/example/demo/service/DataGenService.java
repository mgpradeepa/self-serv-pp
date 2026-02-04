package com.example.demo.service;

import net.datafaker.Faker;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DataGenService {

    private final Faker faker;

    public  DataGenService() {
        this.faker = new Faker();
    }

    public List<Object> generateRecords(Object template, int count) {
        return IntStream.range(0, count)
                        .mapToObj( i -> processTemplate(template))
                        .collect(Collectors.toList());
    }

    private Object processTemplate(Object input) {

        if (input instanceof Map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            ((Map<String, Object>) input).forEach((k, v) -> copy.put(k, processTemplate(v)));           
           return copy;
        }else if (input instanceof List) {
            return ((List<Object>) input).stream()
                    .map(this::processTemplate)
                    .collect(Collectors.toList());
    }
    else if(input instanceof String str && str.contains("#{")) {
        return faker.expression(str);
    }
        return input; // Replace with generated data based on the template
    }
}
    