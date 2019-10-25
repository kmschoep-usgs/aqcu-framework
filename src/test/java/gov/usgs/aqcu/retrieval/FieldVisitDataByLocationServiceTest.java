package gov.usgs.aqcu.retrieval;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataByLocationServiceResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import net.servicestack.client.IReturn;

@RunWith(SpringRunner.class)
public class FieldVisitDataByLocationServiceTest {

	@MockBean
	private AquariusRetrievalService aquariusService;

	private FieldVisitDataByLocationService service;
	private FieldVisitDataByLocationServiceResponse expectedResponse = new FieldVisitDataByLocationServiceResponse();

	@Before
	@SuppressWarnings("unchecked")
	public void setup() throws Exception {
		service = new FieldVisitDataByLocationService(aquariusService);
		given(aquariusService.executePublishApiRequest(any(IReturn.class))).willReturn(expectedResponse);
	}

	@Test
	public void get_happyTest() {
		FieldVisitDataByLocationServiceResponse actual = service.get("a", true, new ArrayList<>(), new ArrayList<>(), "b");
		assertEquals(expectedResponse, actual);
	}

	@Test
	public void get_nullTest() {
		FieldVisitDataByLocationServiceResponse actual = service.get("a", true, null, null, null);
		assertEquals(expectedResponse, actual);
	}
}
