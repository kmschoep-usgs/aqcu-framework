package gov.usgs.aqcu.model;

import java.math.BigDecimal;
import java.time.Instant;

/** 
 * This is an abbreviated version of com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DischargeSummary
 * It has:
 * a) Several fields renamed;
 *	  1) MeasurementId -> measurementNumber;
 *	  2) MeasurementStartTime -> measurementStartDate;
 * b) Discharge from the rounded value;
 * c) Error range calculated from the rounded value:
 *	  1) errorMinDischarge;
 *	  2) errorMaxDischarge
 */
public class FieldVisitMeasurement {
	private BigDecimal shiftInFeet;
	private BigDecimal errorMinShiftInFeet;
	private BigDecimal errorMaxShiftInFeet;
	private BigDecimal discharge;
	private BigDecimal errorMinDischarge;
	private BigDecimal errorMaxDischarge;
	private BigDecimal meanGageHeight;
	private Integer shiftNumber;
	private Instant measurementStartDate;
	private String measurementNumber;
	private Boolean publish;
	private Boolean historic;

	public FieldVisitMeasurement(){
	}

	public BigDecimal getDischarge() {
		return discharge;
	}
	public void setDischarge(BigDecimal discharge) {
		this.discharge = discharge;
	}
	public Instant getMeasurementStartDate() {
		return measurementStartDate;
	}
	public void setMeasurementStartDate(Instant measurementStartDate) {
		this.measurementStartDate = measurementStartDate;
	}
	public BigDecimal getErrorMinDischarge() {
		return errorMinDischarge;
	}
	public void setErrorMinDischarge(BigDecimal errorMinDischarge) {
		this.errorMinDischarge = errorMinDischarge;
	}
	public BigDecimal getErrorMaxDischarge() {
		return errorMaxDischarge;
	}
	public void setErrorMaxDischarge(BigDecimal errorMaxDischarge) {
		this.errorMaxDischarge = errorMaxDischarge;
	}
	public String getMeasurementNumber() {
		return measurementNumber;
	}
	public void setMeasurementNumber(String measurementNumber) {
		this.measurementNumber = measurementNumber;
	}
	public Boolean isPublish() {
		return publish;
	}
	public void setPublish(Boolean publish) {
		this.publish = publish;
	}
	public BigDecimal getShiftInFeet() {
		return shiftInFeet;
	}
	public void setShiftInFeet(BigDecimal shiftInFeet) {
		this.shiftInFeet = shiftInFeet;
	}
	public BigDecimal getErrorMaxShiftInFeet() {
		return errorMaxShiftInFeet;
	}
	public void setErrorMaxShiftInFeet(BigDecimal errorMaxShiftInFeet) {
		this.errorMaxShiftInFeet = errorMaxShiftInFeet;
	}
	public BigDecimal getErrorMinShiftInFeet() {
		return errorMinShiftInFeet;
	}
	public void setErrorMinShiftInFeet(BigDecimal errorMinShiftInFeet) {
		this.errorMinShiftInFeet = errorMinShiftInFeet;
	}
	public BigDecimal getMeanGageHeight() {
		return meanGageHeight;
	}
	public void setMeanGageHeight(BigDecimal meanGageHeight) {
		this.meanGageHeight = meanGageHeight;
	}
	public Boolean isHistoric() {
		return historic;
	}
	public void setHistoric(Boolean historic) {
		this.historic = historic;
	}
	public Integer getShiftNumber() {
		return shiftNumber;
	}
	public void setShiftNumber(Integer shiftNumber) {
		this.shiftNumber = shiftNumber;
	}
}
