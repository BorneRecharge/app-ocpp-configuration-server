package fr.uge.chargepointconfiguration.firmware;

import fr.uge.chargepointconfiguration.typeallowed.TypeAllowedDto;
import java.util.Set;

/**
 * DTO to read firmware in database.
 *
 * @param  id Database id of the firmware stored.
 * @param url Link where is the firmware stored.
 * @param version Firmware reference version.
 * @param constructor Manufacturer of this firmware.
 * @param typesAllowed Set of all the compatible firmware.
 */
public record FirmwareDto(
    int id, String url, String version, String constructor, Set<TypeAllowedDto> typesAllowed) {}
