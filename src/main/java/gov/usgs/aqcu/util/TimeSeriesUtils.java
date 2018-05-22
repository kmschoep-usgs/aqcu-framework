package gov.usgs.aqcu.util;

import java.time.ZoneOffset;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttribute;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttributeFilter;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

public abstract class TimeSeriesUtils {
	public static final String DV_SERIES_COMPUTATION_PERIOD_IDENTIFIER = "Daily";

	public static boolean isDailyTimeSeries(TimeSeriesDescription timeSeriesDescription) {
		return timeSeriesDescription != null
				&& DV_SERIES_COMPUTATION_PERIOD_IDENTIFIER.equalsIgnoreCase(timeSeriesDescription.getComputationPeriodIdentifier());
	}

	public static boolean isPrimaryTimeSeries(TimeSeriesDescription timeSeriesDescription) {
		ExtendedAttributeFilter primaryFilter = AquariusRetrievalUtils.getPrimaryFilter();
        for(ExtendedAttribute ext : timeSeriesDescription.getExtendedAttributes()) {
            if(ext.getName() != null && ext.getValue() != null) {
                if(ext.getName().equals(primaryFilter.getFilterName()) && ext.getValue().equals(primaryFilter.getFilterValue())) {
                    return true;
                }
            }
		}
		return false;
	}

	public static ZoneOffset getZoneOffset(TimeSeriesDescription timeSeriesDescription) {
		return AqcuTimeUtils.getZoneOffset(timeSeriesDescription == null || timeSeriesDescription.getUtcOffset() == null ? 0.0 : timeSeriesDescription.getUtcOffset());
	}

	public static ZoneOffset getZoneOffset(LocationDataServiceResponse locationResponse) {
		return AqcuTimeUtils.getZoneOffset(locationResponse == null || locationResponse.getUtcOffset() == null ? 0.0 : locationResponse.getUtcOffset());
	}
}