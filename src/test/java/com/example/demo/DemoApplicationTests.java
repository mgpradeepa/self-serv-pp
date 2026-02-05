package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.service.AvroConverterUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DecoderFactory;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void validateAvroConversion()  throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInput = "{\n" + //
						" \"serverTime\": \"705725396969\",\n" + //
						" \"trace_id\": \"trace_300f7b11-3f83-4064-a919-383b796e3408\" ,\n" + //
						" \"device_id\": \"dev_5eb85b98-429a-4638-a4df-51ff4faaeec0\",\n" + //
						" \"subscriber_id\": \"sub_c8c24283-6247-4e25-b516-282f4013b89f\",\n" + //
						" \"network_id\": \"netw_6f5475ef-eefa-46b0-bdb0-72f2c0224087\",\n" + //
						" \"make\": \"Philips\",\n" + //
						" \"model\": \"BlackBerry\",\n" + //
						" \"tenant_id\": \"43\",\n" + //
						" \"action_name\":\"SHUTDOWN\",\n" + //
						" \"transaction_type\": \"ONLINE\",\n" + //
						" \"auth_type\": \"SSO\",\n" + //
						" \"fault_reason\": \"Device Not Responding\",\n" + //
						" \"action_status\": \"PENDING\",\n" + //
						"  \"message\": \"ello hoge\"\n" + //
						"}";
		AvroConverterUtil converter = new AvroConverterUtil();
		byte[] avroData = converter.rawConvert(jsonInput,"Test");
		assertNotNull(avroData);
		assertTrue(avroData.length > 0);

		JsonNode rootNode = mapper.readTree(jsonInput);
        Schema schema = converter.inferSchema( rootNode, "Test");

        // 4. Decode the binary back to an Avro GenericRecord
        GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
        GenericRecord decodedRecord = reader.read(null, 
            DecoderFactory.get().binaryDecoder(new ByteArrayInputStream(avroData), null));

        // 5. Assertions: Compare decoded Avro values with original JSON values
        assertThat(decodedRecord.get("trace_id").toString()).isEqualTo("trace_300f7b11-3f83-4064-a919-383b796e3408");
        assertThat(decodedRecord.get("make").toString()).isEqualTo("Philips");
        assertThat(decodedRecord.get("action_name").toString()).isEqualTo("SHUTDOWN");
	}


}
