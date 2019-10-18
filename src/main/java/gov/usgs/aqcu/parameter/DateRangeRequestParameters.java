package gov.usgs.aqcu.parameter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.format.annotation.DateTimeFormat;

import gov.usgs.aqcu.validation.RequestPeriodPresent;
import gov.usgs.aqcu.validation.StartDateBeforeEndDate;

@RequestPeriodPresent
@StartDateBeforeEndDate
public class DateRangeRequestParameters {
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	protected LocalDate startDate;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	protected LocalDate endDate;

	@Min(value=2)
	@Max(value=9999)
	protected Integer waterYear;

	@Min(value=1)
	@Max(value=12)
	protected Integer lastMonths;

	protected Pair<LocalDate, LocalDate> requestPeriod;

	public Instant getStartInstant(ZoneOffset zoneOffset) {
		if (requestPeriod == null) {
			determineRequestPeriod();
		}
		return requestPeriod.getLeft().atStartOfDay().toInstant(zoneOffset);
	}

	public Instant getEndInstant(ZoneOffset zoneOffset) {
		if (requestPeriod == null) {
			determineRequestPeriod();
		}
		return requestPeriod.getRight().atTime(23,59,59,999999999).toInstant(zoneOffset);
	}

	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public Integer getWaterYear() {
		return waterYear;
	}
	public void setWaterYear(Integer waterYear) {
		this.waterYear = waterYear;
	}
	public Integer getLastMonths() {
		return lastMonths;
	}
	public void setLastMonths(Integer lastMonths) {
		this.lastMonths = lastMonths;
	}

	protected void determineRequestPeriod() {
		if (lastMonths != null) {
			requestPeriod = lastMonthsToRequestPeriod(lastMonths);
		} else if (waterYear != null) {
			requestPeriod = waterYearToRequestPeriod(waterYear);
		} else if(startDate != null && endDate != null) {
			requestPeriod = datesToRequestPeriod(startDate, endDate);
		} else {
			//Should never get here - Validation of parameters should have failed and control logic baled...
			throw new RuntimeException("Failed to convert request parameters to request period.");
		}
	}

	protected Pair<LocalDate, LocalDate> waterYearToRequestPeriod(Integer waterYear) {
		LocalDate reportStartDate = LocalDate.of(waterYear-1,10,1);
		LocalDate reportEndDate  = LocalDate.of(waterYear,9,30);

		return new ImmutablePair<LocalDate, LocalDate>(reportStartDate, reportEndDate );
	}

	protected Pair<LocalDate, LocalDate> lastMonthsToRequestPeriod(Integer lastMonths) {
		LocalDate nowMinusMonths = LocalDate.now().minusMonths(lastMonths);
		LocalDate reportStartDate  = LocalDate.of(nowMinusMonths.getYear(), nowMinusMonths.getMonth(), 1);
		LocalDate reportEndDate  = LocalDate.now();

		return new ImmutablePair<LocalDate, LocalDate>(reportStartDate , reportEndDate);
	}

	protected Pair<LocalDate, LocalDate> datesToRequestPeriod(LocalDate startDate, LocalDate endDate) {
		return new ImmutablePair<LocalDate, LocalDate>(startDate, endDate);
	}
}
