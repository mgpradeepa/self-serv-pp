package com.example.demo.service;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DataGenService {
    private Logger logger = LoggerFactory.getLogger(DataGenService.class);

    private final Faker faker;

    public DataGenService() {
        this.faker = new Faker();
    }

    public List<Object> generateRecords(Object template, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> processTemplate(template))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Object processTemplate(Object input) {
        logger.debug(input.toString());

        if (input instanceof Map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            ((Map<String, Object>) input).forEach((k, v) -> copy.put(k, processTemplate(v)));
            return copy;
        } else if (input instanceof List) {
            return ((List<Object>) input).stream()
                    .map(this::processTemplate)
                    .collect(Collectors.toList());
        } else if (input instanceof String str && str.contains("#{")) {
            String result = faker.expression(str);
            if (result.matches("\\d+")) {
                try {
                    return Long.parseLong(result);
                } catch (NumberFormatException e) {
                    return result; // Return as string if it's too large for Long
                }
            }
            logger.info("Generated value {}", result);
            return result;
        }
        return input;
    }

    public List<Object> generateSchemabasedData(Object template, int count, String schemaPath) {

        List<Object> records = generateRecords(template, count); // string
        List<Object> dataresult = new ArrayList<>();
        for (Object jsonString : records) {
            try {
                dataresult.add(new AvroConverterUtil().rawConvert(jsonString.toString(), "record"));
            } catch (Exception e) {
                logger.error("Error converting to Avro: {}", e.getMessage());
                e.printStackTrace();
            }

        }

        logger.info("data result {}", dataresult.getFirst());

        return dataresult;

    }

    private Object convertToProtobuf(List<Object> records, Class<?> protoClass) {
        //TODO:  Implement protobuf for future usage
        throw new UnsupportedOperationException("Protobuf conversion not yet implemented");
    }
}
