package fr.uge.chargepointconfiguration.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;

/**
 * Parses a JSON.
 */
public class JsonParser {

  /**
   * Transform object to formatted JSON string.
   *
   * @param object object to transform
   * @param <T>    type of the object to transform
   * @return the string formatted
   */
  public static <T> String objectToJsonString(T object) {
    Objects.requireNonNull(object);
    var mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Unable to parse object to JSON string : " + e.getMessage(), e);
    }
  }

  /**
   * Transform a JSON string to object.
   *
   * @param type    type of the object
   * @param content json string that represent the object
   * @param <T>     type of the object generated
   * @return a new instance of object
   */
  public static <T> T stringToObject(Class<T> type, String content) {
    Objects.requireNonNull(type);
    Objects.requireNonNull(content);
    var mapper = new ObjectMapper();
    try {
      return mapper.readValue(content, type);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Unable to parse JSON string to object : " + e.getMessage(), e);
    }
  }

}

