package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp;

/**
 * Represents the message's type.<br>
 * - 2 if it is a request ;<br>
 * - 3 if it is a response ;<br>
 * - 99 if it is unknown.
 */
public enum MessageType {
  REQUEST(2),
  RESPONSE(3),
  UNKNOWN(99);

  private final int callType;

  MessageType(int callType) {
    this.callType = callType;
  }

  /**
   * Returns the call type of the current enum.
   *
   * @return The current enum's call type.
   */
  public int getCallType() {
    return callType;
  }

  /**
   * By giving the callType, this method returns the correct enum.
   *
   * @param callType The message's call type.
   * @return The enum according to the call type.
   */
  public static MessageType codeToEnum(int callType) {
    return switch (callType) {
      case 2 -> REQUEST;
      case 3 -> RESPONSE;
      default -> UNKNOWN;
    };
  }
}
