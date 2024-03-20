package fr.uge.chargepointconfiguration.chargepoint.notification;

/**
 * A message sent for the toast in the front.
 *
 * @param content {@link CriticalityWebsocketContent}.
 */
public record CriticalityWebsocketNotification(String title, Type type, String content)
    implements WebSocketNotification {

  /**
   * Criticality of the toast.<br>
   * It can be :<br>
   * - ERROR, an error ;<br>
   * - INFO, an information.
   */
  public enum Type {
    ERROR,
    INFO,
    SUCCESS
  }
}
