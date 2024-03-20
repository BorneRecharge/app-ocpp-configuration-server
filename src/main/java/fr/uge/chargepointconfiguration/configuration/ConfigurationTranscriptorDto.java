package fr.uge.chargepointconfiguration.configuration;

/**
 * DTO to display ConfigurationTranscription in front page.
 *
 * @param id Database id of the configuration stored.
 * @param fullName Adequate description of the technical key used for chargepoint configuration.
 * @param regex The regex used to validate the value.
 */
public record ConfigurationTranscriptorDto(int id, String fullName, String regex) {}
