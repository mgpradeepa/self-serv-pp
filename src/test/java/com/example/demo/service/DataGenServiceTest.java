package com.example.demo.service;

import com.google.protobuf.Struct;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataGenServiceTest {

    @Test
    void convertToProtobufConvertsValidStringAndMapRecords() throws Exception {
        DataGenService service = new DataGenService();
        Method convertMethod = DataGenService.class.getDeclaredMethod("convertToProtobuf", List.class, Class.class);
        convertMethod.setAccessible(true);

        String jsonRecord = "{\"name\":\"alice\",\"age\":30}";
        Map<String, Object> mapRecord = new LinkedHashMap<>();
        mapRecord.put("status", "ok");
        mapRecord.put("score", 99);

        @SuppressWarnings("unchecked")
        List<Object> converted = (List<Object>) convertMethod.invoke(
                service,
                List.of(jsonRecord, mapRecord),
                null
        );

        assertEquals(2, converted.size());

        Struct first = Struct.parseFrom((byte[]) converted.get(0));
        Struct second = Struct.parseFrom((byte[]) converted.get(1));

        assertEquals("alice", first.getFieldsOrThrow("name").getStringValue());
        assertEquals(30d, first.getFieldsOrThrow("age").getNumberValue());
        assertEquals("ok", second.getFieldsOrThrow("status").getStringValue());
        assertEquals(99d, second.getFieldsOrThrow("score").getNumberValue());
    }

    @Test
    void convertToProtobufSkipsInvalidRecordsAndReturnsOnlyValidBytes() throws Exception {
        DataGenService service = new DataGenService();
        Method convertMethod = DataGenService.class.getDeclaredMethod("convertToProtobuf", List.class, Class.class);
        convertMethod.setAccessible(true);

        String invalidJson = "{bad_json}";
        String validJson = "{\"event\":\"created\"}";

        @SuppressWarnings("unchecked")
        List<Object> converted = (List<Object>) convertMethod.invoke(
                service,
                List.of(invalidJson, validJson),
                null
        );

        assertEquals(1, converted.size());
        assertTrue(converted.getFirst() instanceof byte[]);

        Struct parsed = Struct.parseFrom((byte[]) converted.getFirst());
        assertEquals("created", parsed.getFieldsOrThrow("event").getStringValue());
    }

    @Test
    void generateAndPushToDestinationHandlesProtobufPath() {
        DataGenService service = new DataGenService();
        Map<String, Object> template = Map.of("id", "#{number.numberBetween '1','100'}", "action", "PING");

        boolean result = service.generateAndPushToDestination(template, 2, "protobuf", "localhost|topic-a");

        assertTrue(result);
    }
}
