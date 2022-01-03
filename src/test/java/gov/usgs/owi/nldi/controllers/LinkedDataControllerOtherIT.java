package gov.usgs.owi.nldi.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;

import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.owi.nldi.BaseIT;
import gov.usgs.owi.nldi.transform.BasinTransformer;

@EnableWebMvc
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@DatabaseSetup("classpath:/testData/nldi_data/crawler_source.xml")
public class LinkedDataControllerOtherIT extends BaseIT {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String RESULT_FOLDER  = "feature/other/";

	@BeforeEach
	public void setUp() {
		urlRoot = "http://localhost:" + port + context;
	}

	@Test
	public void getCharacteristicDataTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13302592/tot",
				HttpStatus.OK.value(),
				null,
				null,
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "data/comid_13302592_tot.json"),
				true,
				false);
	}

	@Test
	public void getCharacteristicDataMissingTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/133999999/tot",
				HttpStatus.NOT_FOUND.value(),
				null,
				null,
				null,
				null,
				true,
				true);
	}

	@Test
	public void getCharacteristicDataFilteredTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13302592/tot?characteristicId=TOT_N97&characteristicId=TOT_ET",
				HttpStatus.OK.value(),
				null,
				null,
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "data/comid_13302592_tot_filtered.json"),
				true,
				false);
	}

	@Test
	public void getBasinTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13302592/basin",
				HttpStatus.OK.value(),
				BasinTransformer.BASIN_COUNT_HEADER,
				"1",
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "basin/comid_13302592.json"),
				true,
				false);
	}

	@Test
	public void getBasinMissingTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/1330259299/basin",
				HttpStatus.NOT_FOUND.value(),
				null,
				null,
				null,
				null,
				true,
				true);
	}


	//DataSources Testing
	@Test
	public void getDataSourcesTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data",
				HttpStatus.OK.value(),
				null,
				null,
				MediaType.APPLICATION_JSON_VALUE,
				getCompareFile(RESULT_FOLDER, "dataSources.json"),
				true,
				false);
	}

	//Features Testing
	@Test
	public void getFeaturesTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp",
				HttpStatus.OK.value(),
				null,
				null,
			 	BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "wqpFeatureCollection.json"),
				true,
				false);
	}

	@Test
	public void getFeaturesTestInvalid() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqx",
				HttpStatus.OK.value(),
				null,
				null,
				null,
				null,
				false,
				false);
	}

	//Object Testing Catchment
	@Test
	public void getComidTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/comid/13294288",
				HttpStatus.OK.value(),
				null,
				null,
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER, "comid_13294288.json"),
				true,
				false);
	}

	//Linked Object Testing WQP
	@Test
	@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
	public void getWqpTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880",
				HttpStatus.OK.value(),
				null,
				null,
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER_WQP, "wqp_USGS-05427880.json"),
				true,
				false);
	}

	//Linked Object Testing huc12pp
	@Test
	@DatabaseSetup("classpath:/testData/nldi_data/feature/huc12pp.xml")
	public void gethuc12ppTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/huc12pp/070900020604",
				HttpStatus.OK.value(),
				null,
				null,
				BaseController.MIME_TYPE_GEOJSON,
				getCompareFile(RESULT_FOLDER_HUC, "huc12pp_070900020604.json"),
				true,
				false);
	}

	//Navigation Types Testing
	@Test
	@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
	public void getNavigationTypesTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigation",
				HttpStatus.OK.value(),
				null,
				null,
				MediaType.APPLICATION_JSON_VALUE,
				getCompareFile(RESULT_FOLDER, "wqp_USGS-05427880.json"),
				true,
				false);
	}

	@Test
	public void getNavigationTypesNotFoundTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqx/USGS-05427880/navigation",
				HttpStatus.NOT_FOUND.value(),
				null,
				null,
				MediaType.APPLICATION_JSON_VALUE,
				null,
				true,
				false);

		assertEntity(restTemplate,
				"/linked-data/wqp/USGX-05427880/navigation",
				HttpStatus.NOT_FOUND.value(),
				null,
				null,
				MediaType.APPLICATION_JSON_VALUE,
				null,
				true,
				false);
	}


	//Navigation Types Testing
	@Test
	@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
	public void getNavigationOptionsTest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigation/UT?f=json",
				HttpStatus.OK.value(),
				null,
				null,
				MediaType.APPLICATION_JSON_VALUE,
				getCompareFile(RESULT_FOLDER, "navigation.json"),
				true,
				false);
	}

	@Test
	@DatabaseSetup("classpath:/testData/nldi_data/feature/wqp.xml")
	public void getNavigationOptionsTestBadRequest() throws Exception {
		assertEntity(restTemplate,
				"/linked-data/wqp/USGS-05427880/navigation/XX",
				HttpStatus.BAD_REQUEST.value(),
				null,
				null,
				null,
				null,
				false,
				false);
	}

}
