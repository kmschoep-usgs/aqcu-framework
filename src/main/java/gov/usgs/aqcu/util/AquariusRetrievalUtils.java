package gov.usgs.aqcu.util;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttributeFilter;

import org.springframework.stereotype.Repository;


@Repository
public abstract class AquariusRetrievalUtils {	
	public static ExtendedAttributeFilter getPrimaryFilter() {
		ExtendedAttributeFilter primaryFilter = new ExtendedAttributeFilter();
			primaryFilter
				.setFilterName("PRIMARY_FLAG")
				.setFilterValue("Primary");
		return primaryFilter;
	}
}
