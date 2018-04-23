package gov.usgs.aqcu.serializer;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

import com.google.gson.JsonPrimitive;

public class LocalDateGsonSerializerTest {

	LocalDate reportEndDate = LocalDate.of(2018, 03, 16);
	LocalDate reportStartDate = LocalDate.of(2018, 02, 16);

	@Test
	public void localDateSerializationTest() {
		LocalDateGsonSerializer serializer = new LocalDateGsonSerializer();
		assertEquals(new JsonPrimitive("2018-03-16"), serializer.serialize(reportEndDate, null, null));
		assertEquals(new JsonPrimitive("2018-02-16"), serializer.serialize(reportStartDate, null, null));
	}

}
