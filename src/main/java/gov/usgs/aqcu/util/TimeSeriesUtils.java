package gov.usgs.aqcu.util;

import java.time.ZoneOffset;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

public abstract class TimeSeriesUtils {
    public static final String DV_SERIES_COMPUTATION_PERIOD_IDENTIFIER = "Daily";
    
    public static boolean isDailyTimeSeries(TimeSeriesDescription timeSeriesDescription) {
		return timeSeriesDescription != null
				&& DV_SERIES_COMPUTATION_PERIOD_IDENTIFIER.equalsIgnoreCase(timeSeriesDescription.getComputationPeriodIdentifier());
    }
    
    public static ZoneOffset getZoneOffset(TimeSeriesDescription timeSeriesDescription) {
        return AqcuTimeUtils.getZoneOffset(timeSeriesDescription == null ? 0.0 : timeSeriesDescription.getUtcOffset());
    }
}