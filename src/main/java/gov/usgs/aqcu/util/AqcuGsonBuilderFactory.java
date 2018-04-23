package gov.usgs.aqcu.util;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.aquaticinformatics.aquarius.sdk.timeseries.serializers.InstantDeserializer;
import com.aquaticinformatics.aquarius.sdk.timeseries.serializers.InstantSerializer;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;

import gov.usgs.aqcu.serializer.LocalDateGsonSerializer;
import gov.usgs.aqcu.serializer.OffsetDateTimeGsonSerializer;

public abstract class AqcuGsonBuilderFactory {

	private static FieldNamingStrategy LOWER_CASE_CAMEL_CASE = new FieldNamingStrategy() {  
		@Override
		public String translateName(Field f) {
			String fName = f.getName();
			if(fName != null && fName.length() > 0) {
				char c[] = f.getName().toCharArray();
				c[0] = Character.toLowerCase(c[0]);
				return new String(c);
			} else {
				return fName;
			}
		}
	};

	public static GsonBuilder getConfiguredGsonBuilder() {
		return new GsonBuilder()
			.registerTypeAdapter(Instant.class, new InstantSerializer())
			.registerTypeAdapter(Instant.class, new InstantDeserializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
			.registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeGsonSerializer())
			.setFieldNamingStrategy(LOWER_CASE_CAMEL_CASE);
	}
}