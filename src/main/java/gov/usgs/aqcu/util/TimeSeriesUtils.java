package gov.usgs.aqcu.util;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttribute;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttributeFilter;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import gov.usgs.aqcu.model.InstantRange;
import gov.usgs.aqcu.model.MinMaxData;
import gov.usgs.aqcu.model.MinMaxPoint;

public abstract class TimeSeriesUtils {
	public static final String DV_SERIES_COMPUTATION_PERIOD_IDENTIFIER = "Daily";
	public static final String ESTIMATED_QUALIFIER_VALUE = "ESTIMATED";

	public static boolean isDailyTimeSeries(TimeSeriesDescription timeSeriesDescription) {
		return timeSeriesDescription != null
				&& DV_SERIES_COMPUTATION_PERIOD_IDENTIFIER.equalsIgnoreCase(timeSeriesDescription.getComputationPeriodIdentifier());
	}

	public static boolean isPrimaryTimeSeries(TimeSeriesDescription timeSeriesDescription) {
		ExtendedAttributeFilter primaryFilter = AquariusRetrievalUtils.getPrimaryFilter();
		if(timeSeriesDescription != null && timeSeriesDescription.getExtendedAttributes() != null && !timeSeriesDescription.getExtendedAttributes().isEmpty()) {
			for(ExtendedAttribute ext : timeSeriesDescription.getExtendedAttributes()) {
				if(ext.getName() != null && ext.getValue() != null && ext.getName().equals(primaryFilter.getFilterName()) && ext.getValue().equals(primaryFilter.getFilterValue())) {
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

	public static List<InstantRange> getEstimatedPeriods(List<Qualifier> qualifiers) {
		List<InstantRange> estimatedPeriods = qualifiers.stream()
			.filter(x -> x.getIdentifier().equals(ESTIMATED_QUALIFIER_VALUE))
			.map(x -> {
				InstantRange dateRange = new InstantRange(x.getStartTime(), x.getEndTime());
				return dateRange;
			})
			.collect(Collectors.toList());
		return estimatedPeriods;
	}

	/**
	 * This method should only be called if the timeSeriesPoints list is not null.
	 */
	public static MinMaxData getMinMaxData(List<TimeSeriesPoint> timeSeriesPoints) {
		Map<BigDecimal, List<MinMaxPoint>> minMaxPoints = timeSeriesPoints.parallelStream()
				.map(x -> {
					MinMaxPoint point = new MinMaxPoint(x.getTimestamp().getDateTimeOffset(), DoubleWithDisplayUtil.getRoundedValue(x.getValue()));
					return point;
				})
				.filter(x -> x.getValue() != null)
				.collect(Collectors.groupingByConcurrent(MinMaxPoint::getValue));

		BigDecimalSummaryStatistics stats = minMaxPoints.keySet().parallelStream()
				.collect(BigDecimalSummaryStatistics::new,
						BigDecimalSummaryStatistics::accept,
						BigDecimalSummaryStatistics::combine);

		return new MinMaxData(stats.getMin(), stats.getMax(), minMaxPoints);
	}
}