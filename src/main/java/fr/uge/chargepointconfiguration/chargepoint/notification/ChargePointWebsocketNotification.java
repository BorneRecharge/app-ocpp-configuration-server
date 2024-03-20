package fr.uge.chargepointconfiguration.chargepoint.notification;

import fr.uge.chargepointconfiguration.status.StatusDto;

/**
 * Charge point update websocket notification.
 */
public record ChargePointWebsocketNotification(int id, StatusDto status)
    implements WebSocketNotification {}
