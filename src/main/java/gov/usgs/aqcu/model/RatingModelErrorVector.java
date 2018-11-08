package gov.usgs.aqcu.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RatingModelErrorVector {
	protected BigDecimal maxErrorValue;
	protected BigDecimal value;
	protected BigDecimal minErrorValue;

	public RatingModelErrorVector() {

	}

	public RatingModelErrorVector(BigDecimal maxErrorValue, BigDecimal value, BigDecimal minErrorValue) {
		this.maxErrorValue = maxErrorValue;
		this.value = value;
		this.minErrorValue = minErrorValue;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public void setMaxErrorValue(BigDecimal maxErrorValue) {
		this.maxErrorValue = maxErrorValue;
	}
	public void setMinErrorValue(BigDecimal minErrorValue) {
		this.minErrorValue = minErrorValue;
	}
	public BigDecimal getValue() {
		return value;
	}
	public BigDecimal getMaxErrorValue() {
		return maxErrorValue;
	}
	public BigDecimal getMinErrorValue() {
		return minErrorValue;
	}
	public List<BigDecimal> getAsList() {
		return new ArrayList<>(Arrays.asList(getMaxErrorValue(), getValue(), getMinErrorValue()));
	}
}