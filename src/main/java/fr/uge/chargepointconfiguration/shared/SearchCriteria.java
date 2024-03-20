package fr.uge.chargepointconfiguration.shared;

/**
 * A record to represent a criteria for a filter.
 *
 * @param key the name of the key to be tested
 * @param operation the {@link Operation} to test
 * @param value the value to be tested with the operation
 */
public record SearchCriteria(String key, Operation operation, Object value) {

  /**
   * Operations that can be performed for the tests of the criterias.
   */
  public enum Operation {
    MORE_THAN,
    LESS_THAN,
    CONTAINS,
    UNKNOWN;

    /**
     * Get an {@link Operation} from a string.<br>
     * Can be "<", ">" or ":"
     *
     * @param string the given string
     * @return The corresponding operation, UNKNOWN if unknown
     */
    public static Operation fromString(String string) {
      return switch (string) {
        case "<" -> LESS_THAN;
        case ">" -> MORE_THAN;
        case ":" -> CONTAINS;
        default -> UNKNOWN;
      };
    }
  }
}
