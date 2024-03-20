package fr.uge.chargepointconfiguration.typeallowed;

/**
 * DTO to read TypeAllowed in database.
 *
 * @param id Database id of the typealled stored.
 * @param constructor A chargepoint's manufacturer where a firmware is working on.
 * @param type The commercial name of a chargepoint where a firmware is working on.
 */
public record TypeAllowedDto(int id, String constructor, String type) {}
