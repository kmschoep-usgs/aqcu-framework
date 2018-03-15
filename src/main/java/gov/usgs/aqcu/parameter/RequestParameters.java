package gov.usgs.aqcu.parameter;

import java.time.LocalDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

}
