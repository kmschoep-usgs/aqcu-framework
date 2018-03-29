package gov.usgs.aqcu.serializer;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDate;

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
		assertEquals(gson.toJson(reportEndInstant).compareTo("\"2018-03-16T23:59:59.9999999Z\""), 0);
		assertEquals(gson.toJson(reportStartInstant).compareTo("\"2018-02-16T00:00:00.0000000Z\""), 0);
	}

	@Test
	public void instantDeserializationTest() {
		assertEquals(reportEndInstant, gson.fromJson("\"2018-03-16T23:59:59.9999999Z\"", Instant.class));
		assertEquals(reportStartInstant, gson.fromJson("\"2018-02-16T00:00:00.00Z\"", Instant.class));
	}

	@Test 
	public void localDateSerializationTest() {
		assertEquals(0, gson.toJson(reportEndDate).compareTo("\"2018-03-16\""));
		assertEquals(0, gson.toJson(reportStartDate).compareTo("\"2018-02-16\""));
	}

	@Test 
	public void lowerCamelCaseTest() {
		SerializationTestClass testClass = new SerializationTestClass();
		testClass.setUpperCamelCaseString("test");
		assertEquals(0, gson.toJson(testClass).compareTo("{\"upperCamelCaseString\":\"test\"}"));
	}
}
