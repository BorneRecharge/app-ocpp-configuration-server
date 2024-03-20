package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * JUnit test class for the {@link UpdateFirmwareRequest16}.
 */
class UpdateFirmwareRequest16Test {

  /**
   * Should not throw an exception when instantiating the record.
   */
  @Test
  public void correctConstructorShouldNotThrowException() {
    assertDoesNotThrow(() -> {
      new UpdateFirmwareRequest16("location", Instant.now());
    });
  }

  /**
   * Should return the correct location.
   */
  @Test
  public void returnsCorrectLocation() {
    var test = new UpdateFirmwareRequest16("location", Instant.now());
    assertEquals("location", test.location());
  }

  /**
   * Should return the correct retrieve date.
   */
  @Test
  public void returnsCorrectRetrieveDate() {
    var date = Instant.now();
    var test = new UpdateFirmwareRequest16("location", date);
    assertEquals(date, test.retrieveDate());
  }

  /**
   * Should throw a {@link NullPointerException} if the location is null.
   */
  @Test
  public void throwsExceptionIfLocationIsNull() {
    assertThrows(
        NullPointerException.class, () -> new UpdateFirmwareRequest16(null, Instant.now()));
  }

  /**
   * Should throw a {@link NullPointerException} if the retrieve date is null.
   */
  @Test
  public void throwsExceptionIfRetrieveDateIsNull() {
    assertThrows(NullPointerException.class, () -> new UpdateFirmwareRequest16("location", null));
  }
}
