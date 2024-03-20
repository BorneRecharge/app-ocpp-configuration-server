package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp;

/**
 * Interface used to define an OCPP message.
 */
public interface OcppMessage {

  /**
   * Tells if the given message is a REQUEST or a RESPONSE.<br>
   * It is useful in sense where the two types are not formatted the same.
   *
   * @param ocppMessage {@link OcppMessage}.
   * @return {@link MessageType}.
   */
  static MessageType ocppMessageToMessageType(OcppMessage ocppMessage) {
    return switch (ocppMessage) {
      case OcppMessageRequest ignored -> MessageType.REQUEST;
      case OcppMessageResponse ignored -> MessageType.RESPONSE;
      default -> MessageType.UNKNOWN;
    };
  }
}
