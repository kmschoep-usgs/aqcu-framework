package gov.usgs.aqcu.retrieval;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierMetadata;

import net.servicestack.client.IReturn;

@RunWith(SpringRunner.class)
public class QualifierLookupServiceTest {

	@MockBean
	private AquariusRetrievalService aquariusService;

	private QualifierLookupService service;
	private Qualifier qualifierA = new Qualifier().setIdentifier("a");
	private Qualifier qualifierB = new Qualifier().setIdentifier("b");
	private Qualifier qualifierC = new Qualifier().setIdentifier("c");
	private Qualifier qualifierD = new Qualifier().setIdentifier("d");
	private QualifierMetadata qualifierMetadataA = new QualifierMetadata().setIdentifier("a");
	private QualifierMetadata qualifierMetadataB = new QualifierMetadata().setIdentifier("b");
	private QualifierMetadata qualifierMetadataC = new QualifierMetadata().setIdentifier("c");
	private QualifierMetadata qualifierMetadataD = new QualifierMetadata().setIdentifier("d");

	@Before
	@SuppressWarnings("unchecked")
	public void setup() throws Exception {
		service = new QualifierLookupService(aquariusService);
		given(aquariusService.executePublishApiRequest(any(IReturn.class))).willReturn(new QualifierListServiceResponse()
			.setQualifiers(new ArrayList<QualifierMetadata>(Arrays.asList(qualifierMetadataA, qualifierMetadataB, qualifierMetadataC, qualifierMetadataD))));
	}

	@Test
	public void buildItentifierListTest() {
		List<String> actual = service.buildIdentifierList(Arrays.asList(qualifierA, qualifierB, qualifierC, qualifierD));
		assertEquals(4, actual.size());
		assertThat(actual, containsInAnyOrder("a", "b", "c", "d"));
	}

	@Test
	public void filterListTest() {
		Map<String, QualifierMetadata> actual = service.filterList(Arrays.asList("a", "c", "d"), Arrays.asList(qualifierMetadataA, qualifierMetadataB, qualifierMetadataC, qualifierMetadataD));
		assertEquals(3, actual.size());
		assertThat(actual, IsMapContaining.hasEntry("a", qualifierMetadataA));
		assertThat(actual, IsMapContaining.hasEntry("c", qualifierMetadataC));
		assertThat(actual, IsMapContaining.hasEntry("d", qualifierMetadataD));
	}

	@Test
	public void getByQualifierList_happyPathTest() {
		Map<String, QualifierMetadata> actual = service.getByQualifierList(Arrays.asList(qualifierA, qualifierC, qualifierD));
		assertEquals(3, actual.size());
		assertThat(actual, IsMapContaining.hasEntry("a", qualifierMetadataA));
		assertThat(actual, IsMapContaining.hasEntry("c", qualifierMetadataC));
		assertThat(actual, IsMapContaining.hasEntry("d", qualifierMetadataD));
	}
}