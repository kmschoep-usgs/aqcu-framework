package gov.usgs.aqcu.builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import gov.usgs.aqcu.model.DataGap;

@Service
public class DataGapListBuilderService {	

	public List<DataGap> buildGapList(List<TimeSeriesPoint> timeSeriesPoints) {
		List<DataGap> gapList = new ArrayList<>();

		for(int i = 0; i < timeSeriesPoints.size(); i++) {
			TimeSeriesPoint point = timeSeriesPoints.get(i);

			if(point.getValue().getNumeric() == null) {
				//Gap Marker Found			
				Instant preTime = (i > 0) ?  timeSeriesPoints.get(i-1).getTimestamp().getDateTimeOffset() : null;
				Instant postTime = (i < (timeSeriesPoints.size() -1)) ? timeSeriesPoints.get(i+1).getTimestamp().getDateTimeOffset() : null;
				DataGap gap = new DataGap();
				gap.setStartTime(preTime);
				gap.setEndTime(postTime);
				gapList.add(gap);
			}
		}

		return gapList;
	}
}
