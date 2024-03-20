package fr.uge.chargepointconfiguration.chargepoint;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO to create configuration in database.
 *
 * @param serialNumberChargepoint The chargepoint's unique serial id.
 * @param type                    The commercial name of the chargepoint.
 * @param constructor             The chargepoint's manufacturer.
 * @param clientId                The client's name of the chargepoint.
 * @param configuration           A JSON containing the chargepoint's configuration.
 */
public record CreateChargepointDto(
    @JsonProperty("serialNumber") String serialNumberChargepoint,
    String type,
    String constructor,
    String clientId,
    int configuration) {}
