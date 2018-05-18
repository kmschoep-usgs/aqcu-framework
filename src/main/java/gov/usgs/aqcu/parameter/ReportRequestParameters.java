package gov.usgs.aqcu.parameter;

import javax.validation.constraints.NotNull;

public class ReportRequestParameters extends DateRangeRequestParameters {

	@NotNull
	private String primaryTimeseriesIdentifier;

	public String getPrimaryTimeseriesIdentifier() {
		return primaryTimeseriesIdentifier;
	}
	public void setPrimaryTimeseriesIdentifier(String primaryTimeseriesIdentifier) {
		this.primaryTimeseriesIdentifier = primaryTimeseriesIdentifier;
	}

	public String getAsQueryString(String overrideIdentifier, boolean absoluteTime) {
		String queryString = "";
		if (requestPeriod == null) {
			determineRequestPeriod();
		}

		if(absoluteTime && requestPeriod != null) {
			queryString += "startDate=" + requestPeriod.getLeft();
			queryString += "&endDate=" + requestPeriod.getRight();
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
