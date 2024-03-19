package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessageResponse;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.RegistrationStatus;

import java.time.Instant;
import java.util.Objects;

import static fr.uge.chargepointconfiguration.tools.JsonParser.OCPP_RFC3339_DATE_FORMAT;

/**
 * Defines a response to the BootNotificationRequest.<br>
 * See this as an ACK packet.
 *
 * @param currentTime The current server time of the server in String.
 * @param interval This value defines the interval in second that the remote should respect
 *                 before sending a HeartBeat packet (which is like a PING).
 * @param status This enum serves as a response for the message. If it is accepted,
 *               rejected or something else.
 */
public record BootNotificationResponse16(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OCPP_RFC3339_DATE_FORMAT, timezone = "UTC")
        Instant currentTime,
        int interval,
        RegistrationStatus status) implements OcppMessageResponse {

  /**
   * {@link BootNotificationResponse16}'s constructor.
   *
   * @param currentTime The current server time of the server in String.
   * @param interval This value defines the interval in second that the remote should respect
   *                 before sending a HeartBeat packet (which is like a PING).
   * @param status This enum serves as a response for the message. If it is accepted,
   *               rejected or something else.
   */
  public BootNotificationResponse16 {
    Objects.requireNonNull(currentTime);
    Objects.requireNonNull(status);
  }
}
