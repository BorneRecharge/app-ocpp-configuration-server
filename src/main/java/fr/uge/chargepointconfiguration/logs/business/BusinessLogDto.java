package fr.uge.chargepointconfiguration.logs.business;

import fr.uge.chargepointconfiguration.chargepoint.ChargepointDto;
import fr.uge.chargepointconfiguration.logs.sealed.BusinessLogEntity;
import fr.uge.chargepointconfiguration.user.UserDto;
import java.sql.Timestamp;

/**
 * DTO to read business log in database.
 *
 * @param id Database id of the log stored.
 * @param date Date when the log had been created.
 * @param user User logged currently implied with this log, null if not.
 * @param chargepoint Chargepoint implied with this log, null if not.
 * @param category {@link BusinessLogEntity.Category}
 * @param completeLog All the log in one String.
 */
public record BusinessLogDto(
    int id,
    Timestamp date,
    UserDto user,
    ChargepointDto chargepoint,
    BusinessLogEntity.Category category,
    String level,
    String completeLog) {}
