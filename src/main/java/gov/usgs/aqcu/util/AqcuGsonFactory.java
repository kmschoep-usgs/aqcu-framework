package gov.usgs.aqcu.util;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;

import com.aquaticinformatics.aquarius.sdk.timeseries.serializers.InstantSerializer;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.spring.web.json.Json;

import com.aquaticinformatics.aquarius.sdk.timeseries.serializers.InstantDeserializer;
import gov.usgs.aqcu.serializer.SwaggerGsonSerializer;
import gov.usgs.aqcu.serializer.LocalDateGsonSerializer;

public abstract class AqcuGsonFactory {
	private static final Logger LOG = LoggerFactory.getLogger(AqcuGsonFactory.class);
	
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

	public static Gson getConfiguredGson() {
		return new GsonBuilder()
			.registerTypeAdapter(Instant.class, new InstantSerializer())
			.registerTypeAdapter(Instant.class, new InstantDeserializer())
			.registerTypeAdapter(Json.class, new SwaggerGsonSerializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
			.setFieldNamingStrategy(LOWER_CASE_CAMEL_CASE)
			.create();
	}
}