package gov.usgs.aqcu.builder;

import java.util.List;
import java.util.ArrayList;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import gov.usgs.aqcu.model.DataGap;
import gov.usgs.aqcu.model.DataGapExtent;

@Component
public class DataGapListBuilderService {	
	private static final Logger LOG = LoggerFactory.getLogger(DataGapListBuilderService.class);

	public List<DataGap> buildGapList(List<TimeSeriesPoint> timeSeriesPoints, Instant startDate, Instant endDate) {
		List<DataGap> gapList = new ArrayList<>();

		for(int i = 0; i < timeSeriesPoints.size(); i++) {
			TimeSeriesPoint point = timeSeriesPoints.get(i);
			Instant preTime = (i > 0) ?  timeSeriesPoints.get(i-1).getTimestamp().getDateTimeOffset() : null;
			Instant postTime = (i < (timeSeriesPoints.size() -1)) ? timeSeriesPoints.get(i+1).getTimestamp().getDateTimeOffset() : null;

			if(point.getValue().getNumeric() == null) {
				//Gap Marker Found
				DataGap gap = new DataGap();
				gap.setStartTime(preTime);
				gap.setEndTime(postTime);

				//Determine where this gap is
				if(preTime != null && postTime != null) {
					gap.setGapExtent(DataGapExtent.CONTAINED);
				} else if(preTime == null) {
					gap.setGapExtent(DataGapExtent.OVER_START);
				} else if(postTime == null) {
					gap.setGapExtent(DataGapExtent.OVER_END);
				} else {
					gap.setGapExtent(DataGapExtent.OVER_ALL);
				}

				gapList.add(gap);
			}
		}

		return gapList;
	}
}
	
