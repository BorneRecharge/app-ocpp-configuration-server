package fr.uge.chargepointconfiguration.chargepointwebsocket;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.MessageType;
import java.util.Objects;

/**
 * Defines the web socket request message.<br>
 * It is sent before a {@link WebSocketResponseMessage}.
 *
 * @param callType    The type of call defined by {@link MessageType}.
 * @param messageId   The message's id.
 * @param messageName {@link MessageTypeRequest}.
 * @param data        The data given by the message, it is in Json format.
 */
public record WebSocketRequestMessage(
    int callType, long messageId, WebSocketMessage.MessageTypeRequest messageName, String data)
    implements WebSocketMessage {

  /**
   * {@link WebSocketRequestMessage}'s constructor.
   *
   * @param callType    The type of call defined by {@link MessageType}.
   * @param messageId   The message's id.
   * @param messageName {@link MessageTypeRequest}.
   * @param data        The data given by the message, it is in Json format.
   */
  public WebSocketRequestMessage {
    Objects.requireNonNull(messageName);
    Objects.requireNonNull(data);
  }

  @Override
  public boolean isRequest() {
    return true;
  }

  @Override
  public String toString() {
    return "[" + callType + ",\"" + messageId + "\",\"" + messageName.getName() + "\"," + data
        + "]";
  }
}
