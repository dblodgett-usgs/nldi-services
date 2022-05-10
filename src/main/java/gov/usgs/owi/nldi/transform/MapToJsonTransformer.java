package gov.usgs.owi.nldi.transform;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MapToJsonTransformer implements ITransformer {
  private static final Logger LOG = LoggerFactory.getLogger(MapToJsonTransformer.class);

  private OutputStream target;
  private JsonFactory jsonFactory;
  private HttpServletResponse response;
  private boolean firstRow = true; // thread local??

  // package instead of private for testing
  JsonGenerator jsonGenerator;

  /** gets called only once, on the first row */
  abstract void initJson(JsonGenerator jsonGenerator, Map<String, Object> resultMap);

  /** gets called multiple times, once per row */
  abstract void writeMap(JsonGenerator jsonGenerator, Map<String, Object> resultMap);

  public MapToJsonTransformer(HttpServletResponse response) {
    try {
      this.target = new BufferedOutputStream(response.getOutputStream());
    } catch (IOException e) {
      String msgText = "Unable to get output stream";
      LOG.error(msgText, e);
      throw new RuntimeException(msgText, e);
    }
    this.response = response;
    jsonFactory = new JsonFactory();
    try {
      jsonGenerator = jsonFactory.createGenerator(target);
    } catch (IOException e) {
      throw new RuntimeException("Error building json generator", e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(Object result) {
    if (null == result) {
      return;
    }

    if (result instanceof Map) {
      Map<String, Object> resultMap = (Map<String, Object>) result;
      if (firstRow) {
        initJson(jsonGenerator, resultMap);
        firstRow = false;
      }
      writeMap(jsonGenerator, resultMap);
    }

    try {
      target.flush();
    } catch (IOException e) {
      throw new RuntimeException("Error flushing OutputStream", e);
    }
  }

  /** output the closing tags and flush the stream. */
  @Override
  public void end() {
    try {
      if (null != jsonGenerator) {
        jsonGenerator.close();
      }
      target.flush();
    } catch (IOException e) {
      throw new RuntimeException("Error ending json document", e);
    }
  }

  @Override
  public void close() throws Exception {
    // do nothing, just like OutputStream
  }

  /**
   * Returns the toString of the object for the given key, or empty string if the object is not in
   * the map, or is null
   */
  protected String getValue(Map<String, Object> resultMap, String key) {
    if (resultMap.containsKey(key) && null != resultMap.get(key)) {
      return resultMap.get(key).toString();
    } else {
      return "";
    }
  }
}
