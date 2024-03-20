package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessageRequest;
import java.util.Objects;

/**
 * Defines the BootNotificationRequest sent by the chargepoint.<br>
 * The server should answer with a {@link BootNotificationResponse16}.
 *
 * @param chargePointVendor The manufacturer of the chargepoint.
 * @param chargePointModel The chargepoint's commercial name.
 * @param chargePointSerialNumber The serial id of the chargepoint.
 * @param chargeBoxSerialNumber The special name of the chargepoint.
 * @param firmwareVersion The firmware version of the chargepoint.
 */
public record BootNotificationRequest16(
    String chargePointVendor,
    String chargePointModel,
    String chargePointSerialNumber,
    String chargeBoxSerialNumber,
    String firmwareVersion)
    implements OcppMessageRequest {

  /**
   * {@link BootNotificationRequest16}'s constructor.
   *
   * @param chargePointVendor The manufacturer of the chargepoint.
   * @param chargePointModel The chargepoint's commercial name.
   * @param chargePointSerialNumber The serial id of the chargepoint.
   * @param chargeBoxSerialNumber The special name of the chargepoint.
   * @param firmwareVersion The firmware version of the chargepoint.
   */
  public BootNotificationRequest16 {
    Objects.requireNonNull(chargePointVendor);
    Objects.requireNonNull(chargePointModel);
    if (chargePointVendor.length() > 20) {
      throw new IllegalArgumentException(
          "Chargepoint vendor name cannot be longer than 20 characters.");
    }
    if (chargePointModel.length() > 20) {
      throw new IllegalArgumentException(
          "Chargepoint model name cannot be longer than 20 characters.");
    }
    if (chargePointSerialNumber != null && chargePointSerialNumber.length() > 25) {
      throw new IllegalArgumentException(
          "Chargepoint serial number cannot be longer than 25 characters.");
    }
    if (chargeBoxSerialNumber != null && chargeBoxSerialNumber.length() > 25) {
      throw new IllegalArgumentException(
          "Chargebox serial number cannot be longer than 25 characters.");
    }
    if (firmwareVersion != null && firmwareVersion.length() > 50) {
      throw new IllegalArgumentException(
          "Chargepoint firmware version cannot be longer than 50 characters.");
    }
  }
}
