package gov.usgs.aqcu.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.PeriodOfApplicability;
import com.aquaticinformatics.aquarius.sdk.timeseries.serializers.InstantDeserializer;

public abstract class AqcuTimeUtils {
	private static final Logger LOG = LoggerFactory.getLogger(AqcuTimeUtils.class);
	public static final Instant OPEN_ENDED_START_THRESHOLD = InstantDeserializer.MinConcreteValue;
	public static final Instant OPEN_ENDED_END_THRESHOLD = InstantDeserializer.MaxConcreteValue;

	public static boolean doPeriodsOverlap(PeriodOfApplicability p1, PeriodOfApplicability p2) {
		return doesTimeRangeOverlap(p1.getStartTime(), p1.getEndTime(), p2.getStartTime(), p2.getEndTime());
	}

	public static boolean doesTimeRangeOverlap(Instant start1, Instant end1, Instant start2, Instant end2) {
		return (start1.compareTo(end2) <= 0 && end1.compareTo(start2) >= 0);
	}

	public static boolean isOpenEndedTime(Instant time) {
		if(time.compareTo(OPEN_ENDED_START_THRESHOLD) <= 0 || time.compareTo(OPEN_ENDED_END_THRESHOLD) >= 0) {
			return true;
		}

		return false;
	}

	public static String toQueryDate(Instant time) {
		return time.toString().substring(1,10);
	}
}