package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessageRequest;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.FirmwareStatus;
import java.util.Objects;

/**
 * Packet sent by the machine to describe the current state of the firmware download.<br>
 * The server should respond by a {@link FirmwareStatusNotificationResponse16}.
 *
 * @param status {@link FirmwareStatus}.
 */
public record FirmwareStatusNotificationRequest16(FirmwareStatus status)
    implements OcppMessageRequest {

  /**
   * {@link FirmwareStatusNotificationRequest16}'s constructor.
   *
   * @param status {@link FirmwareStatus}.
   */
  public FirmwareStatusNotificationRequest16 {
    Objects.requireNonNull(status);
  }
}
