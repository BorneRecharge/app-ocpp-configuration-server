package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessageResponse;

/**
 * Empty packet sent by the server to acknowledge the {@link FirmwareStatusNotificationRequest16}.
 */
public record FirmwareStatusNotificationResponse16() implements OcppMessageResponse {}
