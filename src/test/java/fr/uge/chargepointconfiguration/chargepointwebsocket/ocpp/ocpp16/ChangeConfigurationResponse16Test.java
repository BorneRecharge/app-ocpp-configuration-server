package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.ConfigurationStatus;
import org.junit.jupiter.api.Test;

/**
 * JUnit test class for the {@link ChangeConfigurationResponse16}.
 */
class ChangeConfigurationResponse16Test {

  /**
   * Should not throw an exception when instantiating the record.
   */
  @Test
  public void correctConstructorShouldNotThrowException() {
    assertDoesNotThrow(() -> {
      new ChangeConfigurationResponse16(ConfigurationStatus.Accepted);
    });
  }

  /**
   * Should return the correct status.
   */
  @Test
  public void returnsCorrectStatus() {
    var test = new ChangeConfigurationResponse16(ConfigurationStatus.Accepted);
    assertEquals(ConfigurationStatus.Accepted, test.status());
  }

  /**
   * Should throw a {@link NullPointerException} if the status is null.
   */
  @Test
  public void throwsExceptionIfStatusIsNull() {
    assertThrows(NullPointerException.class, () -> new ChangeConfigurationResponse16(null));
  }
}
