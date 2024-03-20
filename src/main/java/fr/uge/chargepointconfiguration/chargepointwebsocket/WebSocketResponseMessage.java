package fr.uge.chargepointconfiguration.chargepointwebsocket;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.MessageType;
import java.util.Objects;

/**
 * Defines the web socket response message.<br>
 * It is sent after a {@link WebSocketRequestMessage}.
 *
 * @param callType  The type of call defined by {@link MessageType}.
 * @param messageId The id of the request message received beforehand.
 * @param data      The data given by the message, it is in Json format.
 */
public record WebSocketResponseMessage(int callType, long messageId, String data)
    implements WebSocketMessage {

  /**
   * {@link WebSocketResponseMessage}'s constructor.
   *
   * @param callType  The type of call defined by {@link MessageType}.
   * @param messageId The id of the current message.
   * @param data      The data of the message.
   */
  public WebSocketResponseMessage {
    Objects.requireNonNull(data);
  }

  @Override
  public boolean isRequest() {
    return false;
  }

  @Override
  public String toString() {
    return "[" + callType + ",\"" + messageId + "\"," + data + "]";
  }
}
