package gov.usgs.aqcu.serializer;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.aquaticinformatics.aquarius.sdk.timeseries.serializers.InstantDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class OffsetDateTimeGsonSerializer implements JsonSerializer<OffsetDateTime> {

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern(InstantDeserializer.Pattern)
			.withLocale(Locale.US)
			.withZone(ZoneId.of("UTC"));

	public JsonElement serialize(OffsetDateTime date, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(FORMATTER.format(date));
	}
}
