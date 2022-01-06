package gov.usgs.owi.nldi.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.owi.nldi.BaseIT;
import gov.usgs.owi.nldi.transform.FeatureTransformer;

@EnableWebMvc
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/nldi_data/crawler_source.xml")
@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
public class LinkedDataControllerDataSourceIT extends BaseIT {

	@Value("${serverContextPath}")
	private String context;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@BeforeEach
	public void setUp() {
		urlRoot = "http://localhost:" + port + context;
	}

	//Navigation Within Datasource Testing
	@Test
	@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
	public void getWqpUtTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigation/UT/wqp?distance=9999",
				HttpStatus.OK.value(),
				FeatureTransformer.FEATURE_COUNT_HEADER,
				"7",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER_WQP, "wqp_USGS-05427880_UT_wqp.json"),
				true,
				false);
	}

	@Test
	@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
	public void getWqpUtTestDistance1() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigation/UT/wqp?distance=1",
				HttpStatus.OK.value(),
				FeatureTransformer.FEATURE_COUNT_HEADER,
				"6",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER_WQP, "wqp_USGS-05427880_UT_wqp_distance_1.json"),
				true,
				false);

	}


	@Test
	@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
	public void getWqpUtTestDistanceEmpty() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigation/UT/wqp?distance=",
				HttpStatus.OK.value(),
				FeatureTransformer.FEATURE_COUNT_HEADER,
				"7",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER_WQP, "wqp_USGS-05427880_UT_wqp_distance_empty.json"),
				true,
				false);
	}

	@Test
	@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
	public void getWqpUtTestDistanceAboveMax() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigation/UT/wqp?distance=10000",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFeatures.distance: distance must be between 1 and 9999 kilometers",
				false,
				false);
	}


	@Test
	@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
	public void getWqpUtTestDistanceBelowMin() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigation/UT/wqp?distance=-1",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFeatures.distance: distance must be between 1 and 9999 kilometers",
				false,
				false);
	}

	//Navigation Different Datasource Testing
	@Test
	@DatabaseSetup("classpath:/testData/nldi_data/feature/huc12pp.xml")
	@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
	public void getWqpDmTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigation/DM/huc12pp?distance=9999",
				HttpStatus.OK.value(),
				FeatureTransformer.FEATURE_COUNT_HEADER,
				"19",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER_WQP, "wqp_USGS-05427880_DM_huc12pp.json"),
				true,
				false);
	}

	@Test
	public void badInputTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqx/USGS-05427880/navigation/DM/huc12pp?distance=9999",
				HttpStatus.NOT_FOUND.value(),
				null,
				null,
				null,
				null,
				true,
				false);
	}

	//Parameter Error Testing
	@Test
	public void badNavigationModeTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigation/XX/huc12pp?distance=9999",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				"getFeatures.navigationMode: must match \"DD|DM|PP|UT|UM\"",
				false,
				false);
	}

}
