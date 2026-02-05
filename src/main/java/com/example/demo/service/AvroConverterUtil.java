package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class AvroConverterUtil {



     private static final ObjectMapper mapper = new ObjectMapper();

    public byte[] rawConvert(String json, String recordName) throws Exception {
        JsonNode rootNode = mapper.readTree(json);
        
        // 1. Infer Schema
        Schema schema = inferSchema(rootNode, recordName);
        
        // 2. Map JSON to GenericRecord
        GenericRecord record = mapToRecord(rootNode, schema);
        
        // 3. Serialize to Binary
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        writer.write(record, encoder);
        encoder.flush();
        return out.toByteArray();
    }

    public  Schema inferSchema(JsonNode node, String name) {
        if (node.isObject()) {
            SchemaBuilder.FieldAssembler<Schema> assembler = SchemaBuilder.record(name).fields();
            Iterator<String> fieldNames = node.fieldNames();
            while (

fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                assembler.name(fieldName).type(inferSchema(node.get(fieldName), fieldName)).noDefault();
            }
            return assembler.endRecord();
        } else if (node.isArray()) {
            // Infers array type from the first element, defaults to String if empty
            Schema elementSchema = node.size() > 0 ? inferSchema(node.get(0), name + "Item") : Schema.create(Schema.Type.STRING);
            return Schema.createArray(elementSchema);
        } else if (node.isInt()) return Schema.create(Schema.Type.INT);
        else if (node.isLong()) return Schema.create(Schema.Type.LONG);
        else if (node.isDouble()) return Schema.create(Schema.Type.DOUBLE);
        else if (node.isBoolean()) return Schema.create(Schema.Type.BOOLEAN);
        else return Schema.create(Schema.Type.STRING);
    }

    private GenericRecord mapToRecord(JsonNode node, Schema schema) {
        GenericRecord record = new GenericData.Record(schema);
        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            JsonNode value = node.get(name);
            if (value.isObject()) {
                record.put(name, mapToRecord(value, schema.getField(name).schema()));
            } else if (value.isArray()) {
                List<Object> list = new ArrayList<>();
                value.forEach(item -> list.add(item.asText())); // Simplify or recurse for objects
                record.put(name, list);
            } else {
                record.put(name, value.asText()); // Avro is flexible with string conversion
            }
        }
        return record;
    }
}
