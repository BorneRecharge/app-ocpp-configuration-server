package fr.uge.chargepointconfiguration;

import fr.uge.chargepointconfiguration.chargepoint.notification.Notification;
import fr.uge.chargepointconfiguration.tools.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Define the handler which manage clients websocket connection.
 */
public class FrontWebSocketHandler extends TextWebSocketHandler {
  private static final Object lock = new Object();
  private static final ArrayList<WebSocketSession> usersSession = new ArrayList<>();

  /**
   * Call after a client websocket connection.
   *
   * @param session client websocket session
   */
  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    // Perform actions when a new WebSocket connection is established
    synchronized (lock) {
      usersSession.add(session);
    }
  }

  /**
   * Call when a session closed the websocket connection.
   *
   * @param session websocket user session
   * @param status  connection status
   * @throws Exception throw
   */
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    synchronized (lock) {
      usersSession.remove(session);
    }
  }

  /**
   * Send a text message to all clients connected to the websocket server.
   *
   * @param notificationMessage Websocket notification message
   */
  public static void sendMessageToUsers(Notification notificationMessage) {
    var textMessage = new TextMessage(JsonParser.objectToJsonString(notificationMessage));
    synchronized (lock) {
      usersSession.forEach(webSocketSession -> {
        try {
          webSocketSession.sendMessage(textMessage);
        } catch (IOException e) {
          System.out.println("Failed to sent a message to the client: " + e.getMessage());
        }
      });
    }
  }
}
