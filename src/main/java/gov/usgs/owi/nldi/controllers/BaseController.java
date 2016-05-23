package gov.usgs.owi.nldi.controllers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;
import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.CountDao;
import gov.usgs.owi.nldi.dao.NavigationDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.dao.StreamingResultHandler;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.transform.FeatureTransformer;
import gov.usgs.owi.nldi.transform.FlowLineTransformer;
import gov.usgs.owi.nldi.transform.ITransformer;

public abstract class BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

	public static final String DATA_SOURCE = "dataSource";

	public static final String NAVIGATE = NavigationDao.NAVIGATE;
	public static final String SESSION_ID = "sessionId";
	public static final String COUNT_SUFFIX = "_count";

	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String MIME_TYPE_GEOJSON = "application/vnd.geo+json";
	public static final String FEATURE_COUNT_HEADER = BaseDao.FEATURES + COUNT_SUFFIX;
	public static final String FLOW_LINES_COUNT_HEADER = BaseDao.FLOW_LINES + COUNT_SUFFIX;


	protected final CountDao countDao;
	protected final StreamingDao streamingDao;
	protected final Navigation navigation;
	protected final String rootUrl;

	private final KeyLockManager lockManager = KeyLockManagers.newLock();

	public BaseController(CountDao inCountDao, StreamingDao inStreamingDao, Navigation inNavigation, String inRootUrl) {
		countDao = inCountDao;
		streamingDao = inStreamingDao;
		navigation = inNavigation;
		rootUrl = inRootUrl;
	}

	protected void streamFlowLines(HttpServletResponse response,
			String comid, String navigationMode, String stopComid, String distance) {

		OutputStream responseStream = null;
		try {
			responseStream = new BufferedOutputStream(response.getOutputStream());
			String sessionId = getSessionId(responseStream, comid, navigationMode, distance, stopComid);
			if (null != sessionId) {
				Map<String, Object> parameterMap = new HashMap<> ();
				parameterMap.put(SESSION_ID, sessionId);
				addHeaders(response, BaseDao.FLOW_LINES, parameterMap);
				streamResults(new FlowLineTransformer(responseStream, rootUrl), BaseDao.FLOW_LINES, parameterMap);
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
			}

		} catch (Throwable e) {
			LOG.error("Handle me better" + e.getLocalizedMessage(), e);
		} finally {
			if (null != responseStream) {
				try {
					responseStream.flush();
				} catch (Throwable e) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error flushing response stream", e);
				}
			}
		}
	}

	protected void streamFeatures(HttpServletResponse response,
			String comid, String navigationMode, String stopComid, String distance, String dataSource) {

		OutputStream responseStream = null;
		try {
			responseStream = new BufferedOutputStream(response.getOutputStream());
			String sessionId = getSessionId(responseStream, comid, navigationMode, distance, stopComid);
			if (null != sessionId) {
				Map<String, Object> parameterMap = new HashMap<> ();
				parameterMap.put(SESSION_ID, sessionId);
				parameterMap.put(DATA_SOURCE, dataSource.toLowerCase());
				addHeaders(response, BaseDao.FEATURES, parameterMap);
				streamResults(new FeatureTransformer(responseStream, rootUrl), BaseDao.FEATURES, parameterMap);
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
			}
	
		} catch (Exception e) {
			LOG.error("Handle me better" + e.getLocalizedMessage(), e);
		} finally {
			if (null != responseStream) {
				try {
					responseStream.flush();
				} catch (IOException e) {
					//Just log, cause we obviously can't tell the client
					LOG.error("Error flushing response stream", e);
				}
			}
		}
	}

	protected void streamResults(ITransformer transformer, String featureType, Map<String, Object> parameterMap) {
		LOG.trace("start streaming");
		ResultHandler<?> handler = new StreamingResultHandler(transformer);
		streamingDao.stream(featureType, parameterMap, handler);
		transformer.end();
		LOG.trace("done streaming");
	}

	protected void addContentHeader(HttpServletResponse response) {
		response.setHeader(HEADER_CONTENT_TYPE, MIME_TYPE_GEOJSON);
	}

	protected void addHeaders(HttpServletResponse response, String featureType, Map<String, Object> parameterMap) {
		LOG.trace("entering addHeaders");

		addContentHeader(response);
		response.setHeader(featureType + COUNT_SUFFIX, countDao.count(featureType, parameterMap));

		LOG.trace("leaving addHeaders");
	}

	protected String getSessionId(OutputStream responseStream, final String comid, final String navigationMode,
			final String distance, final String stopComid) {
		String key = String.join("|", comid, navigationMode, distance, stopComid);
		return lockManager.executeLocked(key, () -> navigation.navigate(responseStream, comid, navigationMode, distance, stopComid));
	}

}
