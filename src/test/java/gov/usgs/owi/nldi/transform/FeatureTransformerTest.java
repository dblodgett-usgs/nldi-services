package gov.usgs.owi.nldi.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.services.TestConfigurationService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

public class FeatureTransformerTest {

  protected TestConfigurationService configurationService;
  protected FeatureTransformer transformer;
  protected MockHttpServletResponse response;

  @BeforeEach
  public void beforeTest() throws IOException {
    configurationService = new TestConfigurationService();
    response = new MockHttpServletResponse();
    transformer = new FeatureTransformer(response, configurationService);
  }

  @AfterEach
  public void afterTest() throws Exception {
    transformer.close();
  }

  @Test
  public void constructorTest() {
    boolean threwRuntimeEx = false;
    try {
      new FeatureTransformer(null, configurationService);

    } catch (RuntimeException re) {
      threwRuntimeEx = true;
    }
    assertTrue(threwRuntimeEx);
  }

  @Test
  public void writePropertiesTest() {
    Map<String, Object> map = new HashMap<>();
    map.put(FlowLineTransformer.NHDPLUS_COMID, "13293474");
    map.put(FeatureTransformer.COMID, "47439231");
    map.put(FeatureTransformer.IDENTIFIER, "identifierValue");
    map.put(FeatureTransformer.NAME, "nameValue");
    map.put(FeatureTransformer.URI, "uriValue");
    map.put(LookupDao.SOURCE, "sourceValue");
    map.put(FeatureTransformer.SOURCE_NAME_DB, "sourceNameValue");
    map.put(FeatureTransformer.REACHCODE, "05020002004263");
    map.put(FeatureTransformer.MEASURE, 1.3823300000);
    map.put(FeatureTransformer.FEATURE_TYPE_DB, "typeValue");
    try {
      transformer.jsonGenerator.writeStartObject();
      transformer.writeProperties(transformer.jsonGenerator, map);
      transformer.jsonGenerator.writeEndObject();
      // need to flush the JsonGenerator to get at output.
      transformer.jsonGenerator.flush();
      assertEquals(
          "{\"type\":\"typeValue\",\"source\":\"sourceValue\",\"sourceName\":\"sourceNameValue\",\"identifier\":\"identifierValue\",\"name\":\"nameValue\","
              + "\"uri\":\"uriValue\",\"comid\":\"47439231\",\"reachcode\":\"05020002004263\",\"measure\":\"1.38233\","
              + "\"navigation\":\""
              + configurationService.getLinkedDataUrl()
              + "/sourcevalue/identifierValue/navigation\"}",
          response.getContentAsString());
    } catch (IOException e) {
      fail(e.getLocalizedMessage());
    }
  }

  @Test
  public void writePropertiesTestBad() {
    Map<String, Object> map = new HashMap<>();
    map.put(FlowLineTransformer.NHDPLUS_COMID, "13293474");
    map.put(FeatureTransformer.COMID, "47439231");
    map.put(FeatureTransformer.IDENTIFIER, "identifierValue");
    map.put(FeatureTransformer.NAME, "nameValue");
    map.put(FeatureTransformer.URI, "uriValue");
    map.put(LookupDao.SOURCE, "sourceValue");
    map.put(FeatureTransformer.SOURCE_NAME_DB, "sourceNameValue");
    map.put(FeatureTransformer.REACHCODE, "05020002004263");
    map.put(FeatureTransformer.MEASURE, 1.3823300000);
    boolean threwRuntimeEx = false;
    try {
      transformer.jsonGenerator.writeStartObject();
      transformer.writeProperties(transformer.jsonGenerator, null);
      transformer.jsonGenerator.writeEndObject();
      // need to flush the JsonGenerator to get at output.
      transformer.jsonGenerator.flush();
      fail("should have thrown runtime exception");
    } catch (RuntimeException e) {
      threwRuntimeEx = true;
    } catch (Throwable t) {
      fail(t.getMessage());
    }
    assertTrue(threwRuntimeEx);
  }

  @Test
  public void testWritePropertiesNoReachNoMeasure() {
    Map<String, Object> map = new HashMap<>();
    map.put(FlowLineTransformer.NHDPLUS_COMID, "13294118");
    map.put(FeatureTransformer.COMID, "81149213");
    map.put(FeatureTransformer.IDENTIFIER, "identifier2Value");
    map.put(FeatureTransformer.NAME, "name2Value");
    map.put(FeatureTransformer.URI, "uri2Value");
    map.put(LookupDao.SOURCE, "source2Value");
    map.put(FeatureTransformer.SOURCE_NAME_DB, "sourceName2Value");
    map.put(FeatureTransformer.FEATURE_TYPE_DB, "type2Value");

    try {
      transformer.jsonGenerator.writeStartObject();
      transformer.writeProperties(transformer.jsonGenerator, map);
      transformer.jsonGenerator.writeEndObject();
      // need to flush the JsonGenerator to get at output.
      transformer.jsonGenerator.flush();
      assertEquals(
          "{\"type\":\"type2Value\",\"source\":\"source2Value\",\"sourceName\":\"sourceName2Value\",\"identifier\":\"identifier2Value\",\"name\":\"name2Value\","
              + "\"uri\":\"uri2Value\",\"comid\":\"81149213\",\"navigation\":\""
              + configurationService.getLinkedDataUrl()
              + "/source2value/identifier2Value/navigation\"}",
          response.getContentAsString());
    } catch (IOException e) {
      fail(e.getLocalizedMessage());
    }
  }
}
