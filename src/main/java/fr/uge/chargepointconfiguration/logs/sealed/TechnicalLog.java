package fr.uge.chargepointconfiguration.logs.sealed;

import fr.uge.chargepointconfiguration.logs.CustomLogger;

/**
 * Record used in {@link CustomLogger} for {@link TechnicalLogEntity}.
 *
 * @param component {@link TechnicalLogEntity.Component}
 * @param completeLog All the log in a String.
 */
public record TechnicalLog(TechnicalLogEntity.Component component, String completeLog)
    implements Log {}
