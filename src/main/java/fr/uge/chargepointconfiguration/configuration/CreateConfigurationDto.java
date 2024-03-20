package fr.uge.chargepointconfiguration.configuration;

/**
 * DTO to post configuration in database.
 *
 * @param name How you want your configuration to be named.
 * @param description Brieve description of the meaining of this configuration.
 * @param configuration A JSON containing key and values for your configuration.
 */
public record CreateConfigurationDto(
    String name, String description, String configuration, int firmware) {}
