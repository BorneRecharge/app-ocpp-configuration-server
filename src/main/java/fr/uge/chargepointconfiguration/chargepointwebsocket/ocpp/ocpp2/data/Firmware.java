package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2.data;

/**
 * An object for the UpdateFirmwareRequest.<br>
 * Represents a copy of the firmware that can be loaded/updated
 * on the Charging station.
 *
 * @param location URI defining the origin of the firmware.
 * @param retrieveDateTime Date and time which the firmware shall be retrieved.
 */
public record Firmware(String location, String retrieveDateTime) {}
