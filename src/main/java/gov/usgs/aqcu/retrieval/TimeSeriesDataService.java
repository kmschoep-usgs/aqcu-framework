package gov.usgs.aqcu.retrieval;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataCorrectedServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataRawServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

import gov.usgs.aqcu.parameter.DateRangeRequestParameters;
import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class TimeSeriesDataService {
	private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesDataService.class);

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public TimeSeriesDataService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}
	
	@LogExecutionTime
	public TimeSeriesDataServiceResponse get(String timeseriesIdentifier, DateRangeRequestParameters requestParameters, ZoneOffset zoneOffset, Boolean isDaily, Boolean isRaw, Boolean doIncludeGaps, String getParts) {
		TimeSeriesDataServiceResponse timeSeriesResponse = new TimeSeriesDataServiceResponse();

		/** 
		 * 24:00 Times in Aquarius for DV Points
		 * 
		 * In Aquarius DV points are returned at a time of "24:00" on the day they occurred. However, because of the way that the AQ 
		 * API handles 24:00s on requests, these points end up being treated as occuring at midnight on the following day, instead of 
		 * 24:00 on the preceding day. I.E: Daily Value for 01-01-2000 is recorded at "01-01-2000T24:00:00" but gets parsed into 
		 * "01-02-2000T00:00:00". If you make an API request for "01-01-2000T24:00:00" you don't get the DV point for 01-01-2000, 
		 * you get the DV point for 01-02-2000.
		 * 
		 * As a result, we need to push our request end date one day into the future to retrieve the daily value for our actual end date. 
		 * Theoretically, we would want to push our request start date one day into the future as well, otherwise we would recieve the DV
		 * point for the 1st day prior to our request period since we always request start dates at midnight and the preceding day's DV
		 * 24:00 time would be interpreted as being at midnight on our request day. However, another quirk of the API prevents that from
		 * being a viable solution.
		 * 
		 * The Aquarius API also will cut off certain time-range data to the bounds of the request period, even if those aren't the true
		 * bounds of the time-range data. I.E: If there is an estimated qualifier from 01-03-2000T00:00:00 to 01-13-2000T00:00:00 then a
		 * request for time series data including qualifiers from 01-04-2000T00:00:00 to 01-06-2000T00:00:00 will return the estimated
		 * period as being active from 01-04-2000T00:00:00 to 01-06-2000T00:00:00 instead of returning its actual effective period of
		 * 01-03-2000T00:00:00 to 01-13-2000T00:00:00. As a result, if we push our request start date one day into the future our 
		 * Estimated qualifier effective start date gets pushed one day into the future, and then our DV point on 01-04-2000:24:00:00
		 * ends up not being marked as estimated, since our new effective estimaged qualifier start date would be 01-05-2000T00:00:00Z.
		 * 
		 * Thus, the only viable solution is to push our request end date one day further to get the last DV point we require, but keep
		 * our request start date the same. When we get the list of points back we then chop off the first recieved point in the list
		 * because it is the DV point from the preceding day to our report start date. This keeps the time-range bounded data to the
		 * span of the entire report range, and prevents issues like the one described above.
		 * 
		 * */ 
		
		Instant endDate = adjustIfDv(requestParameters.getEndInstant(zoneOffset), isDaily);

		try {
			timeSeriesResponse = get(timeseriesIdentifier, requestParameters.getStartInstant(zoneOffset), endDate, isRaw, doIncludeGaps, getParts);
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to fetch TimeSeriesData" + 
				(isRaw ? "Raw" : "Corrected") + "ServiceRequest from Aquarius: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
		
		// Remove the first point from daily series because it is the DV point from the day prior to our request start date
		if(isDaily && timeSeriesResponse != null && timeSeriesResponse.getPoints() != null && !timeSeriesResponse.getPoints().isEmpty()) {
			timeSeriesResponse.setPoints(new ArrayList<>(timeSeriesResponse.getPoints().subList(1, timeSeriesResponse.getPoints().size())));
		}

		return timeSeriesResponse;
	}

	protected TimeSeriesDataServiceResponse get(String timeSeriesIdentifier, Instant startDate, Instant endDate, Boolean isRaw, Boolean doIncludeGaps, String getParts) {
		TimeSeriesDataServiceResponse timeSeriesResponse;

		if(isRaw != null && !isRaw) {
			TimeSeriesDataCorrectedServiceRequest request = new TimeSeriesDataCorrectedServiceRequest()
				.setTimeSeriesUniqueId(timeSeriesIdentifier)
				.setQueryFrom(startDate)
				.setQueryTo(endDate)
				.setApplyRounding(true)
				.setIncludeGapMarkers(doIncludeGaps)
				.setGetParts(getParts);
			timeSeriesResponse = aquariusRetrievalService.executePublishApiRequest(request);
		} else {
			TimeSeriesDataRawServiceRequest request = new TimeSeriesDataRawServiceRequest()
				.setTimeSeriesUniqueId(timeSeriesIdentifier)
				.setQueryFrom(startDate)
				.setQueryTo(endDate)
				.setApplyRounding(true)
				.setGetParts(getParts);
			timeSeriesResponse = aquariusRetrievalService.executePublishApiRequest(request);
		}
		
		return timeSeriesResponse;
	}

	protected Instant adjustIfDv(Instant instant, boolean isDaily) {
		return isDaily ? instant.plus(Duration.ofDays(1)) : instant;
	}
}
