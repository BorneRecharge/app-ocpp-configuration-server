package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessageResponse;

/**
 * The response to the {@link UpdateFirmwareRequest20}.<br>
 * It is used to respond to this request by accepting or rejecting the firmware.
 * There is also errors for the status such as :<br>
 * - InvalidCertificate ;<br>
 * - RevokedCertificate.
 *
 * @param status This field indicates whether the Charging station
 *               was able to accept the request.
 */
public record UpdateFirmwareResponse20(String status) implements OcppMessageResponse {}
