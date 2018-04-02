package gov.usgs.aqcu.model;

import java.time.Instant;
import java.time.Duration;
import java.math.BigDecimal;

public class DataGap {
	private Instant startTime = null;
	private Instant endTime = null;
	private BigDecimal durationInHours = null;
	private DataGapExtent gapExtent = DataGapExtent.OVER_ALL;

	public DataGap() {}

	public DataGap(Instant startTime, Instant endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
		calculateDurationInHours();
		calculateGapExtent();
	}

	public Instant getStartTime() {
		return startTime;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public DataGapExtent getGapExtent() {
		return gapExtent;
	}

	public BigDecimal getDurationInHours() {
		return durationInHours;
	}

	public void setStartTime(Instant val) {
		startTime= val;
		calculateDurationInHours();
		calculateGapExtent();
	}

	public void setEndTime(Instant val) {
		endTime = val;
		calculateDurationInHours();
		calculateGapExtent();
	}

	protected void calculateDurationInHours() {
		if(startTime != null && endTime != null) {
			durationInHours = BigDecimal.valueOf(Duration.between(startTime, endTime).getSeconds() / 3600.0);
		} else {
			durationInHours = null;
		}
	}

	protected void calculateGapExtent() {
		if(startTime != null && endTime != null) {
			gapExtent = DataGapExtent.CONTAINED;
		} else if(startTime == null && endTime != null) {
			gapExtent = DataGapExtent.OVER_START;
		} else if(startTime != null && endTime == null) {
			gapExtent = DataGapExtent.OVER_END;
		} else {
			gapExtent = DataGapExtent.OVER_ALL;
		}
	}
}