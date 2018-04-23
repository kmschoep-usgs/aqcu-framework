package gov.usgs.aqcu.serializer;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.google.gson.Gson;

import gov.usgs.aqcu.util.AqcuGsonBuilderFactory;

public class GsonSerializationTest { 
	Instant reportEndInstant = Instant.parse("2018-03-16T23:59:59.9999999Z");
	Instant reportStartInstant = Instant.parse("2018-02-16T00:00:00.00Z");
	LocalDate reportEndDate = LocalDate.of(2018, 03, 16);
	LocalDate reportStartDate = LocalDate.of(2018, 02, 16);
	Gson gson;

	private class SerializationTestClass {
		private String UpperCamelCaseString;
		public String getUpperCamelCaseString() {
			return UpperCamelCaseString;
		}
		public void setUpperCamelCaseString(String v) {
			UpperCamelCaseString = v;
		}
	}

	@Before
	public void setup() {
		gson = AqcuGsonBuilderFactory.getConfiguredGsonBuilder().create();
	}

	@Test
	public void instantSerializationTest() {
		assertEquals("\"2018-03-16T23:59:59.9999999Z\"", gson.toJson(reportEndInstant));
		assertEquals("\"2018-02-16T00:00:00.0000000Z\"", gson.toJson(reportStartInstant));
	}

	@Test
	public void instantDeserializationTest() {
		assertEquals(reportEndInstant, gson.fromJson("\"2018-03-16T23:59:59.9999999Z\"", Instant.class));
		assertEquals(reportStartInstant, gson.fromJson("\"2018-02-16T00:00:00.00Z\"", Instant.class));
	}

	@Test
	public void localDateSerializationTest() {
		assertEquals("\"2018-03-16\"", gson.toJson(reportEndDate));
		assertEquals("\"2018-02-16\"", gson.toJson(reportStartDate));
	}

	@Test
	public void lowerCamelCaseTest() {
		SerializationTestClass testClass = new SerializationTestClass();
		testClass.setUpperCamelCaseString("test");
		assertEquals("{\"upperCamelCaseString\":\"test\"}", gson.toJson(testClass));
	}

	@Test
	public void offsetDateTimeSerializationTest() {
		LocalDateTime ldt = LocalDateTime.of(2018, 4, 23, 9, 8, 12, 3000);
		assertEquals("\"2018-04-23T15:08:12.0000030Z\"", gson.toJson(OffsetDateTime.of(ldt, ZoneOffset.of("-6"))));
		assertEquals("\"2018-04-23T09:08:12.0000030Z\"", gson.toJson(OffsetDateTime.of(ldt, ZoneOffset.UTC)));
	}
}
