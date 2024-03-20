package fr.uge.chargepointconfiguration.status;

import fr.uge.chargepointconfiguration.chargepoint.Chargepoint;
import java.sql.Timestamp;

/**
 * DTO to read status in database.
 *
 * @param lastUpdate Last time this chargepoint status has been changed.
 * @param error Show the last chargepoint error's log.
 * @param state Represent the state of connection of the chargepoint.
 * @param step The mods which a machine can be.
 * @param status The status of the process.<br>
 */
public record StatusDto(
    Timestamp lastUpdate,
    String error,
    boolean state,
    Chargepoint.Step step,
    Chargepoint.StatusProcess status) {}
