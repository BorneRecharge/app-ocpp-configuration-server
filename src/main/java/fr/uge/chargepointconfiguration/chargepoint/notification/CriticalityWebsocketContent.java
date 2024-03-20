package fr.uge.chargepointconfiguration.chargepoint.notification;

/**
 * Content for the {@link CriticalityWebsocketNotification}.
 *
 * @param title The title of the front.
 * @param criticality {@link Type}.
 * @param content The content of the toast.
 */
public record CriticalityWebsocketContent(String title, Type criticality, String content) {

  /**
   * Criticality of the toast.<br>
   * It can be :<br>
   * - ERROR, an error ;<br>
   * - INFO, an information.
   */
  public enum Type {
    ERROR,
    INFO
  }
}
