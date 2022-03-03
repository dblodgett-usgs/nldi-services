package gov.usgs.owi.nldi.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

import gov.usgs.owi.nldi.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import org.springframework.web.server.ResponseStatusException;

public class NetworkControllerTest {

	private StreamingDao streamingDao;
	@Mock
	private LookupDao lookupDao;
	@Mock
	private Navigation navigation;
	@Mock
	private Parameters parameters;
	@Mock
	private LogService logService;
	@Mock
	private PyGeoApiService pygeoapiService;

	private TestConfigurationService configurationService;
	private NetworkController controller;
	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		configurationService = new TestConfigurationService();
		controller = new NetworkController(lookupDao, streamingDao, navigation, parameters, configurationService, logService, pygeoapiService);
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();

		when(logService.logRequest(any(HttpServletRequest.class))).thenReturn(BigInteger.ONE);
	}

	@Test
	public void getFlowlinesTest() throws Exception {
		try {
			controller.getFlowlines(request, response, "13297246", "PP", "13297198", null, null);
			fail("should have failed");
		} catch (ResponseStatusException rse) {
			//good
		} catch (Throwable t) {
			fail(t);
		}
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), eq(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	public void getFeaturesTest() throws Exception {
		controller.getFeatures(request, response, null, null, null, null, null, null);
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
		//this is a INTERNAL_SERVER_ERROR because of NPEs that shouldn't happen in real life.
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
	}

	@Test
	public void getBasinTest() throws Exception {
		controller.getFeatures(request, response, null, null, BaseDao.BASIN, null, null, null);
		verify(logService).logRequest(any(HttpServletRequest.class));
		verify(logService).logRequestComplete(any(BigInteger.class), any(int.class));
		//this is a INTERNAL_SERVER_ERROR because of NPEs that shouldn't happen in real life.
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
	}

}
