package gov.usgs.aqcu.retrieval;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.usgs.aqcu.client.NwisRaClient;
import gov.usgs.aqcu.model.nwis.WaterQualitySampleRecord;
import gov.usgs.aqcu.model.nwis.GroundWaterParameter;
import gov.usgs.aqcu.model.nwis.ParameterRecord;
import gov.usgs.aqcu.model.nwis.ParameterRecords;
import gov.usgs.aqcu.model.nwis.WaterLevelRecords;
import gov.usgs.aqcu.model.nwis.WaterQualitySampleRecords;
import gov.usgs.aqcu.parameter.DateRangeRequestParameters;

@Repository
public class NwisRaService {
	private static final Logger LOG = LoggerFactory.getLogger(NwisRaService.class);

	private static final DateTimeFormatter PARTIAL_DATA_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

	private NwisRaClient nwisRaClient;

	protected static final String AQ_PARAMS_FILTER_VALUE = "AQUNIT"; //we will be matching on UNIT
	protected static final String AQ_NAME_PARAMS_FILTER_VALUE = "AQNAME"; //this is to get the AQ name
	protected static final String GW_LEV_COLUMN_GROUPS_TO_RETRIEVE = "WaterLevelDate,WaterLevelBelowLSDValue,WaterLevelAboveSeaLevelValue,WaterLevelBelowMPValue,WaterLevelMeta";
	protected static final String QW_COLUMN_GROUPS_TO_RETRIEVE = "WQBySample";

	@Autowired
	public NwisRaService(NwisRaClient nwisRaClient) {
		this.nwisRaClient = nwisRaClient;
	}

	public List<ParameterRecord> getAqParameterUnits() {
		ResponseEntity<String> responseEntity = nwisRaClient.getParameters(AQ_PARAMS_FILTER_VALUE);
		return deseriealize(responseEntity.getBody(), ParameterRecords.class).getRecords();
	}

	public List<ParameterRecord> getAqParameterNames() {
		ResponseEntity<String> responseEntity = nwisRaClient.getParameters(AQ_NAME_PARAMS_FILTER_VALUE);
		return deseriealize(responseEntity.getBody(), ParameterRecords.class).getRecords();
	}

	public WaterLevelRecords getGwLevels(DateRangeRequestParameters requestParameters, String siteId,
			GroundWaterParameter gwParam, ZoneOffset zoneOffset) {
		ResponseEntity<String> responseEntity = nwisRaClient.getWaterLevelRecords(siteId,
				GW_LEV_COLUMN_GROUPS_TO_RETRIEVE, getPartialDateString(requestParameters, zoneOffset),
				gwParam.getGwLevEnt(), gwParam.getSeaLevDatum());
		return deseriealize(responseEntity.getBody(), WaterLevelRecords.class);
	}

	public List<WaterQualitySampleRecord> getQwData(DateRangeRequestParameters requestParameters, String siteId,
			String nwisPcode, ZoneOffset zoneOffset) {
		ResponseEntity<String> responseEntity = nwisRaClient.getWaterQualitySampleRecords(siteId,
				QW_COLUMN_GROUPS_TO_RETRIEVE, "true", "true", nwisPcode, nwisPcode,
				getPartialDateString(requestParameters, zoneOffset));
		return deseriealize(responseEntity.getBody(), WaterQualitySampleRecords.class).getRecords();
	}

	public String getNwisPcode(String aqName, String unit) 
	{
		String pcode = null;

		// First find the NWIS name using the nameAliases
		Optional<ParameterRecord> nwisName = this.getAqParameterNames().parallelStream()
				.filter(x -> x.getAlias().equals(aqName))
				.findFirst();

		if (nwisName.isPresent()) {
			// then find the pcode using the name and unit
			Optional<ParameterRecord> unitAlias = this.getAqParameterUnits().parallelStream()
					.filter(x -> x.getAlias().equals(unit) && x.getName().equals(nwisName.get().getName()))
					.findAny();
			if (unitAlias.isPresent()) {
				pcode = unitAlias.get().getCode();
			}
		}
		return pcode;
	}

	protected String getPartialDateString(DateRangeRequestParameters requestParameters, ZoneOffset zoneOffset) {
		// date range param needs to be of the form YYYYMMDD,YYYYMMDD
		String dateRange = PARTIAL_DATA_FORMAT.withZone(zoneOffset)
				.format(requestParameters.getStartInstant(zoneOffset)) + ","
				+ PARTIAL_DATA_FORMAT.withZone(zoneOffset).format(requestParameters.getEndInstant(zoneOffset));
		return dateRange;
	}

	protected <T> T deseriealize(String json, Class<T> clazz) {
		T rtn;
		ObjectMapper mapper = new ObjectMapper();
		try {
			rtn = mapper.readValue(json, clazz);
		} catch (Exception e) {
			LOG.error("Problems deserializing ", e);
			throw new RuntimeException(e);
		}
		return rtn;
	}
}
