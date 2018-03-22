package gov.usgs.aqcu.parameter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.format.annotation.DateTimeFormat;

import gov.usgs.aqcu.validation.ReportPeriodPresent;
import gov.usgs.aqcu.validation.StartDateBeforeEndDate;
import gov.usgs.aqcu.util.AqcuTimeUtils;

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

	private Pair<Instant,Instant> reportPeriod;

	public Instant getStartInstant() {
		if (reportPeriod == null) {
			determineReportPeriod();
		}
		return reportPeriod.getLeft();
	}

	public Instant getEndInstant() {
		if (reportPeriod == null) {
			determineReportPeriod();
		}
		return reportPeriod.getRight();
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
		} else {
			reportPeriod = new ImmutablePair<Instant,Instant>(dateToReportStartTime(startDate), dateToReportEndTime(endDate));
		}
	}

	protected Pair<Instant,Instant> waterYearToReportPeriod(Integer waterYear) {
		Instant reportStartTime = Instant.from(LocalDateTime.of(waterYear-1,10,1,0,0,0).toInstant(ZoneOffset.UTC));
		Instant reportEndTime = Instant.from(LocalDateTime.of(waterYear,9,30,23,59,59,999999999).toInstant(ZoneOffset.UTC));

		return new ImmutablePair<Instant,Instant>(reportStartTime, reportEndTime);
	}

	protected Pair<Instant,Instant> lastMonthsToReportPeriod(Integer lastMonths) {
		LocalDate nowDate = LocalDate.now().minusMonths(lastMonths);
		Instant reportStartTime = LocalDateTime.of(nowDate.getYear(), nowDate.getMonth(), 1, 0, 0, 0).toInstant(ZoneOffset.UTC);
		Instant reportEndTime = LocalDate.now().atTime(23,59,59,999999999).toInstant(ZoneOffset.UTC);

		return new ImmutablePair<Instant,Instant>(reportStartTime, reportEndTime);
	}

	protected Instant dateToReportStartTime(LocalDate startDate) {
		Instant startTime = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
		return startTime;
	}

	protected Instant dateToReportEndTime(LocalDate endDate) {
		Instant endTime = endDate.atTime(23,59,59,999999999).toInstant(ZoneOffset.UTC);
		return endTime;
	}

	public String getAsQueryString(String overrideIdentifier, boolean absoluteTime) {
		String queryString = "";

		if(absoluteTime) {
			queryString += "startDate=" + AqcuTimeUtils.toQueryDate(getStartInstant());
			queryString += "&endDate=" + AqcuTimeUtils.toQueryDate(getEndInstant());
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
		
		queryString += "&primaryTimeseriesIdentifier=" + (overrideIdentifier != null ? overrideIdentifier : getPrimaryTimeseriesIdentifier());

		return queryString;
	}
}
