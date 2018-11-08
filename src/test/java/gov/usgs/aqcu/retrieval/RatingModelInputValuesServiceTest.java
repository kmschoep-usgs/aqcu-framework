package gov.usgs.aqcu.retrieval;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingModelInputValuesServiceResponse;

import net.servicestack.client.IReturn;

@RunWith(SpringRunner.class)
public class RatingModelInputValuesServiceTest {

	@MockBean
	private AquariusRetrievalService aquariusService;

	private RatingModelInputValuesService service;

	@Before
	@SuppressWarnings("unchecked")
	public void setup() throws Exception {
		service = new RatingModelInputValuesService(aquariusService);

		given(aquariusService.executePublishApiRequest(any(IReturn.class))).willReturn(
			new RatingModelInputValuesServiceResponse().setInputValues(new ArrayList<>(Arrays.asList(3.0D, 2.0D, 1.0D)))
		);
	}

	@Test
	public void getTest() throws Exception {
		BigDecimal maxErrorResp = BigDecimal.valueOf(3.0D);
		BigDecimal valueResp = BigDecimal.valueOf(2.0D);
		BigDecimal minErrorResp = BigDecimal.valueOf(1.0D);
		List<BigDecimal> resultList = service.get("", Instant.now(), new ArrayList<>());
		assertEquals(3, resultList.size());
		assertThat(resultList, containsInAnyOrder(maxErrorResp, valueResp, minErrorResp));
	}

}
