package fr.uge.chargepointconfiguration.firmware;

import fr.uge.chargepointconfiguration.typeallowed.TypeAllowedDto;
import java.util.List;

/**
 * DTO to create a firmware in database.
 *
 * @param url          Firmware FTP download link.
 * @param version      Firmware reference version.
 * @param constructor  Manufacturer of this firmware.
 * @param typesAllowed Set of all the compatible firmware.
 */
public record CreateFirmwareDto(
    String url, String version, String constructor, List<TypeAllowedDto> typesAllowed) {}
