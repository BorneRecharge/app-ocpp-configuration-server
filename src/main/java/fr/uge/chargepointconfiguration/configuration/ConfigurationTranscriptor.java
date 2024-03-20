package fr.uge.chargepointconfiguration.configuration;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2.data.Component;
import fr.uge.chargepointconfiguration.firmware.FirmwareKey;
import java.util.Objects;

/**
 * For a full name, we give the correct OCPP 1.6 and OCPP 2.0.1 key (and component).<br>
 * It is used for translating the given configuration file from the database and for the front.
 */
public enum ConfigurationTranscriptor {
  LIGHT_INTENSITY(
      1,
      "Intensité de la LED",
      FirmwareKey.LIGHT_INTENSITY,
      "UNKNOWN",
      new Component("UNKNOWN"),
      "^(100|\\d{1,2})$"),

  NETWORK_PROFILE(
      2, "Adresse du prochain serveur", FirmwareKey.NETWORK_PROFILE, "", new Component(""), ""),

  CHARGEPOINT_IDENTITY(
      3,
      "Nom de la borne",
      FirmwareKey.CHARGEPOINT_IDENTITY,
      "Identity",
      new Component("SecurityCtrlr"),
      "^.{0,20}$"),

  LOCAL_AUTH_LIST(
      4,
      "Activer la liste des badges d'authentification ?",
      FirmwareKey.LOCAL_AUTH_LIST,
      "LocalAuthListEnabled",
      new Component("LocalAuthListCtrlr"),
      ""),

  STATION_MAX_CURRENT(
      5, "Courant max de la borne", FirmwareKey.STATION_MAX_CURRENT, "", new Component(""), ""),

  STATION_PASSWORD(
      6,
      "Changer le mot de passe de la borne",
      FirmwareKey.STATION_PASSWORD,
      "BasicAuthPassword",
      new Component("SecurityCtrlr"),
      "^(?!.*[\\\\\",]).{10,40}$");

  private final int id;
  private final String fullName;
  private final FirmwareKey ocpp16Key;
  private final String ocpp20Key;
  private final Component component;
  private final String regexRule;

  /**
   * Enum's constructor. Should be private and not called outside this enum.
   *
   * @param fullName The name which will be displayed to the user, it should be unique.
   * @param ocpp16Key The configuration key according to the OCPP 1.6 protocol.
   * @param ocpp20Key The configuration key according to the OCPP 2.0.1 protocol.
   * @param component {@link Component}.
   * @param regexRule The rule which a user should respect when giving a value to the given key.
   */
  ConfigurationTranscriptor(
      int id,
      String fullName,
      FirmwareKey ocpp16Key,
      String ocpp20Key,
      Component component,
      String regexRule) {
    this.id = id;
    this.fullName = Objects.requireNonNull(fullName);
    this.ocpp16Key = Objects.requireNonNull(ocpp16Key);
    this.ocpp20Key = Objects.requireNonNull(ocpp20Key);
    this.component = Objects.requireNonNull(component);
    this.regexRule = Objects.requireNonNull(regexRule);
  }

  /**
   * Returns the configuration's id.<br>
   * It is used to quickly find the corresponding configuration from the database.
   *
   * @return The configuration's id.
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the full name of a given key configuration.<br>
   * This value is used to be shown to the user.
   *
   * @return The configuration full name.
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * Returns the configuration's key according to the OCPP 1.6 protocol.
   *
   * @return The configuration's key.
   */
  public FirmwareKey getOcpp16Key() {
    return ocpp16Key;
  }

  /**
   * Returns the configuration's key according to the OCPP 2.0.1 protocol.
   *
   * @return The configuration's key.
   */
  public String getOcpp20Key() {
    return ocpp20Key;
  }

  /**
   * Returns the configuration's {@link Component}.
   *
   * @return {@link Component}.
   */
  public Component getComponent() {
    return component;
  }

  /**
   * Returns the configuration's regex rule for the value.
   *
   * @return The regex rule which the user should respect when giving a value.
   */
  public String getRegexRule() {
    return regexRule;
  }

  /**
   * Searches the correct enum with the given full name.
   *
   * @param id Configuration's id.
   * @return {@link ConfigurationTranscriptor}.
   */
  public static ConfigurationTranscriptor idToEnum(int id) {
    return switch (id) {
      case 1 -> LIGHT_INTENSITY;
      case 2 -> NETWORK_PROFILE;
      case 3 -> CHARGEPOINT_IDENTITY;
      case 4 -> LOCAL_AUTH_LIST;
      case 5 -> STATION_MAX_CURRENT;
      case 6 -> STATION_PASSWORD;
      default -> throw new IllegalStateException("Unknown key");
    };
  }

  public ConfigurationTranscriptorDto toDto() {
    return new ConfigurationTranscriptorDto(id, fullName, regexRule);
  }
}
