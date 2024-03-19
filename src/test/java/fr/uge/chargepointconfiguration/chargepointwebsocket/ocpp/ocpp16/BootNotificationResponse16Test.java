package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.RegistrationStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test class for {@link BootNotificationResponse16}.
 */
class BootNotificationResponse16Test {

  /**
   * Should not throw an exception when instantiating the record.
   */
  @Test
  public void correctConstructorShouldNotThrowException() {
    assertDoesNotThrow(() -> {
      new BootNotificationResponse16(
              Instant.now(),
              1,
              RegistrationStatus.Accepted
      );
    });
  }

  /**
   * Should return the correct current time.
   */
  @Test
  public void returnsCorrectTime() {
    var currentTime = Instant.now();
    var test = new BootNotificationResponse16(
            currentTime,
            1,
            RegistrationStatus.Accepted
    );
    assertEquals(currentTime, test.currentTime());
  }

  /**
   * Should return the correct interval.
   */
  @Test
  public void returnsCorrectInterval() {
    var test = new BootNotificationResponse16(
            Instant.now(),
            1,
            RegistrationStatus.Accepted
    );
    assertEquals(1, test.interval());
  }

  /**
   * Should return the correct status.
   */
  @Test
  public void returnsCorrectStatus() {
    var test = new BootNotificationResponse16(
            Instant.now(),
            1,
            RegistrationStatus.Accepted
    );
    assertEquals(RegistrationStatus.Accepted, test.status());
  }

  /**
   * Should throw a {@link NullPointerException} if the time is null.
   */
  @Test
  public void throwsExceptionIfTimeIsNull() {
    assertThrows(NullPointerException.class, () -> new BootNotificationResponse16(
            null,
            1,
            RegistrationStatus.Accepted
    ));
  }

  /**
   * Should throw a {@link NullPointerException} if the status is null.
   */
  @Test
  public void throwsExceptionIfStatusIsNull() {
    assertThrows(NullPointerException.class, () -> new BootNotificationResponse16(
            Instant.now(),
            1,
            null
    ));
  }
}