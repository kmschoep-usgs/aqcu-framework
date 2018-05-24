package gov.usgs.aqcu.util;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttributeFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;


@Repository
public abstract class AquariusRetrievalUtils {
	private static final Logger LOG = LoggerFactory.getLogger(AquariusRetrievalUtils.class);
	
	public static ExtendedAttributeFilter getPrimaryFilter() {
		ExtendedAttributeFilter primaryFilter = new ExtendedAttributeFilter();
			primaryFilter
				.setFilterName("PRIMARY_FLAG")
				.setFilterValue("Primary");
		return primaryFilter;
	}
}
