package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * JUnit test class for the {@link BootNotificationRequest16}.
 */
class BootNotificationRequest16Test {

  /**
   * Should not throw an exception when instantiating the record.
   */
  @Test
  public void correctConstructorShouldNotThrowException() {
    assertDoesNotThrow(() -> {
      new BootNotificationRequest16(
          "chargePointVendor",
          "chargePointModel",
          "chargePointSerialNumber",
          "chargeBoxSerialNumber",
          "firmwareVersion");
    });
  }

  /**
   * Should return the correct charge point vendor.
   */
  @Test
  public void returnsCorrectChargepointVendor() {
    var test = new BootNotificationRequest16(
        "chargePointVendor",
        "chargePointModel",
        "chargePointSerialNumber",
        "chargeBoxSerialNumber",
        "firmwareVersion");
    assertEquals("chargePointVendor", test.chargePointVendor());
  }

  /**
   * Should return the correct charge point model.
   */
  @Test
  public void returnsCorrectChargepointModel() {
    var test = new BootNotificationRequest16(
        "chargePointVendor",
        "chargePointModel",
        "chargePointSerialNumber",
        "chargeBoxSerialNumber",
        "firmwareVersion");
    assertEquals("chargePointModel", test.chargePointModel());
  }

  /**
   * Should return the correct charge point serial number.
   */
  @Test
  public void returnsCorrectChargepointSerialNumber() {
    var test = new BootNotificationRequest16(
        "chargePointVendor",
        "chargePointModel",
        "chargePointSerialNumber",
        "chargeBoxSerialNumber",
        "firmwareVersion");
    assertEquals("chargePointSerialNumber", test.chargePointSerialNumber());
  }

  /**
   * Should return the correct charge point serial number even if it is null.
   */
  @Test
  public void returnsCorrectChargepointSerialNumberEvenIfNull() {
    var test = new BootNotificationRequest16(
        "chargePointVendor", "chargePointModel", null, "chargeBoxSerialNumber", "firmwareVersion");
    assertNull(test.chargePointSerialNumber());
  }

  /**
   * Should return the correct charge point box serial number.
   */
  @Test
  public void returnsCorrectChargepointBoxSerialNumber() {
    var test = new BootNotificationRequest16(
        "chargePointVendor",
        "chargePointModel",
        "chargePointSerialNumber",
        "chargeBoxSerialNumber",
        "firmwareVersion");
    assertEquals("chargeBoxSerialNumber", test.chargeBoxSerialNumber());
  }

  /**
   * Should return the correct charge point box serial number even if it is null.
   */
  @Test
  public void returnsCorrectChargepointBoxSerialNumberEvenIfNull() {
    var test = new BootNotificationRequest16(
        "chargePointVendor",
        "chargePointModel",
        "chargePointSerialNumber",
        null,
        "firmwareVersion");
    assertNull(test.chargeBoxSerialNumber());
  }

  /**
   * Should return the correct firmware version.
   */
  @Test
  public void returnsCorrectChargepointFirmwareVersion() {
    var test = new BootNotificationRequest16(
        "chargePointVendor",
        "chargePointModel",
        "chargePointSerialNumber",
        "chargeBoxSerialNumber",
        "firmwareVersion");
    assertEquals("firmwareVersion", test.firmwareVersion());
  }

  /**
   * Should return the correct firmware version even if it is null.
   */
  @Test
  public void returnsCorrectChargepointFirmwareVersionEvenIfNull() {
    var test = new BootNotificationRequest16(
        "chargePointVendor",
        "chargePointModel",
        "chargePointSerialNumber",
        "chargeBoxSerialNumber",
        null);
    assertNull(test.firmwareVersion());
  }

  /**
   * Should throw an {@link NullPointerException} if the charge point vendor is null.
   */
  @Test
  public void throwsExceptionIfVendorIsNull() {
    assertThrows(
        NullPointerException.class,
        () -> new BootNotificationRequest16(
            null,
            "chargePointModel",
            "chargePointSerialNumber",
            "chargeBoxSerialNumber",
            "firmwareVersion"));
  }

  /**
   * Should throw an {@link NullPointerException} if the charge point model is null.
   */
  @Test
  public void throwsExceptionIfModelIsNull() {
    assertThrows(
        NullPointerException.class,
        () -> new BootNotificationRequest16(
            "chargePointVendor",
            null,
            "chargePointSerialNumber",
            "chargeBoxSerialNumber",
            "firmwareVersion"));
  }

  /**
   * Should not throw an exception if the charge point serial number is null.
   */
  @Test
  public void doesNotThrowExceptionIfSerialNumberIsNull() {
    assertDoesNotThrow(() -> new BootNotificationRequest16(
        "chargePointVendor", "chargePointModel", null, "chargeBoxSerialNumber", "firmwareVersion"));
  }

  /**
   * Should not throw an exception if the charge point box serial number is null.
   */
  @Test
  public void doesNotThrowExceptionIfBoxSerialNumberIsNull() {
    assertDoesNotThrow(() -> new BootNotificationRequest16(
        "chargePointVendor",
        "chargePointModel",
        "chargePointSerialNumber",
        null,
        "firmwareVersion"));
  }

  /**
   * Should not throw an exception if the charge point firmware version is null.
   */
  @Test
  public void doesNotThrowExceptionIfFirmwareVersionIsNull() {
    assertDoesNotThrow(() -> new BootNotificationRequest16(
        "chargePointVendor",
        "chargePointModel",
        "chargePointSerialNumber",
        "chargeBoxSerialNumber",
        null));
  }

  /**
   * Should throw an {@link IllegalArgumentException} if the vendor is longer
   * than 20 characters.
   */
  @Test
  public void throwsExceptionIfVendorIsIncorrect() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BootNotificationRequest16(
            "More than 20 characters here",
            "chargePointModel",
            "chargePointSerialNumber",
            "chargeBoxSerialNumber",
            "firmwareVersion"));
  }

  /**
   * Should throw an {@link IllegalArgumentException} if the model is longer
   * than 20 characters.
   */
  @Test
  public void throwsExceptionIfModelIsIncorrect() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BootNotificationRequest16(
            "chargePointVendor",
            "More than 20 characters here",
            "chargePointSerialNumber",
            "chargeBoxSerialNumber",
            "firmwareVersion"));
  }

  /**
   * Should throw an {@link IllegalArgumentException} if the serial number is longer
   * than 20 characters.
   */
  @Test
  public void throwsExceptionIfSerialNumberIsIncorrect() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BootNotificationRequest16(
            "chargePointVendor",
            "chargePointModel",
            "More than 25 characters here",
            "chargeBoxSerialNumber",
            "firmwareVersion"));
  }

  /**
   * Should throw an {@link IllegalArgumentException} if the box serial number is longer
   * than 20 characters.
   */
  @Test
  public void throwsExceptionIfBoxSerialNumberIsIncorrect() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BootNotificationRequest16(
            "chargePointVendor",
            "chargePointModel",
            "chargePointSerialNumber",
            "More than 25 characters here",
            "firmwareVersion"));
  }

  /**
   * Should throw an {@link IllegalArgumentException} if the firmware version is longer
   * than 50 characters.
   */
  @Test
  public void throwsExceptionIfFirmwareVersionIsIncorrect() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BootNotificationRequest16(
            "chargePointVendor",
            "chargePointModel",
            "chargePointSerialNumber",
            "chargeBoxSerialNumber",
            "More than 50 characters in this field, so it should not work !"));
  }
}
