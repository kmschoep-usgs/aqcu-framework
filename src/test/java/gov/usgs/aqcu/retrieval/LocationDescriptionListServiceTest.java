package gov.usgs.aqcu.retrieval;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceResponse;

import net.servicestack.client.IReturn;

@RunWith(SpringRunner.class)
public class LocationDescriptionListServiceTest {

	@MockBean
	private AquariusRetrievalService aquariusService;

	private LocationDescriptionListService service;
	private LocationDescription locationDescriptionA = new LocationDescription().setIdentifier("a");
	private LocationDescription locationDescriptionB = new LocationDescription().setIdentifier("b");

	@Before
	public void setup() throws Exception {
		service = new LocationDescriptionListService(aquariusService);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getTest() throws Exception {
		given(aquariusService.executePublishApiRequest(any(IReturn.class))).willReturn(new LocationDescriptionListServiceResponse()
			.setLocationDescriptions(new ArrayList<LocationDescription>(Arrays.asList(locationDescriptionA, locationDescriptionB))));
		List<LocationDescription> actual = service.getRawResponse("abc", null).getLocationDescriptions();
		assertEquals(2, actual.size());
		assertThat(actual, containsInAnyOrder(locationDescriptionA, locationDescriptionB));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getByQualifierList_happyPathTest() {
		given(aquariusService.executePublishApiRequest(any(IReturn.class))).willReturn(new LocationDescriptionListServiceResponse()
			.setLocationDescriptions(new ArrayList<LocationDescription>(Arrays.asList(locationDescriptionA, locationDescriptionB))));
		LocationDescription actual = service.getByLocationIdentifier("abc");
		assertEquals(actual, locationDescriptionA);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getByIdentifierEmptyTest() {
		given(aquariusService.executePublishApiRequest(any(IReturn.class))).willReturn(new LocationDescriptionListServiceResponse()
			.setLocationDescriptions(new ArrayList<LocationDescription>()));

		LocationDescription result = service.getByLocationIdentifier("test");
		assertEquals(result, null);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void searchSitesTest() {
		given(aquariusService.executePublishApiRequest(any(IReturn.class))).willReturn(new LocationDescriptionListServiceResponse()
			.setLocationDescriptions(new ArrayList<LocationDescription>(Arrays.asList(locationDescriptionA, locationDescriptionB))));

		List<LocationDescription> results = service.searchSites("test", 1);
		assertEquals(results.size(), 1);
		results = service.searchSites("test", 2);
		assertEquals(results.size(), 2);
		assertThat(results, containsInAnyOrder(locationDescriptionA, locationDescriptionB));
	}
}