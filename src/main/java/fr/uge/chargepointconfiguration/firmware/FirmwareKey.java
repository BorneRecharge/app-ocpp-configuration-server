package fr.uge.chargepointconfiguration.firmware;

import java.util.Objects;

/**
 * Depending on the firmware version, a key can have a different name.
 */
public enum FirmwareKey {
  LIGHT_INTENSITY("LightIntensity", "LightIntensity", "LightIntensity"),

  NETWORK_PROFILE("BackOffice-URL-wired", "BackOffice-URL-wired", "BackOfficeNetworkProfile1"),

  CHARGEPOINT_IDENTITY("Identity", "Identity", "Identity"),

  LOCAL_AUTH_LIST("LocalAuthListEnabled", "LocalAuthListEnabled", "LocalAuthListEnabled"),

  STATION_MAX_CURRENT("Station-MaxCurrent", "Station-MaxCurrent", "Station-MaxCurrent"),

  STATION_PASSWORD("PW-SetChargerPassword", "PW-SetChargerPassword", "PW-SetChargerPassword");

  private final String firmwareV4KeyName;
  private final String firmwareV5KeyName;
  private final String firmwareV6KeyName;

  /**
   * Enum's constructor. Should be private and not called outside this enum.
   *
   * @param firmwareV4KeyName The key's name in the firmware > 4.*.
   * @param firmwareV5KeyName The key's name in the firmware > 5.*.
   * @param firmwareV6KeyName The key's name in the firmware > 6.*.
   */
  FirmwareKey(String firmwareV4KeyName, String firmwareV5KeyName, String firmwareV6KeyName) {
    this.firmwareV4KeyName = Objects.requireNonNull(firmwareV4KeyName);
    this.firmwareV5KeyName = Objects.requireNonNull(firmwareV5KeyName);
    this.firmwareV6KeyName = Objects.requireNonNull(firmwareV6KeyName);
  }

  /**
   * According to the firmware version, returns the correct key's name.
   *
   * @param firmwareVersion The firmware version of the chargepoint.
   * @return The correct key's name.
   */
  public String getFirmwareKeyAccordingToVersion(String firmwareVersion) {
    var majorVersion = firmwareVersion.split("\\.")[0];
    return switch (majorVersion) {
      case "4" -> firmwareV4KeyName;
      case "5" -> firmwareV5KeyName;
      case "6" -> firmwareV6KeyName;
      default -> throw new IllegalStateException("Unknown firmware");
    };
  }

  /**
   * According to the firmware version, returns the correct value's name.
   *
   * @param firmwareVersion The firmware version of the chargepoint.
   * @return The correct value's name.
   */
  public String getValueFormatAccordingToVersion(String firmwareVersion) {
    var majorVersion = firmwareVersion.split("\\.")[0];
    return switch (majorVersion) {
      case "4", "5" -> "%s";
      case "6" -> "ocppVersion{OCPP16}ocppCsmsUrl{%s}"
          + "messageTimeout{10}securityProfile{0}ocppInterface{Wired0}";
      default -> throw new IllegalStateException("Unknown firmware");
    };
  }
}
