package fr.insee.oascache.metadata;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
* Utilities to support Swagger encoding formats in Feign.
*/
public final class EncodingUtils {

  /**
   * Private constructor. Do not construct this class.
   */
  private EncodingUtils() {}

  private static Object join(String[] stringArray, String separator) {
    return Arrays.stream(stringArray).reduce((s1,s2)->s1+separator+s2);
  }

  /**
   * URL encode a single query parameter.
   * @param parameter The query parameter to encode. This object will not be
   *                  changed.
   * @return The URL encoded string representation of the parameter. If the
   *         parameter is null, returns null.
   */
  public static String encode(Object parameter) {
    if (parameter == null) {
      return null;
    }
      return URLEncoder.encode(parameter.toString(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
  }
}
