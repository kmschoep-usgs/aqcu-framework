package gov.usgs.aqcu.serializer;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.Test;

import com.google.gson.JsonPrimitive;

public class OffsetDateTimeGsonSerializerTest {

	@Test
	public void offsetDateTimeSerializationTest() {
		OffsetDateTimeGsonSerializer serializer = new OffsetDateTimeGsonSerializer();
		LocalDateTime ldt = LocalDateTime.of(2018, 4, 23, 9, 8, 12, 3000);
		assertEquals(new JsonPrimitive("2018-04-23T15:08:12.0000030Z"),
				serializer.serialize(OffsetDateTime.of(ldt, ZoneOffset.of("-6")), null, null));
		assertEquals(new JsonPrimitive("2018-04-23T09:08:12.0000030Z"),
				serializer.serialize(OffsetDateTime.of(ldt, ZoneOffset.UTC), null, null));
	}
}
