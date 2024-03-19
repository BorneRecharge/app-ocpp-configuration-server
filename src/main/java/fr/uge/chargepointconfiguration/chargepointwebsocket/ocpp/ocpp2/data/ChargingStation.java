package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2.data;

/**
 * Data for the OCPP 2.0 BootNotification.<br>
 * It represents a JSON with the chargepoint information.
 *
 * @param model String.
 * @param vendorName String.
 * @param serialNumber String.
 * @param firmwareVersion String.
 */
public record ChargingStation(String model,
                              String vendorName,
                              String serialNumber,
                              String firmwareVersion,
                              Modem modem) {
}
