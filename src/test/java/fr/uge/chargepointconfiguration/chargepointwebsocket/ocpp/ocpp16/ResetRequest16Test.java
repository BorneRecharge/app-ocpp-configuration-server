package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.ResetType;
import org.junit.jupiter.api.Test;

/**
 * JUnit test class for the {@link ResetRequest16}.
 */
class ResetRequest16Test {

  /**
   * Should not throw an exception when instantiating the record.
   */
  @Test
  public void correctConstructorShouldNotThrowException() {
    assertDoesNotThrow(() -> {
      new ResetRequest16(ResetType.Hard);
    });
  }

  /**
   * Should return the correct type.
   */
  @Test
  public void returnsCorrectType() {
    var test = new ResetRequest16(ResetType.Hard);
    assertEquals(ResetType.Hard, test.type());
  }

  /**
   * Should throw a {@link NullPointerException} if the type is null.
   */
  @Test
  public void throwsExceptionIfTypeIsNull() {
    assertThrows(NullPointerException.class, () -> new ResetRequest16(null));
  }
}
