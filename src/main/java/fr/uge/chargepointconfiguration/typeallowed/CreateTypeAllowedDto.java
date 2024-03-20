package fr.uge.chargepointconfiguration.typeallowed;

/**
 * DTO to create a {@link TypeAllowed} in database.
 *
 * @param constructor A chargepoint's manufacturer where a firmware is working on.
 * @param type The commercial name of a chargepoint where a firmware is working on.
 */
public record CreateTypeAllowedDto(String constructor, String type) {}
