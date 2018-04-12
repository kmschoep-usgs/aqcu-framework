package gov.usgs.aqcu.parameter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.format.annotation.DateTimeFormat;

import gov.usgs.aqcu.validation.ReportPeriodPresent;
import gov.usgs.aqcu.validation.StartDateBeforeEndDate;

@ReportPeriodPresent
@StartDateBeforeEndDate
public class RequestParameters {

	@NotNull
	private String primaryTimeseriesIdentifier;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate startDate;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate endDate;

	@Min(value=2)
	@Max(value=9999)
	private Integer waterYear;

	@Min(value=1)
	@Max(value=12)
	private Integer lastMonths;

	private Pair<LocalDate, LocalDate> reportPeriod;

	public Instant getStartInstant(ZoneOffset zoneOffset) {
		if (reportPeriod == null) {
			determineReportPeriod();
		}
		return reportPeriod.getLeft().atStartOfDay().toInstant(zoneOffset);
	}

	public Instant getEndInstant(ZoneOffset zoneOffset) {
		if (reportPeriod == null) {
			determineReportPeriod();
		}
		return reportPeriod.getRight().atTime(23,59,59,999999999).toInstant(zoneOffset);
	}

	public String getPrimaryTimeseriesIdentifier() {
		return primaryTimeseriesIdentifier;
	}
	public void setPrimaryTimeseriesIdentifier(String primaryTimeseriesIdentifier) {
		this.primaryTimeseriesIdentifier = primaryTimeseriesIdentifier;
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

	protected void determineReportPeriod() {
		if (lastMonths != null) {
			reportPeriod = lastMonthsToReportPeriod(lastMonths);
		} else if (waterYear != null) {
			reportPeriod = waterYearToReportPeriod(waterYear);
		} else if(startDate != null && endDate != null) {
			reportPeriod = datesToReportPeriod(startDate, endDate);
		} else {
			//Should never get here - Validation of parameters should have failed and control logic baled...
			//TODO Perhaps throw a runtime exception?
		}
	}

	protected Pair<LocalDate, LocalDate> waterYearToReportPeriod(Integer waterYear) {
		LocalDate reportStartDate = LocalDate.of(waterYear-1,10,1);
		LocalDate reportEndDate  = LocalDate.of(waterYear,9,30);

		return new ImmutablePair<LocalDate, LocalDate>(reportStartDate, reportEndDate );
	}

	protected Pair<LocalDate, LocalDate> lastMonthsToReportPeriod(Integer lastMonths) {
		LocalDate nowMinusMonths = LocalDate.now().minusMonths(lastMonths);
		LocalDate reportStartDate  = LocalDate.of(nowMinusMonths.getYear(), nowMinusMonths.getMonth(), 1);
		LocalDate reportEndDate  = LocalDate.now();

		return new ImmutablePair<LocalDate, LocalDate>(reportStartDate , reportEndDate);
	}

	protected Pair<LocalDate, LocalDate> datesToReportPeriod(LocalDate startDate, LocalDate endDate) {
		return new ImmutablePair<LocalDate, LocalDate>(startDate, endDate);
	}

	public String getAsQueryString(String overrideIdentifier, boolean absoluteTime) {
		String queryString = "";
		if (reportPeriod == null) {
			determineReportPeriod();
		}

		if(absoluteTime && reportPeriod != null) {
			queryString += "startDate=" + reportPeriod.getLeft();
			queryString += "&endDate=" + reportPeriod.getRight();
		} else {
			if (getLastMonths() != null) {
				queryString += "lastMonths=" + getLastMonths();
			} else if (getWaterYear() != null) {
				queryString += "waterYear=" + getWaterYear();
			} else if (getStartDate() != null && getEndDate() != null){
				queryString += "startDate=" + getStartDate();
				queryString += "&endDate=" + getEndDate();
			}
		}

		if(overrideIdentifier != null || getPrimaryTimeseriesIdentifier() != null) {
			queryString += "&primaryTimeseriesIdentifier=" + (overrideIdentifier != null ? overrideIdentifier : getPrimaryTimeseriesIdentifier());
		}

		return queryString;
	}
}
