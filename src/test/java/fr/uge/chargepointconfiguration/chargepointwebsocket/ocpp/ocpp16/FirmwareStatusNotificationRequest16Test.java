package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.FirmwareStatus;
import org.junit.jupiter.api.Test;

/**
 * JUnit test class for the {@link FirmwareStatusNotificationRequest16}.
 */
class FirmwareStatusNotificationRequest16Test {

  /**
   * Should not throw an exception when instantiating the record.
   */
  @Test
  public void correctConstructorShouldNotThrowException() {
    assertDoesNotThrow(() -> {
      new FirmwareStatusNotificationRequest16(FirmwareStatus.Installed);
    });
  }

  /**
   * Should return the correct status.
   */
  @Test
  public void returnsCorrectStatus() {
    var test = new FirmwareStatusNotificationRequest16(FirmwareStatus.Installed);
    assertEquals(FirmwareStatus.Installed, test.status());
  }

  /**
   * Should throw a {@link NullPointerException} if the status is null.
   */
  @Test
  public void throwsExceptionIfStatusIsNull() {
    assertThrows(NullPointerException.class, () -> new FirmwareStatusNotificationRequest16(null));
  }
}
