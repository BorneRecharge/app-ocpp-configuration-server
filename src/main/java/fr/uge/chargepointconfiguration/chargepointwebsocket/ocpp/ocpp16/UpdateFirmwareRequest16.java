package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import static fr.uge.chargepointconfiguration.tools.JsonParser.OCPP_RFC3339_DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessageRequest;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents the Update Firmware packet from OCPP 1.6.<br>
 * It should be answered by a {@link UpdateFirmwareResponse16}.
 *
 * @param location The URL of the firmware, it will be used by the machine
 *                 to download the firmware.
 * @param retrieveDate The date which the machine will download the firmware.
 */
public record UpdateFirmwareRequest16(
    String location,
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = OCPP_RFC3339_DATE_FORMAT,
            timezone = "UTC")
        Instant retrieveDate)
    implements OcppMessageRequest {

  /**
   * {@link UpdateFirmwareRequest16}'s constructor.
   *
   * @param location The URL of the firmware, it will be used by the machine
   *                 to download the firmware.
   * @param retrieveDate The date which the machine will download the firmware.
   */
  public UpdateFirmwareRequest16 {
    Objects.requireNonNull(location);
    Objects.requireNonNull(retrieveDate);
  }
}
