package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessageResponse;

/**
 * An empty packet sent to the server to acknowledge the {@link UpdateFirmwareRequest16}.
 */
public record UpdateFirmwareResponse16() implements OcppMessageResponse {}
