package gov.usgs.aqcu.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aquaticinformatics.aquarius.sdk.timeseries.serializers.InstantDeserializer;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.PeriodOfApplicability;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalDateTimeOffset;

public abstract class AqcuTimeUtils {
	private static final Logger LOG = LoggerFactory.getLogger(AqcuTimeUtils.class);
	public static final Instant OPEN_ENDED_START_THRESHOLD = InstantDeserializer.MinConcreteValue;
	public static final Instant OPEN_ENDED_END_THRESHOLD = InstantDeserializer.MaxConcreteValue;

	public static boolean doPeriodsOverlap(PeriodOfApplicability p1, PeriodOfApplicability p2) {
		return doesTimeRangeOverlap(p1.getStartTime(), p1.getEndTime(), p2.getStartTime(), p2.getEndTime());
	}

	//Note: Aquarius Time Ranges have INCLUSIVE Start Dates and EXCLUSIVE End Dates
	public static boolean doesTimeRangeOverlap(Instant start1, Instant end1, Instant start2, Instant end2) {
		return start1.compareTo(end2) < 0 && end1.compareTo(start2) > 0;
	}

	public static boolean isOpenEndedTime(Instant time) {
		if(time.compareTo(OPEN_ENDED_START_THRESHOLD) <= 0 || time.compareTo(OPEN_ENDED_END_THRESHOLD) >= 0) {
			return true;
		}

		return false;
	}

	public static String toQueryDate(Instant time) {
		return time.toString().substring(0,10);
	}

	public static Temporal getTemporal(StatisticalDateTimeOffset dateTimeOffset, boolean isDaily, ZoneOffset zoneOffset) {
		if (dateTimeOffset.isRepresentsEndOfTimePeriod() != null && dateTimeOffset.isRepresentsEndOfTimePeriod() && isDaily) {
			// Daily values with isRepresentsEndOfTimePeriod() end up with an Instant of midnight on the next day, rather than "2400" on the actual day.
			return LocalDateTime.ofInstant(dateTimeOffset.getDateTimeOffset(), zoneOffset).toLocalDate().minusDays(1);
		} else {
			return dateTimeOffset.getDateTimeOffset();
		}
	}

	public static ZoneOffset getZoneOffset(Double utcOffset) {
		// Default to UTC
		ZoneOffset zoneOffset = ZoneOffset.UTC;
		utcOffset = utcOffset == null ? 0 : utcOffset;

		try {
			Double minutes = utcOffset % 1;
			if (minutes != 0) {
				Double hours = utcOffset - minutes;
				zoneOffset = ZoneOffset.ofHoursMinutes(hours.intValue(), (int) Math.round(minutes * 100));
			} else {
				zoneOffset = ZoneOffset.ofHours(utcOffset.intValue());
			}
		} catch (Exception e) {
			LOG.info("Error converting utcOffset({}) to ZoneOffset", utcOffset);
		}

		return zoneOffset;
	}

	public static String getTimezone(Double offset) {
		StringBuilder timezone = new StringBuilder("Etc/GMT");
		if (offset <= 0) {
			timezone.append("+");
		} else {
			timezone.append("-");
		}
		timezone.append(Math.abs(offset.intValue()));
		return timezone.toString();
	}
}