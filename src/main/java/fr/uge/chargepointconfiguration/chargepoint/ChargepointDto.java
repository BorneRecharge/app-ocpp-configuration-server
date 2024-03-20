package fr.uge.chargepointconfiguration.chargepoint;

import fr.uge.chargepointconfiguration.configuration.ConfigurationDto;
import fr.uge.chargepointconfiguration.status.StatusDto;

/**
 * DTO to read chargepoint in database.
 *
 * @param id Database id of the chargepoint stored.
 * @param serialNumberChargepoint The chargepoint's unique serial id.
 * @param type The commercial name of the chargepoint.
 * @param constructor The chargepoint's manufacturer.
 * @param clientId The client's name of the chargepoint.
 * @param configuration A JSON containing the chargepoint's configuration.
 * @param status {@link StatusDto}.
 */
public record ChargepointDto(
    int id,
    String serialNumberChargepoint,
    String type,
    String constructor,
    String clientId,
    ConfigurationDto configuration,
    StatusDto status) {}
