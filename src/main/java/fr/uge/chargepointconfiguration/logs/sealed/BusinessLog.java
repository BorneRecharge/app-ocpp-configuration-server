package fr.uge.chargepointconfiguration.logs.sealed;

import fr.uge.chargepointconfiguration.chargepoint.Chargepoint;
import fr.uge.chargepointconfiguration.logs.CustomLogger;
import fr.uge.chargepointconfiguration.user.User;

/**
 * Record used in {@link CustomLogger} for {@link BusinessLogEntity}.
 *
 * @param user User logged currently implied with this log, null if not.
 * @param chargepoint Chargepoint implied with this log, null if not.
 * @param category {@link TechnicalLogEntity.Component}
 * @param completeLog All the log in a String.
 */
public record BusinessLog(
    User user, Chargepoint chargepoint, BusinessLogEntity.Category category, String completeLog)
    implements Log {}
