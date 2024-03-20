package fr.uge.chargepointconfiguration.logs.technical;

import fr.uge.chargepointconfiguration.logs.sealed.TechnicalLogEntity;
import java.sql.Timestamp;
import org.apache.logging.log4j.Level;

/**
 * DTO to read technical log in database.
 *
 * @param id Database id of the log stored.
 * @param date Date when the log had been created.
 * @param component {@link TechnicalLogEntity.Component}
 * @param level {@link Level}
 * @param completeLog All the log in one String.
 */
public record TechnicalLogDto(
    int id,
    Timestamp date,
    TechnicalLogEntity.Component component,
    String level,
    String completeLog) {}
