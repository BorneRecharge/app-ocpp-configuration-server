package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessageRequest;

/**
 * Represents the firmware status notification request to gives the current status of
 * the firmware download or installation.
 *
 * @param status The current state of the firmware download.
 * @param requestId The id of the message that started the update process.
 */
public record FirmwareStatusNotificationRequest20(String status, int requestId)
    implements OcppMessageRequest {}
