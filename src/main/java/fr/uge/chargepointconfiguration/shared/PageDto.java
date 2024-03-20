package fr.uge.chargepointconfiguration.shared;

import java.util.List;
import java.util.Objects;

/**
 * A record to represent a page of data.
 *
 * @param total total amount of element
 * @param page which page is displayed
 * @param size asked size of the page (not necessarily effective size)
 * @param data list of T containing the actual data
 *
 * @param <T> The type of data it is containing
 */
public record PageDto<T>(long total, long totalElement, int page, int size, List<T> data) {

  /**
   * Default constructor for a page containing a list of T.
   *
   * @param total total amount of element
   * @param page which page is displayed
   * @param size asked size of the page (not necessarily effective size)
   * @param data list of T containing the actual data
   */
  public PageDto {
    Objects.requireNonNull(data);
    if (total < 0 || page < 0 || size < 0) {
      throw new IllegalArgumentException("Illegal negative value.");
    }
  }
}
